/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.storage;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

public class BackpackManager {
	/**
	 * 0 - Miner's Backpack 1 - Digger's Backpack 2 - Forester's Backpack 3 - Hunter's Backpack 4 - Adventurer's Backpack
	 * 
	 * Use IMC messages to achieve the same effect!
	 */
	public static ArrayList<ItemStack>[] backpackItems;

	public static IBackpackInterface backpackInterface;

	/**
	 * Only use this if you know what you are doing. Prefer backpackInterface.
	 */
	public static HashMap<String, IBackpackDefinition> definitions = new HashMap<String, IBackpackDefinition>();
}
