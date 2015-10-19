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

public enum PacketIdServer implements IPacketId {
	INVALID,

	// Core Gui
	GUI_SELECTION_REQUEST(new PacketGuiSelectRequest()),
	PIPETTE_CLICK(new PacketPipetteClick()),
	ACCESS_SWITCH(new PacketAccessSwitch()),
	ACCESS_SWITCH_ENTITY(new PacketAccessSwitchEntity()),
	CHIPSET_CLICK(new PacketChipsetClick()),
	SOLDERING_IRON_CLICK(new PacketSolderingIronClick()),

	// Apiculture
	BEE_LOGIC_ACTIVE_ENTITY_REQUEST,

	// Factory
	WORKTABLE_NEI_SELECT,

	// Mail
	LETTER_INFO_REQUEST,
	TRADING_ADDRESS_REQUEST,
	POBOX_INFO_REQUEST,
	LETTER_TEXT_SET;

	public static final PacketIdServer[] VALUES = values();

	@Nonnull
	private IForestryPacketServer packetHandler;

	PacketIdServer() {
		this.packetHandler = PacketDummy.instance;
	}

	PacketIdServer(@Nonnull IForestryPacketServer packetHandler) {
		this.packetHandler = packetHandler;
	}

	public void setPacketHandler(@Nonnull IForestryPacketServer packetHandler) {
		this.packetHandler = packetHandler;
	}

	@Nonnull
	public IForestryPacketServer getPacketHandler() {
		return packetHandler;
	}
}
