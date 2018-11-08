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

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleSpecies;

public class AlleleButterflySpecies extends AlleleSpecies
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
		AlleleManager.alleleRegistry.registerAllele(this, EnumButterflyChromosome.SPECIES);
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
		return getModID() + ":" + Constants.TEXTURE_PATH_ENTITIES + "/" + texture + ".png";
	}

	@Override
	public String getItemTexture() {
		return getModID() + ":items/" + texture;
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

	@Override
	public float getResearchSuitability(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0f;
		}

		if (itemstack.getItem() == Items.GLASS_BOTTLE) {
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
	public NonNullList<ItemStack> getResearchBounty(World world, GameProfile researcher, IIndividual individual,
		int bountyLevel) {
		ItemStack serum = getRoot().getMemberStack(individual.copy(), EnumFlutterType.SERUM);
		NonNullList<ItemStack> bounty = NonNullList.create();
		bounty.add(serum);
		return bounty;
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
	@SideOnly(Side.CLIENT)
	public void registerSprites() {
		String spriteName = getItemTexture();
		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		textureMap.registerSprite(new ResourceLocation(spriteName));
	}
}
