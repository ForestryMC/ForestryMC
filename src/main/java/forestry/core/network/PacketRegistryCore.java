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

import forestry.core.network.packets.PacketCamouflageSelectClient;
import forestry.core.network.packets.PacketCamouflageSelectServer;
import forestry.core.network.packets.PacketChipsetClick;
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
import forestry.core.network.packets.PacketUpdateClimateControl;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRegistryCore implements IPacketRegistry {
	@Override
	public void registerPacketsServer() {
		PacketIdServer.GUI_SELECTION_REQUEST.setPacketHandler(new PacketGuiSelectRequest.Handler());
		PacketIdServer.PIPETTE_CLICK.setPacketHandler(new PacketPipetteClick.Handler());
		PacketIdServer.CHIPSET_CLICK.setPacketHandler(new PacketChipsetClick.Handler());
		PacketIdServer.SOLDERING_IRON_CLICK.setPacketHandler(new PacketSolderingIronClick.Handler());
		PacketIdServer.CAMOUFLAGE_SELECTION.setPacketHandler(new PacketCamouflageSelectServer.Handler());
		PacketIdServer.CLIMATE_CONTROL_UPDATE.setPacketHandler(new PacketUpdateClimateControl.Handler());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerPacketsClient() {
		PacketIdClient.ERROR_UPDATE.setPacketHandler(new PacketErrorUpdate.Handler());
		PacketIdClient.ERROR_UPDATE_ENTITY.setPacketHandler(new PacketErrorUpdateEntity.Handler());
		PacketIdClient.GUI_UPDATE.setPacketHandler(new PacketGuiUpdate.Handler());
		PacketIdClient.GUI_UPDATE_ENTITY.setPacketHandler(new PacketGuiUpdateEntity.Handler());
		PacketIdClient.GUI_LAYOUT_SELECT.setPacketHandler(new PacketGuiLayoutSelect.Handler());
		PacketIdClient.GUI_ENERGY.setPacketHandler(new PacketGuiEnergy.Handler());
		PacketIdClient.SOCKET_UPDATE.setPacketHandler(new PacketSocketUpdate.Handler());
		PacketIdClient.TILE_FORESTRY_UPDATE.setPacketHandler(new PacketTileStream.Handler());
		PacketIdClient.ITEMSTACK_DISPLAY.setPacketHandler(new PacketItemStackDisplay.Handler());
		PacketIdClient.FX_SIGNAL.setPacketHandler(new PacketFXSignal.Handler());
		PacketIdClient.TANK_LEVEL_UPDATE.setPacketHandler(new PacketTankLevelUpdate.Handler());
		PacketIdClient.GENOME_TRACKER_UPDATE.setPacketHandler(new PacketGenomeTrackerSync.Handler());
		PacketIdClient.CAMOUFLAGE_SELECTION.setPacketHandler(new PacketCamouflageSelectClient.Handler());
	}
}
