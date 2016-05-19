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
package forestry.mail.blocks;

import javax.annotation.Nonnull;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.mail.tiles.TileMailbox;
import forestry.mail.tiles.TileStampCollector;
import forestry.mail.tiles.TileTrader;

public enum BlockTypeMail implements IBlockType {
	MAILBOX(TileMailbox.class, "mailbox"),
	TRADE_STATION(TileTrader.class, "trade_station"),
	PHILATELIST(TileStampCollector.class, "stamp_collector");

	public static final BlockTypeMail[] VALUES = values();

	@Nonnull
	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeMail(@Nonnull Class<T> teClass, @Nonnull String name) {
		this.machineProperties = new MachineProperties<>(teClass, name);
	}

	@Nonnull
	@Override
	public IMachineProperties getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
