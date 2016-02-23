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
package forestry.factory.blocks;

import javax.annotation.Nonnull;

import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.factory.tiles.TileFabricator;
import forestry.factory.tiles.TileRaintank;
import forestry.factory.tiles.TileWorktable;

public enum BlockTypeFactoryPlain implements IBlockType {
	FABRICATOR(0, TileFabricator.class, "fabricator"),
	RAINTANK(1, TileRaintank.class, "raintank"),
	WORKTABLE(2, TileWorktable.class, "worktable");

	public static final BlockTypeFactoryPlain[] VALUES = values();

	@Nonnull
	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeFactoryPlain(int meta, @Nonnull Class<T> teClass, @Nonnull String name) {
		this.machineProperties = new MachineProperties<>(meta, teClass, name);
	}

	@Nonnull
	@Override
	public IMachineProperties getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return name();
	}
}
