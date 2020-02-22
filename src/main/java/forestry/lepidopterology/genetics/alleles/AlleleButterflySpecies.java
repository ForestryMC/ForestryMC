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
package forestry.lepidopterology.genetics.alleles;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;

import genetics.api.classification.IClassification;

import forestry.api.core.ISpriteRegistry;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.genetics.IButterflyRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleForestrySpecies;

public class AlleleButterflySpecies extends AlleleForestrySpecies
	implements IAlleleButterflySpecies, IAlleleButterflySpeciesBuilder {
	private final String texture;
	private final Color serumColour;
	private float rarity = 0.1f;
	private float flightDistance = 5.0f;
	private boolean isActualNocturnal = false;

	private final Set<BiomeDictionary.Type> spawnBiomes = new HashSet<>();

	private final Map<ItemStack, Float> butterflyLoot = new HashMap<>();
	private final Map<ItemStack, Float> caterpillarLoot = new HashMap<>();

	public AlleleButterflySpecies(String uid, String unlocalizedName, String authority, String unlocalizedDescription,
		String modID, String texturePath, boolean isDominant, IClassification branch, String binomial,
		Color serumColour) {
		super(modID, uid, unlocalizedName, authority, unlocalizedDescription, isDominant, branch, binomial);
		this.serumColour = serumColour;

		this.texture = texturePath;
	}

	@Override
	public IAlleleButterflySpecies build() {
		return this;
	}

	@Override
	public IButterflyRoot getRoot() {
		return ButterflyManager.butterflyRoot;
	}

	@Override
	public AlleleButterflySpecies setRarity(float rarity) {
		this.rarity = rarity;
		return this;
	}

	@Override
	public AlleleButterflySpecies setFlightDistance(float flightDistance) {
		this.flightDistance = flightDistance;
		return this;
	}

	@Override
	public AlleleButterflySpecies setNocturnal() {
		this.isActualNocturnal = true;
		return this;
	}

	public AlleleButterflySpecies addSpawnBiomes(Set<BiomeDictionary.Type> biomeTags) {
		spawnBiomes.addAll(biomeTags);
		return this;
	}

	public AlleleButterflySpecies addSpawnBiome(BiomeDictionary.Type biomeTag) {
		spawnBiomes.add(biomeTag);
		return this;
	}

	@Override
	public String getEntityTexture() {
		return getRegistryName().getNamespace() + ":" + Constants.TEXTURE_PATH_ENTITIES + "/" + texture + ".png";
	}

	@Override
	public String getItemTexture() {
		return getRegistryName().getNamespace() + ":item/" + texture;
	}

	@Override
	public Set<BiomeDictionary.Type> getSpawnBiomes() {
		return spawnBiomes;
	}

	@Override
	public boolean strictSpawnMatch() {
		return false;
	}

	/* RESEARCH */
	@Override
	public int getComplexity() {
		return (int) (1.35f / rarity * 1.5);
	}

	/* OTHER */
	@Override
	public boolean isSecret() {
		return rarity < 0.25f;
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
	public int getSpriteColour(int renderPass) {
		if (renderPass > 0) {
			return serumColour.getRGB() & 0xffffff;
		}
		return 0xffffff;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerSprites(ISpriteRegistry registry) {
		registry.addSprite(new ResourceLocation(getItemTexture()));
	}
}
