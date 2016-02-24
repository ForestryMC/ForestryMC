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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileBase;
import forestry.factory.tiles.TileBottler;
import forestry.factory.tiles.TileCarpenter;
import forestry.factory.tiles.TileCentrifuge;
import forestry.factory.tiles.TileFermenter;
import forestry.factory.tiles.TileMillRainmaker;
import forestry.factory.tiles.TileMoistener;
import forestry.factory.tiles.TileSqueezer;
import forestry.factory.tiles.TileStill;

public enum BlockTypeFactoryTesr implements IBlockTypeTesr {
	BOTTLER(0, TileBottler.class, "bottler"),
	CARPENTER(1, TileCarpenter.class, "carpenter"),
	CENTRIFUGE(2, TileCentrifuge.class, "centrifuge"),
	FERMENTER(3, TileFermenter.class, "fermenter"),
	MOISTENER(4, TileMoistener.class, "moistener"),
	SQUEEZER(5, TileSqueezer.class, "squeezer"),
	STILL(6, TileStill.class, "still"),
	RAINMAKER(7, TileMillRainmaker.class, "rainmaker", Proxies.render.getRenderMill(Constants.TEXTURE_PATH_BLOCKS + "/rainmaker_"));

	public static final BlockTypeFactoryTesr[] VALUES = values();

	@Nonnull
	private final IMachinePropertiesTesr<?> machineProperties;

	<T extends TileBase> BlockTypeFactoryTesr(int meta, @Nonnull Class<T> teClass, @Nonnull String name) {
		TileEntitySpecialRenderer<TileBase> renderer = Proxies.render.getRenderDefaultMachine(Constants.TEXTURE_PATH_BLOCKS + "/" + name + "_");
		this.machineProperties = new MachinePropertiesTesr<>(meta, teClass, name, renderer);
	}

	<T extends TileBase> BlockTypeFactoryTesr(int meta, @Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer) {
		this.machineProperties = new MachinePropertiesTesr<>(meta, teClass, name, renderer);
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
