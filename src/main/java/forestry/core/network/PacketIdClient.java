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

import javax.annotation.Nonnull;

public enum PacketIdClient implements IPacketId {
	INVALID,

	// Core Gui
	ERROR_UPDATE(new PacketErrorUpdate()),
	ERROR_UPDATE_ENTITY(new PacketErrorUpdateEntity()),
	GUI_UPDATE(new PacketGuiUpdate()),
	GUI_UPDATE_ENTITY(new PacketGuiUpdateEntity()),
	GUI_LAYOUT_SELECT(new PacketGuiLayoutSelect()),
	GUI_ENERGY(new PacketGuiEnergy()),
	SOCKET_UPDATE(new PacketSocketUpdate()),
	ACCESS_UPDATE(new PacketAccessUpdate()),
	ACCESS_UPDATE_ENTITY(new PacketAccessUpdateEntity()),

	// Core Tile Entities
	TILE_FORESTRY_UPDATE(new PacketTileStream()),
	ITEMSTACK_DISPLAY(new PacketItemStackDisplay()),
	FX_SIGNAL(new PacketFXSignal()),
	TANK_LEVEL_UPDATE(new PacketTankLevelUpdate()),

	// Core Genome
	GENOME_TRACKER_UPDATE(new PacketGenomeTrackerUpdate()),

	// Factory
	WORKTABLE_MEMORY_UPDATE,

	// Apiculture
	TILE_FORESTRY_ACTIVE,
	BEE_LOGIC_ACTIVE,
	BEE_LOGIC_ACTIVE_ENTITY,
	HABITAT_BIOME_POINTER,
	CANDLE_UPDATE,
	IMPRINT_SELECTION_RESPONSE,

	// Arboriculture
	RIPENING_UPDATE,

	// Mail
	TRADING_ADDRESS_RESPONSE,
	LETTER_INFO_RESPONSE,
	POBOX_INFO_RESPONSE;

	public static final PacketIdClient[] VALUES = values();

	@Nonnull
	private IForestryPacketClient packetHandler;

	PacketIdClient() {
		this.packetHandler = PacketDummy.instance;
	}

	PacketIdClient(@Nonnull IForestryPacketClient packetHandler) {
		this.packetHandler = packetHandler;
	}

	public void setPacketHandler(@Nonnull IForestryPacketClient packetHandler) {
		this.packetHandler = packetHandler;
	}

	@Nonnull
	public IForestryPacketClient getPacketHandler() {
		return packetHandler;
	}
}
