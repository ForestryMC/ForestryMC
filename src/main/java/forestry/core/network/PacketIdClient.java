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


import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	// Climate
	UPDATE_CLIMATE,
	CLIMATE_LISTENER_UPDATE,
	CLIMATE_LISTENER_UPDATE_ENTITY,
	CLIMATE_PLAYER,

	// Sorting
	GUI_UPDATE_FILTER,

	// JEI
	RECIPE_TRANSFER_UPDATE;

	public static final PacketIdClient[] VALUES = values();

	@SideOnly(Side.CLIENT)
	@Nullable
	private IForestryPacketHandlerClient packetHandler;

	@SideOnly(Side.CLIENT)
	public void setPacketHandler(IForestryPacketHandlerClient packetHandler) {
		this.packetHandler = packetHandler;
	}

	@SideOnly(Side.CLIENT)
	public IForestryPacketHandlerClient getPacketHandler() {
		if (packetHandler == null) {
			return PacketHandlerDummyClient.INSTANCE;
		}
		return packetHandler;
	}
}
