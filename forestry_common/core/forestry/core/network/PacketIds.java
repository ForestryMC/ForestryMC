/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.network;

/**
 * Valid ids: 0 - 127
 */
public class PacketIds {

	public static final int TILE_FORESTRY_UPDATE = 0;
	public static final int TILE_UPDATE = 1;
	public static final int TILE_DESCRIPTION = 2;
	public static final int IINVENTORY_STACK = 3;
	public static final int FX_SIGNAL = 4;
	public static final int TILE_NBT = 5;

	public static final int HABITAT_BIOME_POINTER = 20;

	public static final int GUI_SELECTION_CHANGE = 30;
	public static final int IMPRINT_SELECTION_GET = 31;
	public static final int GUI_SELECTION = 32;

	public static final int PIPETTE_CLICK = 40;

	public static final int ACCESS_SWITCH = 50;

	public static final int GENOME_TRACKER_UPDATE = 60;
	public static final int GAME_TOKEN_SET = 61;

	public static final int CHIPSET_CLICK = 70;
	public static final int SOLDERING_IRON_CLICK = 71;
	public static final int SOCKET_UPDATE = 72;

	public static final int LETTER_RECIPIENT = 80;
	public static final int LETTER_TEXT = 81;
	public static final int TRADING_MONIKER_SET = 82;
	public static final int TRADING_INFO = 83;
	public static final int POBOX_INFO = 85;
	public static final int POBOX_INFO_REQUEST = 86;

	public static final int LEAF_UPDATE = 90;
	
	public static final int TANK_UPDATE = 91;
	
	// Propolis pipe
	public static final int PROP_SEND_FILTER_SET = 100;
	public static final int PROP_REQUEST_FILTER_SET = 101;
	public static final int PROP_SEND_FILTER_CHANGE_GENOME = 102;
	public static final int PROP_SEND_FILTER_CHANGE_TYPE = 103;
	
	
	

}
