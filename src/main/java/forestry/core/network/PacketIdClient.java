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

import forestry.core.network.packets.PacketDummyClient;

public enum PacketIdClient implements IPacketId {
	INVALID,

	// Core Gui
	ERROR_UPDATE,
	ERROR_UPDATE_ENTITY,
	GUI_UPDATE,
	GUI_UPDATE_ENTITY,
	GUI_LAYOUT_SELECT,
	GUI_ENERGY,
	SOCKET_UPDATE,
	ACCESS_UPDATE,
	ACCESS_UPDATE_ENTITY,

	// Core Tile Entities
	TILE_FORESTRY_UPDATE,
	ITEMSTACK_DISPLAY,
	FX_SIGNAL,
	TANK_LEVEL_UPDATE,

	// Core Genome
	GENOME_TRACKER_UPDATE,

	// Factory
	WORKTABLE_MEMORY_UPDATE,
	WORKTABLE_CRAFTING_UPDATE,

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
		this.packetHandler = PacketDummyClient.instance;
	}

	public void setPacketHandler(@Nonnull IForestryPacketClient packetHandler) {
		this.packetHandler = packetHandler;
	}

	@Nonnull
	public IForestryPacketClient getPacketHandler() {
		return packetHandler;
	}
}
