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


import forestry.core.network.packets.PacketHandlerDummyServer;

/**
 * Packets sent to the server from the client
 */
public enum PacketIdServer implements IPacketId {
	INVALID,

	// Core Gui
	GUI_SELECTION_REQUEST,
	PIPETTE_CLICK,
	CHIPSET_CLICK,
	SOLDERING_IRON_CLICK,

	//Database
	INSERT_ITEM,
	EXTRACT_ITEM,

	// Sorting
	FILTER_CHANGE_RULE,
	FILTER_CHANGE_GENOME,

	// Climate
	SELECT_CLIMATE_TARGETED,
	CLIMATE_LISTENER_UPDATE_REQUEST,
	CLIMATE_LISTENER_UPDATE_REQUEST_ENTITY,

	// JEI
	WORKTABLE_RECIPE_REQUEST,
	RECIPE_TRANSFER_REQUEST,

	// Apiculture
	BEE_LOGIC_ACTIVE_ENTITY_REQUEST,

	// Mail
	LETTER_INFO_REQUEST,
	TRADING_ADDRESS_REQUEST,
	LETTER_TEXT_SET;

	public static final PacketIdServer[] VALUES = values();

	private IForestryPacketHandlerServer packetHandler;

	PacketIdServer() {
		this.packetHandler = PacketHandlerDummyServer.instance;
	}

	public void setPacketHandler(IForestryPacketHandlerServer packetHandler) {
		this.packetHandler = packetHandler;
	}

	public IForestryPacketHandlerServer getPacketHandler() {
		return packetHandler;
	}
}
