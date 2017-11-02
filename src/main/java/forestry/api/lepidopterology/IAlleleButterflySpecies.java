/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.lepidopterology;

import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.BiomeDictionary;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.IAlleleSpecies;

public interface IAlleleButterflySpecies extends IAlleleSpecies {

	/**
	 * @return the IButterflyRoot
	 */
	@Override
	IButterflyRoot getRoot();

	/**
	 * @return Path of the texture to use for entity rendering.
	 */
	String getEntityTexture();

	/**
	 * @return Path of the texture to use for item model.
	 */
	String getItemTexture();

	/**
	 * Allows butterflies to restrict random spawns beyond the restrictions set by getTemperature() and getHumidity().
	 *
	 * @return EnumSet of biome tags this butterfly species can be spawned in.
	 */
	Set<BiomeDictionary.Type> getSpawnBiomes();

	/**
	 * @return true if a prospective spawn biome must not match a biome tag outside of getSpawnBiomes.
	 */
	boolean strictSpawnMatch();

	/**
	 * @return Float between 0 and 1 representing the rarity of the species, will affect spawn rate.
	 */
	float getRarity();

	/**
	 * @return Float representing the distance below which this butterfly will take flight if it detects a player which is not sneaking.
	 */
	float getFlightDistance();

	/**
	 * @return true if this species is only active at night.
	 */
	boolean isNocturnal();

	/**
	 * @return The loot that drops if you kill a butterfly.
	 */
	Map<ItemStack, Float> getButterflyLoot();

	/**
	 * @return The loot that drops if you destroy a leaf that contains a caterpillar.
	 */
	Map<ItemStack, Float> getCaterpillarLoot();

	@SideOnly(Side.CLIENT)
	void registerSprites();
}
