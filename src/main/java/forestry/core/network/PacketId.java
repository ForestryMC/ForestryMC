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

public enum PacketId {
	INVALID,

	TILE_FORESTRY_UPDATE,
	TILE_FORESTRY_ERROR_UPDATE,
	TILE_FORESTRY_GUI_OPENED,
	TILE_FORESTRY_ACTIVE,
	FX_SIGNAL,

	WORKTABLE_MEMORY_UPDATE,

	HABITAT_BIOME_POINTER,
	CANDLE,
	BEE_LOGIC_ACTIVE,

	GUI_SELECTION_CHANGE,
	IMPRINT_SELECTION_GET,
	GUI_SELECTION_SET,
	GUI_LAYOUT_SELECT,
	GUI_PROGRESS_BAR,
	GUI_ITEMSTACK,
	GUI_ENERGY,

	PIPETTE_CLICK,

	ACCESS_SWITCH,

	GENOME_TRACKER_UPDATE,
	GAME_TOKEN_SET,

	CHIPSET_CLICK,
	SOLDERING_IRON_CLICK,
	SOCKET_UPDATE,

	LETTER_REQUEST_INFO,
	LETTER_TEXT,
	TRADING_ADDRESS_SET,
	LETTER_INFO,
	POBOX_INFO,
	POBOX_INFO_REQUEST,
	
	RIPENING_UPDATE,

	// Propolis pipe
	PROP_SEND_FILTER_SET,
	PROP_REQUEST_FILTER_SET,
	PROP_SEND_FILTER_CHANGE_GENOME,
	PROP_SEND_FILTER_CHANGE_TYPE;

	public static final PacketId[] VALUES = values();
}
