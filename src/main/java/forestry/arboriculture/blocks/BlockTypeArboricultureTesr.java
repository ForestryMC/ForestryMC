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
package forestry.arboriculture.blocks;

import net.minecraft.util.math.shapes.VoxelShape;

import forestry.arboriculture.tiles.TileArboristChest;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileNaturalistChest;

public enum BlockTypeArboricultureTesr implements IBlockTypeTesr {
	ARB_CHEST(TileArboristChest.class, "arb_chest", "arbchest", TileNaturalistChest.CHEST_SHAPE);

	public static final BlockTypeArboricultureTesr[] VALUES = values();

	private final IMachinePropertiesTesr<?> machineProperties;

	<T extends TileNaturalistChest> BlockTypeArboricultureTesr(Class<T> teClass, String name, String renderName, VoxelShape shape) {
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr<>(teClass, name, shape, Constants.MOD_ID + ":blocks/" + name + ".0", false);
		Proxies.render.setRenderChest(machineProperties, renderName);
		this.machineProperties = machineProperties;
	}

	@Override
	public IMachinePropertiesTesr<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
