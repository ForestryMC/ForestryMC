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

import forestry.apiculture.tiles.TileApiary;
import forestry.apiculture.tiles.TileBeeHouse;
import forestry.core.blocks.IBlockType;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;

public enum BlockTypeApiculture implements IBlockType {
	APIARY(TileApiary.class, "apiary"),
	BEE_HOUSE(TileBeeHouse.class, "bee_house");

	public static final BlockTypeApiculture[] VALUES = values();

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeApiculture(Class<T> teClass, String name) {
		this.machineProperties = new MachineProperties<>(teClass, name);
	}

	@Override
	public IMachineProperties<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
