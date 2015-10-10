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
package forestry.apiculture.genetics.alleles;

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
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.genetics.JubilanceDefault;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.render.TextureManager;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.ItemStackUtil;

public class AlleleBeeSpecies extends AlleleSpecies implements IAlleleBeeSpeciesCustom {
	private final Map<ItemStack, Float> productChances = new HashMap<>();
	private final Map<ItemStack, Float> specialtyChances = new HashMap<>();

	private final int primaryColour;
	private final int secondaryColour;

	private IBeeIconProvider beeIconProvider = DefaultBeeIconProvider.instance;
	private IJubilanceProvider jubilanceProvider = JubilanceDefault.instance;
	private boolean nocturnal = false;

	public AlleleBeeSpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial, false);

		this.primaryColour = primaryColor;
		this.secondaryColour = secondaryColor;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public IAlleleBeeSpeciesCustom addProduct(ItemStack product, Float chance) {
		if (product == null || product.getItem() == null) {
			throw new IllegalArgumentException("Tried to add null product");
		}
		if (chance <= 0.0f || chance > 1.0f) {
			throw new IllegalArgumentException("chance must be in the range (0, 1]");
		}
		this.productChances.put(product, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom addSpecialty(ItemStack specialty, Float chance) {
		if (specialty == null || specialty.getItem() == null) {
			throw new IllegalArgumentException("Tried to add null specialty");
		}
		if (chance <= 0.0f || chance > 1.0f) {
			throw new IllegalArgumentException("chance must be in the range (0, 1]");
		}
		this.specialtyChances.put(specialty, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesCustom setNocturnal() {
		nocturnal = true;
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
		return GeneticsUtil.getResearchComplexity(this, EnumBeeChromosome.SPECIES);
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack == null) {
			return 0f;
		}

		for (ItemStack stack : productChances.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}
		for (ItemStack stack : specialtyChances.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}

		return super.getResearchSuitability(itemstack);
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		ArrayList<ItemStack> bounty = new ArrayList<>();
		Collections.addAll(bounty, super.getResearchBounty(world, researcher, individual, bountyLevel));

		if (bountyLevel > 10) {
			for (ItemStack stack : specialtyChances.keySet()) {
				bounty.add(ItemStackUtil.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
			}
		}
		for (ItemStack stack : productChances.keySet()) {
			bounty.add(ItemStackUtil.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
		}
		return bounty.toArray(new ItemStack[bounty.size()]);
	}

	/* OTHER */
	@Override
	public boolean isNocturnal() {
		return nocturnal;
	}

	@Override
	public Map<ItemStack, Float> getProductChances() {
		return productChances;
	}

	@Override
	public Map<ItemStack, Float> getSpecialtyChances() {
		return specialtyChances;
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

		public static final DefaultBeeIconProvider instance = new DefaultBeeIconProvider();

		private DefaultBeeIconProvider() {

		}

		private static final IIcon[][] icons = new IIcon[EnumBeeType.values().length][3];

		@Override
		@SideOnly(Side.CLIENT)
		public void registerIcons(IIconRegister register) {
			String beeIconDir = "bees/default/";
			TextureManager textureManager = TextureManager.getInstance();
			IIcon body1 = textureManager.registerTex(register, beeIconDir + "body1");

			for (int i = 0; i < EnumBeeType.values().length; i++) {
				EnumBeeType beeType = EnumBeeType.values()[i];
				if (beeType == EnumBeeType.NONE) {
					continue;
				}

				String beeTypeNameBase = beeIconDir + beeType.toString().toLowerCase(Locale.ENGLISH);

				icons[i][0] = textureManager.registerTex(register, beeTypeNameBase + ".outline");
				if (beeType == EnumBeeType.LARVAE) {
					icons[i][1] = textureManager.registerTex(register, beeTypeNameBase + ".body");
				} else {
					icons[i][1] = body1;
				}
				icons[i][2] = textureManager.registerTex(register, beeTypeNameBase + ".body2");
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
