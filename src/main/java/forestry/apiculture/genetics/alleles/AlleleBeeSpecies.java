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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IBeeSpriteColourProvider;
import forestry.api.apiculture.IJubilanceProvider;
import forestry.api.core.IModelManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.genetics.DefaultBeeModelProvider;
import forestry.apiculture.genetics.DefaultBeeSpriteColourProvider;
import forestry.apiculture.genetics.JubilanceDefault;
import forestry.core.genetics.alleles.AlleleSpecies;
import forestry.core.utils.ItemStackUtil;

public class AlleleBeeSpecies extends AlleleSpecies implements IAlleleBeeSpecies, IAlleleBeeSpeciesBuilder {
	private final Map<ItemStack, Float> productChances = new HashMap<>();
	private final Map<ItemStack, Float> specialtyChances = new HashMap<>();

	private IBeeModelProvider beeModelProvider;
	private IBeeSpriteColourProvider beeSpriteColourProvider;
	private IJubilanceProvider jubilanceProvider;
	private boolean nocturnal = false;

	public AlleleBeeSpecies(String modId, String uid, String unlocalizedName, String authority, String unlocalizedDescription, boolean dominant, IClassification branch, String binomial, int primaryColor, int secondaryColor) {
		super(modId, uid, unlocalizedName, authority, unlocalizedDescription, dominant, branch, binomial);

		beeModelProvider = DefaultBeeModelProvider.instance;
		beeSpriteColourProvider = new DefaultBeeSpriteColourProvider(primaryColor, secondaryColor);
		jubilanceProvider = JubilanceDefault.instance;
	}

	@Override
	public IAlleleBeeSpecies build() {
		AlleleManager.alleleRegistry.registerAllele(this, EnumBeeChromosome.SPECIES);
		return this;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public IAlleleBeeSpeciesBuilder addProduct(ItemStack product, Float chance) {
		if (product.isEmpty()) {
			throw new IllegalArgumentException("Tried to add empty product");
		}
		if (chance <= 0.0f || chance > 1.0f) {
			throw new IllegalArgumentException("chance must be in the range (0, 1]");
		}
		this.productChances.put(product, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder addSpecialty(ItemStack specialty, Float chance) {
		if (specialty.isEmpty()) {
			throw new IllegalArgumentException("Tried to add empty specialty");
		}
		if (chance <= 0.0f || chance > 1.0f) {
			throw new IllegalArgumentException("chance must be in the range (0, 1]");
		}
		this.specialtyChances.put(specialty, chance);
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setJubilanceProvider(IJubilanceProvider provider) {
		this.jubilanceProvider = provider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setNocturnal() {
		nocturnal = true;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setCustomBeeModelProvider(IBeeModelProvider beeIconProvider) {
		this.beeModelProvider = beeIconProvider;
		return this;
	}

	@Override
	public IAlleleBeeSpeciesBuilder setCustomBeeSpriteColourProvider(IBeeSpriteColourProvider beeIconColourProvider) {
		this.beeSpriteColourProvider = beeIconColourProvider;
		return this;
	}

	/* RESEARCH */
	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
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
	public NonNullList<ItemStack> getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		NonNullList<ItemStack> bounty = NonNullList.create();
		bounty.addAll(super.getResearchBounty(world, researcher, individual, bountyLevel));

		if (bountyLevel > 10) {
			for (ItemStack stack : specialtyChances.keySet()) {
				bounty.add(ItemStackUtil.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
			}
		}
		for (ItemStack stack : productChances.keySet()) {
			bounty.add(ItemStackUtil.copyWithRandomSize(stack, (int) ((float) bountyLevel / 2), world.rand));
		}
		return bounty;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(Item item, IModelManager manager) {
		beeModelProvider.registerModels(item, manager);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModel(EnumBeeType type) {
		return beeModelProvider.getModel(type);
	}

	@Override
	public int getSpriteColour(int renderPass) {
		return beeSpriteColourProvider.getSpriteColour(renderPass);
	}
}
