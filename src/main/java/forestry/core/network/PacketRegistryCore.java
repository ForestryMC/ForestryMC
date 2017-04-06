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

import forestry.core.config.Constants;
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
import forestry.core.network.packets.PacketGuiUpdateModule;
import forestry.core.network.packets.PacketItemStackDisplay;
import forestry.core.network.packets.PacketPipetteClick;
import forestry.core.network.packets.PacketSocketUpdate;
import forestry.core.network.packets.PacketSolderingIronClick;
import forestry.core.network.packets.PacketTankLevelUpdate;
import forestry.core.network.packets.PacketTileStream;
import forestry.core.network.packets.PacketUpdateClimateControl;
import forestry.core.utils.ModUtil;

public class PacketRegistryCore extends PacketRegistry {
	@Override
	public void registerPackets() {
		registerClientPacket(new PacketErrorUpdate());
		registerClientPacket(new PacketErrorUpdateEntity());
		registerClientPacket(new PacketGuiUpdate());
		registerClientPacket(new PacketGuiUpdateEntity());
		registerClientPacket(new PacketGuiLayoutSelect());
		registerClientPacket(new PacketGuiEnergy());
		registerClientPacket(new PacketSocketUpdate());
		registerClientPacket(new PacketTileStream());
		registerClientPacket(new PacketItemStackDisplay());
		registerClientPacket(new PacketFXSignal());
		registerClientPacket(new PacketTankLevelUpdate());
		registerClientPacket(new PacketGenomeTrackerSync());
		registerClientPacket(new PacketCamouflageSelectClient());

		registerServerPacket(new PacketGuiSelectRequest());
		registerServerPacket(new PacketPipetteClick());
		registerServerPacket(new PacketChipsetClick());
		registerServerPacket(new PacketSolderingIronClick());
		registerServerPacket(new PacketCamouflageSelectServer());
		registerServerPacket(new PacketUpdateClimateControl());
		if(ModUtil.isModLoaded(Constants.MM_MOD_ID)){
			registerClientPacket(new PacketGuiUpdateModule());
		}
	}
}
