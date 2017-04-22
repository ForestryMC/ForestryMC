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
package forestry.apiculture.network;

import forestry.apiculture.network.packets.PacketActiveUpdate;
import forestry.apiculture.network.packets.PacketAlvearyChange;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.network.packets.PacketBeeLogicActiveEntity;
import forestry.apiculture.network.packets.PacketBeeLogicEntityRequest;
import forestry.apiculture.network.packets.PacketCandleUpdate;
import forestry.apiculture.network.packets.PacketHabitatBiomePointer;
import forestry.apiculture.network.packets.PacketImprintSelectionResponse;
import forestry.core.network.IPacketRegistry;
import forestry.core.network.PacketIdClient;
import forestry.core.network.PacketIdServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRegistryApiculture implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.BEE_LOGIC_ACTIVE_ENTITY_REQUEST.setPacketHandler(new PacketBeeLogicEntityRequest.Handler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.IMPRINT_SELECTION_RESPONSE.setPacketHandler(new PacketImprintSelectionResponse.Handler());
		PacketIdClient.TILE_FORESTRY_ACTIVE.setPacketHandler(new PacketActiveUpdate.Handler());
		PacketIdClient.BEE_LOGIC_ACTIVE.setPacketHandler(new PacketBeeLogicActive.Handler());
		PacketIdClient.BEE_LOGIC_ACTIVE_ENTITY.setPacketHandler(new PacketBeeLogicActiveEntity.Handler());
		PacketIdClient.HABITAT_BIOME_POINTER.setPacketHandler(new PacketHabitatBiomePointer.Handler());
		PacketIdClient.CANDLE_UPDATE.setPacketHandler(new PacketCandleUpdate.Handler());
		PacketIdClient.ALVERAY_CONTROLLER_CHANGE.setPacketHandler(new PacketAlvearyChange.Handler());
	}
}
