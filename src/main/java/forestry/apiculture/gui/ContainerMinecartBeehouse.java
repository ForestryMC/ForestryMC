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
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.apiculture.entities.EntityMinecartBeeHousingBase;
import forestry.core.gui.ContainerEntity;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.packets.PacketGuiUpdateEntity;

public class ContainerMinecartBeehouse extends ContainerEntity<EntityMinecartBeeHousingBase> implements IContainerBeeHousing {
	public ContainerMinecartBeehouse(InventoryPlayer player, EntityMinecartBeeHousingBase entity, boolean hasFrames) {
		super(entity, player, 8, 108);
		ContainerBeeHelper.addSlots(this, entity, hasFrames);
	}

	private int beeProgress = 0;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		int beeProgress = entity.getBeekeepingLogic().getBeeProgressPercent();
		if (this.beeProgress != beeProgress) {
			this.beeProgress = beeProgress;
			IForestryPacketClient packet = new PacketGuiUpdateEntity(entity, entity);
			sendPacketToCrafters(packet);
		}
	}

}
