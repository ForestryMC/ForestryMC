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
package forestry.apiculture.genetics;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.config.Defaults;
import forestry.core.genetics.AlleleSpecies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class AlleleBeeSpecies extends AlleleSpecies implements IAlleleBeeSpecies, IIconProvider {

	public IJubilanceProvider jubilanceProvider;

	private final IBeeRoot root;

	private final HashMap<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
	private final HashMap<ItemStack, Integer> specialty = new HashMap<ItemStack, Integer>();

	private String texture;
	private final int primaryColour;
	private final int secondaryColour;

	private final String iconType = "default";

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, int primaryColor, int secondaryColor) {
		this(uid, dominant, name, branch, null, primaryColor, secondaryColor);
	}

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		this(uid, dominant, name, branch, binomial, primaryColor, secondaryColor, new JubilanceDefault());
	}

	public AlleleBeeSpecies(String uid, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor,
			IJubilanceProvider jubilanceProvider) {
		super(uid, dominant, name, branch, binomial);

		this.root = (IBeeRoot)AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
		this.primaryColour = primaryColor;
		this.secondaryColour = secondaryColor;
		this.jubilanceProvider = jubilanceProvider;
		texture = Defaults.TEXTURE_PATH_ENTITIES + "/bees/honeyBee.png";
	}

	@Override
	public IBeeRoot getRoot() {
		return root;
	}

	public AlleleBeeSpecies setEntityTexture(String texture) {
		this.texture = Defaults.TEXTURE_PATH_ENTITIES + "/bees/" + texture + ".png";
		return this;
	}

	public AlleleBeeSpecies addProduct(ItemStack product, int chance) {
		if (product == null || product.getItem() == null)
			throw new IllegalArgumentException("Tried to add null product");
		this.products.put(product, chance);
		return this;
	}

	public AlleleBeeSpecies addSpecialty(ItemStack specialty, int chance) {
		this.specialty.put(specialty, chance);
		return this;
	}

	public AlleleBeeSpecies setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
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

		for(IMutation mutation : getRoot().getPaths(species, EnumBeeChromosome.SPECIES)) {
			if(!exclude.contains(mutation.getAllele0())) {
				int otherAdvance = getGeneticAdvancement(mutation.getAllele0(), exclude);
				if(otherAdvance > highest)
					highest = otherAdvance;
			}
			if(!exclude.contains(mutation.getAllele1())) {
				int otherAdvance = getGeneticAdvancement(mutation.getAllele1(), exclude);
				if(otherAdvance > highest)
					highest = otherAdvance;
			}
		}

		return own + (highest < 0 ? 0 : highest);
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if(itemstack == null)
			return 0f;

		for(ItemStack stack : products.keySet())
			if(stack.isItemEqual(itemstack))
				return 1.0f;
		for(ItemStack stack : specialty.keySet())
			if(stack.isItemEqual(itemstack))
				return 1.0f;

		return super.getResearchSuitability(itemstack);
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		ArrayList<ItemStack> bounty = new ArrayList<ItemStack>();
		Collections.addAll(bounty, super.getResearchBounty(world, researcher, individual, bountyLevel));

		if(bountyLevel > 10) {
			for(ItemStack stack : specialty.keySet()) {
				bounty.add(StackUtils.copyWithRandomSize(stack, (int)((float)bountyLevel / 2), world.rand));
			}
		}
		for(ItemStack stack : products.keySet()) {
			bounty.add(StackUtils.copyWithRandomSize(stack, (int)((float)bountyLevel / 2), world.rand));
		}
		return bounty.toArray(new ItemStack[bounty.size()]);
	}

	/* OTHER */
	@Override
	public boolean isNocturnal() {
		return false;
	}

	@Override
	public HashMap<ItemStack, Integer> getProducts() {
		return products;
	}

	@Override
	public HashMap<ItemStack, Integer> getSpecialty() {
		return specialty;
	}

	@Override
	public boolean isJubilant(IBeeGenome genome, IBeeHousing housing) {
		return jubilanceProvider.isJubilant(this, genome, housing);
	}

	@Override
	public String getEntityTexture() {
		return texture;
	}

	@Override
	public int getIconColour(int renderPass) {
		if (renderPass == 0)
			return primaryColour;
		if (renderPass == 1)
			return secondaryColour;
		return 0xffffff;
	}

	@SideOnly(Side.CLIENT)
	private static IIcon[][] icons;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister register) {
		icons = new IIcon[EnumBeeType.values().length][3];

		IIcon body1 = TextureManager.getInstance().registerTex(register, "bees/" + iconType + "/body1");

		for (int i = 0; i < EnumBeeType.values().length; i++) {
			if(EnumBeeType.values()[i] == EnumBeeType.NONE)
				continue;

			icons[i][0] = TextureManager.getInstance().registerTex(register, "bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".outline");
			icons[i][1] = (EnumBeeType.values()[i] != EnumBeeType.LARVAE) ? body1
					: TextureManager.getInstance().registerTex(register, "bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body");
			icons[i][2] = TextureManager.getInstance().registerTex(register, "bees/" + iconType + "/" + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body2");
		}

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(EnumBeeType type, int renderPass) {
		return icons[type.ordinal()][renderPass];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(short texUID) {
		return null;
	}

}
