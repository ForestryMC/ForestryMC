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
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;

import forestry.apiculture.tiles.TileApiaristChest;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileForestry;
import forestry.core.tiles.TileNaturalistChest;

public enum BlockTypeApicultureTesr implements IBlockTypeTesr {
	APIARIST_CHEST(TileApiaristChest.class, "api_chest", Proxies.render.getRenderChest("apiaristchest"), TileNaturalistChest.chestBoundingBox);

	public static final BlockTypeApicultureTesr[] VALUES = values();

	@Nonnull
	private final IMachinePropertiesTesr<?> machineProperties;

	<T extends TileForestry> BlockTypeApicultureTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nullable AxisAlignedBB boundingBox) {
		if (boundingBox != null) {
			this.machineProperties = new MachinePropertiesTesr<>(teClass, name, renderer, boundingBox, Constants.RESOURCE_ID + ":blocks/" + name + ".0");
		} else {
			this.machineProperties = new MachinePropertiesTesr<>(teClass, name, renderer, Constants.RESOURCE_ID + ":blocks/" + name + ".0");
		}
	}

	@Nonnull
	@Override
	public IMachinePropertiesTesr<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getName() {
		return getMachineProperties().getName();
	}
}
