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
package forestry.core.circuits;

import java.util.HashMap;
import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import forestry.api.circuits.ICircuit;
import forestry.api.circuits.ICircuitBoard;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitLibrary;
import forestry.api.circuits.ICircuitRegistry;
import forestry.core.config.ForestryItem;

public class CircuitRegistry implements ICircuitRegistry {

	private final LinkedHashMap<String, ICircuitLayout> layoutMap = new LinkedHashMap<String, ICircuitLayout>();
	private final LinkedHashMap<String, ICircuit> circuitMap = new LinkedHashMap<String, ICircuit>();
	private final HashMap<Integer, String> legacyMap = new HashMap<Integer, String>();

	@Override
	public ICircuitLibrary getCircuitLibrary(World world, String playername) {
		CircuitLibrary library = (CircuitLibrary) world.loadItemData(CircuitLibrary.class, "CircuitLibrary_" + playername);

		if (library == null) {
			library = new CircuitLibrary(playername);
			world.setItemData("CircuitLibrary_" + playername, library);
		}

		return library;
	}

	/* CIRCUIT LAYOUTS */
	@Override
	public ICircuitLayout getDefaultLayout() {
		if (layoutMap.containsKey("forestry.engine.tin"))
			return layoutMap.get("forestry.engine.tin");
		else
			return layoutMap.values().iterator().next();
	}

	@Override
	public LinkedHashMap<String, ICircuitLayout> getRegisteredLayouts() {
		return layoutMap;
	}

	@Override
	public void registerLayout(ICircuitLayout layout) {
		layoutMap.put(layout.getUID(), layout);
	}

	@Override
	public ICircuitLayout getLayout(String uid) {
		if (layoutMap.containsKey(uid))
			return layoutMap.get(uid);
		else
			return null;
	}

	/* CIRCUITS */
	@Override
	public HashMap<String, ICircuit> getRegisteredCircuits() {
		return circuitMap;
	}

	@Override
	public void registerCircuit(ICircuit circuit) {
		circuitMap.put(circuit.getUID(), circuit);
	}

	@Override
	public ICircuit getCircuit(String uid) {
		return circuitMap.get(uid);
	}

	@Override
	public void registerLegacyMapping(int id, String uid) {
		this.legacyMap.put(id, uid);
	}

	@Override
	public ICircuit getFromLegacyMap(int id) {
		if (!legacyMap.containsKey(id))
			return null;

		return getCircuit(legacyMap.get(id));
	}

	public void initialize() {
	}
	
	
	
	@Override
	public boolean isChipset(ItemStack itemstack) {
		return ForestryItem.circuitboards.isItemEqual(itemstack);
	}
	
	@Override
	public ICircuitBoard getCircuitboard(ItemStack itemstack) {
		NBTTagCompound nbttagcompound = itemstack.getTagCompound();
		if (nbttagcompound == null)
			return null;

		return new CircuitBoard(nbttagcompound);
	}
}
