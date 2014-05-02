/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.lepidopterology;

import java.util.EnumSet;
import java.util.Map;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.genetics.IAlleleSpecies;

public interface IAlleleButterflySpecies extends IAlleleSpecies {
	
	/**
	 * @return the IBeeRoot
	 */
	IButterflyRoot getRoot();

	/**
	 * @return Path of the texture to use for entity rendering.
	 */
	String getEntityTexture();
	
	/**
	 * Allows butterflies to restrict random spawns beyond the restrictions set by getTemperature() and getHumidity().
	 * 
	 * @return EnumSet of biome tags this butterfly species can be spawned in.
	 */
	EnumSet<BiomeDictionary.Type> getSpawnBiomes();
	
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
	
	Map<ItemStack, Float> getButterflyLoot();
	
	Map<ItemStack, Float> getCaterpillarLoot();
}
