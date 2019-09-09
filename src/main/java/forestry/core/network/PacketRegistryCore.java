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
package forestry.core.network;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.climatology.network.packets.PacketSelectClimateTargeted;
import forestry.core.network.packets.PacketActiveUpdate;
import forestry.core.network.packets.PacketChipsetClick;
import forestry.core.network.packets.PacketClimateListenerUpdate;
import forestry.core.network.packets.PacketClimateListenerUpdateEntity;
import forestry.core.network.packets.PacketClimateListenerUpdateEntityRequest;
import forestry.core.network.packets.PacketClimateListenerUpdateRequest;
import forestry.core.network.packets.PacketClimatePlayer;
import forestry.core.network.packets.PacketClimateUpdate;
import forestry.core.network.packets.PacketErrorUpdate;
import forestry.core.network.packets.PacketErrorUpdateEntity;
import forestry.core.network.packets.PacketFXSignal;
import forestry.core.network.packets.PacketGenomeTrackerSync;
import forestry.core.network.packets.PacketGuiEnergy;
import forestry.core.network.packets.PacketGuiLayoutSelect;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.network.packets.PacketGuiUpdate;
import forestry.core.network.packets.PacketGuiUpdateEntity;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.network.packets.PacketPipetteClick;
import forestry.core.network.packets.PacketSocketUpdate;
import forestry.core.network.packets.PacketSolderingIronClick;
import forestry.core.network.packets.PacketTankLevelUpdate;
import forestry.core.network.packets.PacketTileStream;

public class PacketRegistryCore implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.GUI_SELECTION_REQUEST.setPacketHandler(new PacketGuiSelectRequest.Handler());
		PacketIdServer.PIPETTE_CLICK.setPacketHandler(new PacketPipetteClick.Handler());
		PacketIdServer.CHIPSET_CLICK.setPacketHandler(new PacketChipsetClick.Handler());
		PacketIdServer.SOLDERING_IRON_CLICK.setPacketHandler(new PacketSolderingIronClick.Handler());
		PacketIdServer.SELECT_CLIMATE_TARGETED.setPacketHandler(new PacketSelectClimateTargeted.Handler());
		PacketIdServer.CLIMATE_LISTENER_UPDATE_REQUEST.setPacketHandler(new PacketClimateListenerUpdateRequest.Handler());
		PacketIdServer.CLIMATE_LISTENER_UPDATE_REQUEST_ENTITY.setPacketHandler(new PacketClimateListenerUpdateEntityRequest.Handler());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.ERROR_UPDATE.setPacketHandler(new PacketErrorUpdate.Handler());
		PacketIdClient.ERROR_UPDATE_ENTITY.setPacketHandler(new PacketErrorUpdateEntity.Handler());
		PacketIdClient.GUI_UPDATE.setPacketHandler(new PacketGuiUpdate.Handler());
		PacketIdClient.GUI_UPDATE_ENTITY.setPacketHandler(new PacketGuiUpdateEntity.Handler());
		PacketIdClient.GUI_LAYOUT_SELECT.setPacketHandler(new PacketGuiLayoutSelect.Handler());
		PacketIdClient.GUI_ENERGY.setPacketHandler(new PacketGuiEnergy.Handler());
		PacketIdClient.SOCKET_UPDATE.setPacketHandler(new PacketSocketUpdate.Handler());
		PacketIdClient.TILE_FORESTRY_UPDATE.setPacketHandler(new PacketTileStream.Handler());
		PacketIdClient.TILE_FORESTRY_ACTIVE.setPacketHandler(new PacketActiveUpdate.Handler());
		PacketIdClient.ITEMSTACK_DISPLAY.setPacketHandler(new PacketItemStackDisplay.Handler());
		PacketIdClient.FX_SIGNAL.setPacketHandler(new PacketFXSignal.Handler());
		PacketIdClient.TANK_LEVEL_UPDATE.setPacketHandler(new PacketTankLevelUpdate.Handler());
		PacketIdClient.GENOME_TRACKER_UPDATE.setPacketHandler(new PacketGenomeTrackerSync.Handler());
		PacketIdClient.UPDATE_CLIMATE.setPacketHandler(new PacketClimateUpdate.Handler());
		PacketIdClient.CLIMATE_LISTENER_UPDATE.setPacketHandler(new PacketClimateListenerUpdate.Handler());
		PacketIdClient.CLIMATE_LISTENER_UPDATE_ENTITY.setPacketHandler(new PacketClimateListenerUpdateEntity.Handler());
		PacketIdClient.CLIMATE_PLAYER.setPacketHandler(new PacketClimatePlayer.Handler());
	}
}
