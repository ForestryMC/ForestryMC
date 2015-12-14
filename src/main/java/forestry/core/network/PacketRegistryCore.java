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

import forestry.core.network.packets.PacketAccessSwitch;
import forestry.core.network.packets.PacketAccessSwitchEntity;
import forestry.core.network.packets.PacketAccessUpdate;
import forestry.core.network.packets.PacketAccessUpdateEntity;
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
		registerClientPacket(new PacketAccessUpdate());
		registerClientPacket(new PacketAccessUpdateEntity());
		registerClientPacket(new PacketTileStream());
		registerClientPacket(new PacketItemStackDisplay());
		registerClientPacket(new PacketFXSignal());
		registerClientPacket(new PacketTankLevelUpdate());
		registerClientPacket(new PacketGenomeTrackerSync());

		registerServerPacket(new PacketGuiSelectRequest());
		registerServerPacket(new PacketPipetteClick());
		registerServerPacket(new PacketAccessSwitch());
		registerServerPacket(new PacketAccessSwitchEntity());
		registerServerPacket(new PacketChipsetClick());
		registerServerPacket(new PacketSolderingIronClick());
	}
}
