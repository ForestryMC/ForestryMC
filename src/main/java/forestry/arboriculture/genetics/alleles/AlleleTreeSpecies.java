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
package forestry.arboriculture.genetics.alleles;

import java.util.ArrayList;
import java.util.List;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IAlleleTreeSpeciesBuilder;
import forestry.api.arboriculture.IGermlingModelProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ILeafProvider;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.api.arboriculture.ITreeGenerator;
import forestry.api.arboriculture.ITreeRoot;
import forestry.api.arboriculture.IWoodProvider;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IFruitFamily;
import forestry.arboriculture.genetics.ClimateGrowthProvider;
import forestry.arboriculture.genetics.LeafProvider;
import forestry.core.genetics.alleles.AlleleSpecies;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AlleleTreeSpecies extends AlleleSpecies implements IAlleleTreeSpeciesBuilder, IAlleleTreeSpecies {
	private final ITreeGenerator generator;
	private final IWoodProvider woodProvider;
	private final IGermlingModelProvider germlingModelProvider;
	private final ILeafSpriteProvider leafSpriteProvider;
	private final List<IFruitFamily> fruits = new ArrayList<>();
	private final String modID;
	private EnumPlantType nativeType = EnumPlantType.Plains;
	private final ILeafProvider leafProvider;
	private IGrowthProvider growthProvider = new ClimateGrowthProvider();
	private float rarity = 0.0F;

	public AlleleTreeSpecies(
			String uid,
			String unlocalizedName,
			String authority,
			String unlocalizedDescription,
			boolean isDominant,
			IClassification branch,
			String binomial,
			String modID,
			ILeafSpriteProvider leafSpriteProvider,
			IGermlingModelProvider germlingModelProvider,
			IWoodProvider woodProvider,
			ITreeGenerator generator) {
		this(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial, modID, leafSpriteProvider, germlingModelProvider, woodProvider, generator, new LeafProvider());
	}
	
	public AlleleTreeSpecies(
			String uid,
			String unlocalizedName,
			String authority,
			String unlocalizedDescription,
			boolean isDominant,
			IClassification branch,
			String binomial,
			String modID,
			ILeafSpriteProvider leafIconProvider,
			IGermlingModelProvider germlingModelProvider,
			IWoodProvider woodProvider,
			ITreeGenerator generator,
			ILeafProvider leafProvider) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);

		this.generator = generator;
		this.germlingModelProvider = germlingModelProvider;
		this.woodProvider = woodProvider;
		this.leafSpriteProvider = leafIconProvider;
		this.leafProvider = leafProvider;

		this.modID = modID;
	}

	@Override
	public IAlleleTreeSpecies build() {
		AlleleManager.alleleRegistry.registerAllele(this, EnumTreeChromosome.SPECIES);
		leafProvider.init(this);
		return this;
	}

	@Override
	public ITreeRoot getRoot() {
		return TreeManager.treeRoot;
	}

	@Override
	public AlleleTreeSpecies setPlantType(EnumPlantType type) {
		nativeType = type;
		return this;
	}

	@Override
	public AlleleTreeSpecies addFruitFamily(IFruitFamily family) {
		fruits.add(family);
		return this;
	}
	
	@Override
	public IAlleleTreeSpeciesBuilder setRarity(float rarity) {
		this.rarity = rarity;
		return this;
	}
	
	@Override
	public float getRarity() {
		return rarity;
	}
	
	@Override
	public IAlleleTreeSpeciesBuilder setGrowthProvider(IGrowthProvider growthProvider) {
		this.growthProvider = growthProvider;
		return this;
	}
	
	@Override
	public IGrowthProvider getGrowthProvider() {
		return growthProvider;
	}

	/* OTHER */
	@Override
	public EnumPlantType getPlantType() {
		return nativeType;
	}

	@Override
	public List<IFruitFamily> getSuitableFruit() {
		return fruits;
	}

	@Override
	public ITreeGenerator getGenerator() {
		return generator;
	}

	@Override
	public ILeafSpriteProvider getLeafSpriteProvider() {
		return leafSpriteProvider;
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return leafSpriteProvider.getColor(false);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelResourceLocation getGermlingModel(EnumGermlingType type) {
		return germlingModelProvider.getModel(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getGermlingColour(EnumGermlingType type, int renderPass) {
		return germlingModelProvider.getSpriteColor(type, renderPass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager, EnumGermlingType type) {
		germlingModelProvider.registerModels(item, manager, type);
	}

	@Override
	public IWoodProvider getWoodProvider() {
		return woodProvider;
	}

	@Override
	public ILeafProvider getLeafProvider() {
		return leafProvider;
	}
	
	@Override
	public String getModID() {
		return modID;
	}

	@Override
	public int compareTo(IAlleleTreeSpecies o) {
		return 0;
	}
}
