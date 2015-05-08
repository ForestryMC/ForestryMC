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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpeciesCustom;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeIconProvider;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;

public class AlleleBeeSpecies extends AlleleSpecies implements IAlleleBeeSpeciesCustom {

	private final Map<ItemStack, Integer> products = new HashMap<ItemStack, Integer>();
	private final Map<ItemStack, Integer> specialty = new HashMap<ItemStack, Integer>();

	private final int primaryColour;
	private final int secondaryColour;

	private IBeeIconProvider beeIconProvider;
	private IJubilanceProvider jubilanceProvider;

	public AlleleBeeSpecies(String uid, String authority, String unlocalizedDescription, boolean dominant, String name, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(uid, authority, unlocalizedDescription, dominant, name, branch, binomial, false);

		this.primaryColour = primaryColor;
		this.secondaryColour = secondaryColor;

		setCustomBeeIconProvider(DefaultBeeIconProvider.getInstance());
		setJubilanceProvider(JubilanceDefault.getInstance());
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public IAlleleBeeSpeciesCustom addProduct(ItemStack product, int chance) {
		if (product == null || product.getItem() == null) {
			throw new IllegalArgumentException("Tried to add null product");
		}
		this.products.put(product, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom addSpecialty(ItemStack specialty, int chance) {
		if (specialty == null || specialty.getItem() == null) {
			throw new IllegalArgumentException("Tried to add null specialty");
		}
		this.specialty.put(specialty, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom setCustomBeeIconProvider(IBeeIconProvider beeIconProvider) {
		this.beeIconProvider = beeIconProvider;
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

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack == null) {
			return 0f;
		}

		for (ItemStack stack : products.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}
		for (ItemStack stack : specialty.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}

		return super.getResearchSuitability(itemstack);
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		ArrayList<ItemStack> bounty = new ArrayList<ItemStack>();
		Collections.addAll(bounty, super.getResearchBounty(world, researcher, individual, bountyLevel));

		if (bountyLevel > 10) {
			for (ItemStack stack : specialty.keySet()) {
				bounty.add(StackUtils.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
			}
		}
		for (ItemStack stack : products.keySet()) {
			bounty.add(StackUtils.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
		}
		return bounty.toArray(new ItemStack[bounty.size()]);
	}

	/* OTHER */
	@Override
	public boolean isNocturnal() {
		return false;
	}

	@Override
	public Map<ItemStack, Integer> getProducts() {
		return products;
	}

	@Override
	public Map<ItemStack, Integer> getSpecialty() {
		return specialty;
	}

	@Override
	public boolean isJubilant(IBeeGenome genome, IBeeHousing housing) {
		return jubilanceProvider.isJubilant(this, genome, housing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return new BeeIconProviderWrapper(beeIconProvider);
	}

	@Override
	public IIcon getIcon(EnumBeeType type, int renderPass) {
		return beeIconProvider.getIcon(type, renderPass);
	}

	@Override
	public int getIconColour(int renderPass) {
		if (renderPass == 0) {
			return primaryColour;
		}
		if (renderPass == 1) {
			return secondaryColour;
		}
		return 0xffffff;
	}

	@Override
	public String getEntityTexture() {
		return null;
	}

	private static class DefaultBeeIconProvider implements IBeeIconProvider {

		private static final String iconType = "default";
		private static DefaultBeeIconProvider instance;

		public static DefaultBeeIconProvider getInstance() {
			if (instance == null) {
				instance = new DefaultBeeIconProvider();
			}
			return instance;
		}

		private DefaultBeeIconProvider() {

		}

		private static final IIcon[][] icons = new IIcon[EnumBeeType.values().length][3];

		@SideOnly(Side.CLIENT)
		@Override
		public void registerIcons(IIconRegister register) {
			IIcon body1 = TextureManager.getInstance().registerTex(register, "bees/" + iconType + "/body1");

			for (int i = 0; i < EnumBeeType.values().length; i++) {
				if (EnumBeeType.values()[i] == EnumBeeType.NONE) {
					continue;
				}

				icons[i][0] = TextureManager.getInstance().registerTex(register, "bees/" + iconType + '/' + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".outline");
				icons[i][1] = (EnumBeeType.values()[i] != EnumBeeType.LARVAE) ? body1
						: TextureManager.getInstance().registerTex(register, "bees/" + iconType + '/' + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body");
				icons[i][2] = TextureManager.getInstance().registerTex(register, "bees/" + iconType + '/' + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body2");
			}
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IIcon getIcon(EnumBeeType type, int renderPass) {
			return icons[type.ordinal()][renderPass];
		}
	}

	private static class BeeIconProviderWrapper implements IIconProvider {

		private final IBeeIconProvider beeIconProvider;

		public BeeIconProviderWrapper(IBeeIconProvider beeIconProvider) {
			this.beeIconProvider = beeIconProvider;
		}

		@Override
		public IIcon getIcon(short texUID) {
			return null;
		}

		@Override
		public void registerIcons(IIconRegister register) {
			beeIconProvider.registerIcons(register);
		}
	}
}
