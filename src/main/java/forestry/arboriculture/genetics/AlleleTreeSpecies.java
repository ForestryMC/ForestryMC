/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.genetics;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraftforge.common.EnumPlantType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IMutation;
import forestry.api.world.ITreeGenData;
import forestry.arboriculture.worldgen.WorldGenArboriculture;
import forestry.arboriculture.worldgen.WorldGenBalsa;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.render.TextureManager;
import forestry.core.utils.Utils;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpecies, IIconProvider {

	private static class LeafType {
		public final String ident;
		public final short fancyUID;
		public final short plainUID;
		public final short changedUID;

		public LeafType(String ident, short fancyUID, short plainUID, short changedUID) {
			this.ident = ident;
			this.fancyUID = fancyUID;
			this.plainUID = plainUID;
			this.changedUID = changedUID;
		}
	}

	private static final HashMap<String, LeafType> leafTypes = new HashMap<String, LeafType>();

	static {
		leafTypes.put("deciduous", new LeafType("deciduous", (short) 10, (short) 11, (short) 12));
		leafTypes.put("conifers", new LeafType("conifers", (short) 15, (short) 16, (short) 17));
		leafTypes.put("jungle", new LeafType("jungle", (short) 20, (short) 21, (short) 22));
		leafTypes.put("willow", new LeafType("willow", (short) 25, (short) 26, (short) 27));
		leafTypes.put("maple", new LeafType("maple", (short) 30, (short) 31, (short) 32));
		leafTypes.put("palm", new LeafType("palm", (short) 35, (short) 36, (short) 37));
	}

	private final ITreeRoot root;

	private Class<? extends WorldGenArboriculture> generatorClass = WorldGenBalsa.class;

	private final int primaryColour;
	//private final int secondaryColour;

	private LeafType leafType;

	private int girth = 1;
	private EnumPlantType nativeType = EnumPlantType.Plains;
	private final ArrayList<IFruitFamily> fruits = new ArrayList<IFruitFamily>();

	private final ItemStack wood;

	public AlleleTreeSpecies(String uid, boolean isDominant, String speciesName, IClassification branch, String binomial, int primaryColor,
			Class<? extends WorldGenArboriculture> generator, ItemStack wood) {
		this(uid, isDominant, speciesName, branch, binomial, primaryColor, Utils.multiplyRGBComponents(primaryColor, 1.35f), generator, wood);
	}

	public AlleleTreeSpecies(String uid, boolean isDominant, String speciesName, IClassification branch, String binomial, int primaryColor, int secondaryColor,
			Class<? extends WorldGenArboriculture> generator, ItemStack wood) {
		super("forestry." + uid, "for.trees.species." + speciesName, "Sengir", "for.description." + uid, isDominant, branch, binomial, true);

		this.root = (ITreeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootTrees");
		this.generatorClass = generator;
		this.primaryColour = primaryColor;
		//this.secondaryColour = secondaryColor;
		leafType = leafTypes.get("deciduous");
		this.wood = wood;
	}

	@Override
	public ITreeRoot getRoot() {
		return root;
	}

	public AlleleTreeSpecies setPlantType(EnumPlantType type) {
		this.nativeType = type;
		return this;
	}

	public AlleleTreeSpecies setGirth(int girth) {
		this.girth = girth;
		return this;
	}

	public AlleleTreeSpecies addFruitFamily(IFruitFamily family) {
		fruits.add(family);
		return this;
	}

	public AlleleTreeSpecies setLeafIndices(String ident) {
		leafType = leafTypes.get(ident);
		return this;
	}

	public AlleleTreeSpecies setVanillaMap(int vanillaMeta) {
		vanillaMap = vanillaMeta;
		return this;
	}

	/* RESEARCH */
	@Override
	public int getComplexity() {
		return 1 + getGeneticAdvancement(this, new ArrayList<IAllele>());
	}

	private int getGeneticAdvancement(IAllele species, ArrayList<IAllele> exclude) {

		int own = 1;
		int highest = 0;
		exclude.add(species);

		for (IMutation mutation : getRoot().getPaths(species, EnumBeeChromosome.SPECIES)) {
			if (!exclude.contains(mutation.getAllele0())) {
				int otherAdvance = getGeneticAdvancement(mutation.getAllele0(), exclude);
				if (otherAdvance > highest) {
					highest = otherAdvance;
				}
			}
			if (!exclude.contains(mutation.getAllele1())) {
				int otherAdvance = getGeneticAdvancement(mutation.getAllele1(), exclude);
				if (otherAdvance > highest) {
					highest = otherAdvance;
				}
			}
		}

		return own + (highest < 0 ? 0 : highest);
	}

	/* OTHER */
	@Override
	public EnumPlantType getPlantType() {
		return nativeType;
	}

	@Override
	public ArrayList<IFruitFamily> getSuitableFruit() {
		return fruits;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public WorldGenerator getGenerator(ITree tree, World world, int x, int y, int z) {
		try {
			return generatorClass.getConstructor(new Class[]{ITreeGenData.class}).newInstance(tree);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to instantiate generator of class " + generatorClass.getName());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Class<? extends WorldGenerator>[] getGeneratorClasses() {
		return new Class[]{generatorClass};
	}

	@Override
	public short getLeafIconIndex(ITree tree, boolean fancy) {

		if (!fancy) {
			return leafType.plainUID;
		}

		if (tree.getMate() != null) {
			return leafType.changedUID;
		}

		return leafType.fancyUID;
	}

	public AlleleTreeSpecies setGenerator(Class<? extends WorldGenArboriculture> generatorClass) {
		this.generatorClass = generatorClass;
		return this;
	}

	@Override
	public int getLeafColour(ITree tree) {
		//if(tree == null)
		//	return primaryColour;

		//if (tree.getMate() != null)
		//	return secondaryColour;
		//else
		return primaryColour;
	}

	/* ICONS */
	private int vanillaMap = -1;

	@Override
	public int getIconColour(int renderPass) {
		if (renderPass == 0) {
			return primaryColour;
		}
		return 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	private IIcon icon;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		if (vanillaMap < 0) {
			String name = uid.substring("forestry.".length());
			icon = TextureManager.getInstance().registerTex(register, "germlings/sapling." + name);
		} else {
			icon = Blocks.sapling.getIcon(0, vanillaMap);
		}
		TextureManager.getInstance().registerTexUID(register, leafType.plainUID, "leaves/" + leafType.ident + ".plain");
		TextureManager.getInstance().registerTexUID(register, leafType.changedUID, "leaves/" + leafType.ident + ".changed");
		TextureManager.getInstance().registerTexUID(register, leafType.fancyUID, "leaves/" + leafType.ident + ".fancy");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getGermlingIcon(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.POLLEN) {
			return ForestryItem.pollenCluster.item().getIconFromDamageForRenderPass(0, renderPass);
		}

		return icon;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		if (type == EnumGermlingType.SAPLING) {
			return 0xFFFFFF;
		}
		return getLeafColour(null);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(short texUID) {
		return TextureManager.getInstance().getIcon(texUID);
	}

	@Override
	public ItemStack[] getLogStacks() {
		return new ItemStack[]{wood};
	}

}
