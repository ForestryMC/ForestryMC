/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
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
	 * Species templates for bees that can drop from hives.
	 * 
	 * 0 - Forest 1 - Meadows 2 - Desert 3 - Jungle 4 - End 5 - Snow 6 - Swamp
	 * 
	 * see {@link IMutation} for template format
	 */
	@Deprecated // Deprecated since 3.1. Use HiveManager instead
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
