/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IMutation;
/**
 * 
 * Some miscellaneous lists and settings for bees.
 * 
 * @author SirSengir
 */
public class BeeManager {

	/**
	 * Get your own reference to this via AlleleManager.alleleRegistry.getSpeciesRoot("rootBees") and save it somewhere.
	 */
	@Deprecated
	public static IBeeRoot beeInterface;
	
	/**
	 * Species templates for bees that can drop from hives.
	 * 
	 * 0 - Forest 1 - Meadows 2 - Desert 3 - Jungle 4 - End 5 - Snow 6 - Swamp
	 * 
	 * see {@link IMutation} for template format
	 */
	public static ArrayList<IHiveDrop>[] hiveDrops;

	/**
	 * 0 - Common Village Bees 1 - Uncommon Village Bees (20 % of spawns)
	 */
	public static ArrayList<IBeeGenome>[] villageBees;

	/**
	 * List of items that can induce swarming. Integer denotes x in 1000 chance.
	 */
	public static HashMap<ItemStack, Integer> inducers = new HashMap<ItemStack, Integer>();
}
