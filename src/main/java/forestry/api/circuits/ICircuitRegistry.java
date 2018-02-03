/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.circuits;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICircuitRegistry {

	/* CIRCUITS */
	Map<String, ICircuit> getRegisteredCircuits();

	void registerCircuit(ICircuit circuit);

	void registerDeprecatedCircuitReplacement(String deprecatedCircuit, ICircuit replacement);

	@Nullable
	ICircuit getCircuit(String uid);

	ICircuitLibrary getCircuitLibrary(World world, String playerName);

	/* LAYOUTS */
	Map<String, ICircuitLayout> getRegisteredLayouts();

	void registerLayout(ICircuitLayout layout);

	@Nullable
	ICircuitLayout getLayout(String uid);

	ICircuitLayout getDefaultLayout();

	@Nullable
	ICircuitBoard getCircuitBoard(ItemStack itemstack);

	boolean isChipset(ItemStack itemstack);

}
