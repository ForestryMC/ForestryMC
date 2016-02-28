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
package forestry.apiculture.blocks;

import javax.annotation.Nonnull;

import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IBlockTypeCustom;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;

public enum BlockTypeApiculture implements IBlockTypeCustom {
	APIARY(0, TileApiary.class, "apiary"),
	BEE_HOUSE(1, TileBeeHouse.class, "bee.house");

	public static final BlockTypeApiculture[] VALUES = values();

	@Nonnull
	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeApiculture(int meta, @Nonnull Class<T> teClass, @Nonnull String name) {
		this.machineProperties = new MachineProperties<>(meta, teClass, name);
	}

	@Nonnull
	@Override
	public IMachineProperties<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
