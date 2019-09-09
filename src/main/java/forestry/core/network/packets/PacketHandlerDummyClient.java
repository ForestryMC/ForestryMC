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
package forestry.core.network.packets;

import net.minecraft.entity.player.PlayerEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.core.network.IForestryPacketHandlerClient;
import forestry.core.network.PacketBufferForestry;

@OnlyIn(Dist.CLIENT)
public class PacketHandlerDummyClient extends PacketHandlerDummy implements IForestryPacketHandlerClient {
	public static final PacketHandlerDummyClient INSTANCE = new PacketHandlerDummyClient();

	private PacketHandlerDummyClient() {

	}

	@Override
	public void onPacketData(PacketBufferForestry data, PlayerEntity player) {

	}
}
