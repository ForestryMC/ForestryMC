/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IIndividual;

public interface IBeeListener {

	/**
	 * Called on queen update.
	 * 
	 * @param queen
	 */
	void onQueenChange(ItemStack queen);

	/**
	 * Called when the bees wear out the housing's equipment.
	 * 
	 * @param amount
	 *            Integer indicating the amount worn out.
	 */
	void wearOutEquipment(int amount);

	/**
	 * Called just before the children are generated, and the queen removed.
	 * 
	 * @param queen
	 */
	void onQueenDeath(IBee queen);

	/**
	 * Called after the children have been spawned, but before the queen appears
	 * 
	 * @param queen
	 */
	void onPostQueenDeath(IBee queen);

	boolean onPollenRetrieved(IBee queen, IIndividual pollen, boolean isHandled);
	
	boolean onEggLaid(IBee queen);
}
