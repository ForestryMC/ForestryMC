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
package forestry.apiculture.network.packets;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.network.ForestryPacket;
import forestry.core.network.IForestryPacketClient;
import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;
import forestry.core.network.PacketIdClient;

public class PacketBeeLogicActiveEntity extends ForestryPacket implements IForestryPacketClient {
	private final Entity entity;
	private final BeekeepingLogic beekeepingLogic;

	public PacketBeeLogicActiveEntity(IBeeHousing housing, Entity entity) {
		this.entity = entity;
		this.beekeepingLogic = (BeekeepingLogic) housing.getBeekeepingLogic();
	}

	@Override
	public PacketIdClient getPacketId() {
		return PacketIdClient.BEE_LOGIC_ACTIVE_ENTITY;
	}

	@Override
	protected void writeData(PacketBufferForestry data) {
		data.writeEntityById(entity);
		beekeepingLogic.writeData(data);
	}

	@OnlyIn(Dist.CLIENT)
	public static class Handler implements IForestryPacketHandlerClient {
		@Override
		public void onPacketData(PacketBufferForestry data, PlayerEntity player) throws IOException {
			Entity entity = data.readEntityById(player.world);
			if (entity instanceof IBeeHousing) {
				IBeeHousing beeHousing = (IBeeHousing) entity;
				IBeekeepingLogic beekeepingLogic = beeHousing.getBeekeepingLogic();
				if (beekeepingLogic instanceof BeekeepingLogic) {
					beekeepingLogic.readData(data);
				}
			}
		}
	}
}
