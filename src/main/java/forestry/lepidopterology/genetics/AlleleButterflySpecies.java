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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.core.config.Defaults;
import forestry.core.genetics.AlleleSpecies;

public class AlleleButterflySpecies extends AlleleSpecies implements IAlleleButterflySpecies {

	private final IButterflyRoot root;

	private final String texture;
	private final int serumColour;
	private float rarity = 0.1f;
	private float flightDistance = 5.0f;
	private boolean isActualNocturnal = false;

	private final EnumSet<BiomeDictionary.Type> spawnBiomes = EnumSet.noneOf(BiomeDictionary.Type.class);

	private final Map<ItemStack, Float> butterflyLoot = new HashMap<ItemStack, Float>();
	private final Map<ItemStack, Float> caterpillarLoot = new HashMap<ItemStack, Float>();

	public AlleleButterflySpecies(String uid, boolean isDominant, String name, IClassification branch, String binomial, int serumColour) {
		super(uid, isDominant, "butterflies.species." + branch.getParent().getUID().substring((branch.getParent().getLevel().name().toLowerCase(Locale.ENGLISH)).length() + 1) + "." + name, branch, binomial);
		this.root = (IButterflyRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootButterflies");
		this.serumColour = serumColour;
		texture = "forestry:" + Defaults.TEXTURE_PATH_ENTITIES + "/butterflies/" + uid + ".png";
	}

	@Override
	public IButterflyRoot getRoot() {
		return root;
	}

	public AlleleButterflySpecies setRarity(float rarity) {
		this.rarity = rarity;
		return this;
	}

	public AlleleButterflySpecies setFlightDistance(float flightDistance) {
		this.flightDistance = flightDistance;
		return this;
	}

	public AlleleButterflySpecies setNocturnal(boolean isActualNocturnal) {
		this.isActualNocturnal = isActualNocturnal;
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
		return serumColour;
	}
}
