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


import forestry.core.network.packets.PacketHandlerDummyClient;

/**
 * Packets sent to the client from the server
 */
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
	CAMOUFLAGE_SELECTION,

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
	ALVERAY_CONTROLLER_CHANGE,

	// Arboriculture
	RIPENING_UPDATE,

	// Mail
	TRADING_ADDRESS_RESPONSE,
	LETTER_INFO_RESPONSE,
	POBOX_INFO_RESPONSE,

	// JEI
	RECIPE_TRANSFER_UPDATE;

	public static final PacketIdClient[] VALUES = values();

	private IForestryPacketHandlerClient packetHandler;

	PacketIdClient() {
		this.packetHandler = PacketHandlerDummyClient.instance;
	}

	public void setPacketHandler(IForestryPacketHandlerClient packetHandler) {
		this.packetHandler = packetHandler;
	}

	public IForestryPacketHandlerClient getPacketHandler() {
		return packetHandler;
	}
}
