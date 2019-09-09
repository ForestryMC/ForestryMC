/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.hives;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBee;

public interface IHiveFrame {

	/**
	 * Wears out a frame.
	 *
	 * @param housing IBeeHousing the frame is contained in.
	 * @param frame   ItemStack containing the actual frame.
	 * @param queen   Current queen in the caller.
	 * @param wear    Integer denoting the amount worn out. The wear modifier of the current beekeeping mode has already been taken into account.
	 * @return ItemStack containing the actual frame with adjusted damage, or Empty ItemStack if it has been broken.
	 */
	ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear);

	/**
	 * @param frame ItemStack containing the actual frame.
	 * @return the {@link IBeeModifier} for this frame.
	 * @since Forestry 5.5.1
	 */
	IBeeModifier getBeeModifier(ItemStack frame);
}
