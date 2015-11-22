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
package forestry.lepidopterology.genetics;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.BiomeDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.core.IIconProvider;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpeciesCustom;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.core.genetics.alleles.AlleleSpecies;

public class AlleleButterflySpecies extends AlleleSpecies implements IAlleleButterflySpeciesCustom {
	private final String texture;
	private final Color serumColour;
	private float rarity = 0.1f;
	private float flightDistance = 5.0f;
	private boolean isActualNocturnal = false;

	private final EnumSet<BiomeDictionary.Type> spawnBiomes = EnumSet.noneOf(BiomeDictionary.Type.class);

	private final Map<ItemStack, Float> butterflyLoot = new HashMap<>();
	private final Map<ItemStack, Float> caterpillarLoot = new HashMap<>();

	public AlleleButterflySpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription, String texturePath, boolean isDominant, IClassification branch, String binomial, Color serumColour) {
		super(uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);
		this.serumColour = serumColour;
		this.texture = texturePath;
	}

	@Override
	public IButterflyRoot getRoot() {
		return ButterflyManager.butterflyRoot;
	}

	public AlleleButterflySpecies setRarity(float rarity) {
		this.rarity = rarity;
		return this;
	}

	public AlleleButterflySpecies setFlightDistance(float flightDistance) {
		this.flightDistance = flightDistance;
		return this;
	}

	public AlleleButterflySpecies setNocturnal() {
		this.isActualNocturnal = true;
		return this;
	}

	public AlleleButterflySpecies addSpawnBiomes(EnumSet<BiomeDictionary.Type> biomeTags) {
		spawnBiomes.addAll(biomeTags);
		return this;
	}

	public AlleleButterflySpecies addSpawnBiome(BiomeDictionary.Type biomeTag) {
		spawnBiomes.add(biomeTag);
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return null;
	}

	@Override
	public String getEntityTexture() {
		return texture;
	}

	@Override
	public EnumSet<BiomeDictionary.Type> getSpawnBiomes() {
		return spawnBiomes;
	}

	@Override
	public boolean strictSpawnMatch() {
		return false;
	}

	/* RESEARCH */
	@Override
	public int getComplexity() {
		return (int) ((1.35f / rarity) * 1.5);
	}

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack == null) {
			return 0f;
		}

		if (itemstack.getItem() == Items.glass_bottle) {
			return 0.9f;
		}

		for (ItemStack stack : butterflyLoot.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}
		for (ItemStack stack : caterpillarLoot.keySet()) {
			if (stack.isItemEqual(itemstack)) {
				return 1.0f;
			}
		}

		return super.getResearchSuitability(itemstack);
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		return new ItemStack[]{getRoot().getMemberStack(individual.copy(), EnumFlutterType.SERUM.ordinal())};
	}

	/* OTHER */
	@Override
	public boolean isSecret() {
		return rarity < 0.8f;
	}

	@Override
	public float getRarity() {
		return rarity;
	}

	@Override
	public float getFlightDistance() {
		return flightDistance;
	}

	@Override
	public boolean isNocturnal() {
		return isActualNocturnal;
	}

	@Override
	public Map<ItemStack, Float> getButterflyLoot() {
		return butterflyLoot;
	}

	@Override
	public Map<ItemStack, Float> getCaterpillarLoot() {
		return caterpillarLoot;
	}

	@Override
	public int getIconColour(int renderPass) {
		if (renderPass > 0) {
			return 0xffffff;
		}
		return serumColour.getRGB() & 0xffffff;
	}
}
