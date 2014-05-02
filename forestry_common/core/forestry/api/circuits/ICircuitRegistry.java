/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.circuits;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICircuitRegistry {

	/* CIRCUITS */
	HashMap<String, ICircuit> getRegisteredCircuits();

	void registerCircuit(ICircuit circuit);

	ICircuit getCircuit(String uid);

	ICircuitLibrary getCircuitLibrary(World world, String playername);

	void registerLegacyMapping(int id, String uid);

	ICircuit getFromLegacyMap(int id);

	/* LAYOUTS */
	HashMap<String, ICircuitLayout> getRegisteredLayouts();

	void registerLayout(ICircuitLayout layout);

	ICircuitLayout getLayout(String uid);

	ICircuitLayout getDefaultLayout();
	
	
	ICircuitBoard getCircuitboard(ItemStack itemstack);
	
	boolean isChipset(ItemStack itemstack);

}
