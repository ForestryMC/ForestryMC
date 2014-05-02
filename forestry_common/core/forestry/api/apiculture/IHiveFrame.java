/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.item.ItemStack;

public interface IHiveFrame extends IBeeModifier {

	/**
	 * Wears out a frame.
	 * 
	 * @param housing
	 *            IBeeHousing the frame is contained in.
	 * @param frame
	 *            ItemStack containing the actual frame.
	 * @param queen
	 *            Current queen in the caller.
	 * @param wear
	 *            Integer denoting the amount worn out. The wear modifier of the current beekeeping mode has already been taken into account.
	 * @return ItemStack containing the actual frame with adjusted damage.
	 */
	ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear);

}
