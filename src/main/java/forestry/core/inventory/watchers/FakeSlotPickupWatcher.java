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
package forestry.core.inventory.watchers;

import net.minecraft.entity.player.EntityPlayer;

public class FakeSlotPickupWatcher implements ISlotPickupWatcher {

	public static final FakeSlotPickupWatcher instance = new FakeSlotPickupWatcher();

	private FakeSlotPickupWatcher() {

	}

	@Override
	public void onPickupFromSlot(int slotIndex, EntityPlayer player) {

	}
}
