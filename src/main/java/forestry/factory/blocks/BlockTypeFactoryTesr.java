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

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import forestry.core.blocks.BlockBase;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.blocks.IMachinePropertiesTesr;
import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.config.Constants;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileBase;
import forestry.core.tiles.TileMill;
import forestry.factory.features.FactoryTiles;
import forestry.modules.features.FeatureTileType;

public enum BlockTypeFactoryTesr implements IBlockTypeTesr {
	BOTTLER(() -> FactoryTiles.BOTTLER, "bottler"),
	CARPENTER(() -> FactoryTiles.CARPENTER, "carpenter"),
	CENTRIFUGE(() -> FactoryTiles.CENTRIFUGE, "centrifuge"),
	FERMENTER(() -> FactoryTiles.FERMENTER, "fermenter"),
	MOISTENER(() -> FactoryTiles.MOISTENER, "moistener"),
	SQUEEZER(() -> FactoryTiles.SQUEEZER, "squeezer"),
	STILL(() -> FactoryTiles.STILL, "still"),
	RAINMAKER(() -> FactoryTiles.RAINMAKER, "rainmaker", Constants.TEXTURE_PATH_BLOCK + "/rainmaker_");

	public static final BlockTypeFactoryTesr[] VALUES = values();

	private final IMachinePropertiesTesr<?> machineProperties;

	<T extends TileBase> BlockTypeFactoryTesr(Supplier<FeatureTileType<? extends T>> teClass, String name) {
		final VoxelShape nsBase = Block.box(2D, 2D, 4D, 14, 14, 12);
		final VoxelShape nsFront = Block.box(0D, 0D, 0D, 16, 16, 4);
		final VoxelShape nsBack = Block.box(0D, 0D, 12D, 16, 16, 16);
		final VoxelShape ns = Shapes.or(nsBase, nsFront, nsBack);
		final VoxelShape ewBase = Block.box(4D, 2D, 2D, 12, 14, 14);
		final VoxelShape ewFront = Block.box(0D, 0D, 0D, 4, 16, 16);
		final VoxelShape ewBack = Block.box(12D, 0D, 0D, 16, 16, 16);
		final VoxelShape ew = Shapes.or(ewBase, ewFront, ewBack);
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr.Builder<>(teClass, name)
				.setParticleTexture(name + ".0")
				.setShape((state, reader, pos, context) -> {
					Direction direction = state.getValue(BlockBase.FACING);
					return (direction == Direction.NORTH || direction == Direction.SOUTH) ? ns : ew;
				})
				.create();
		Proxies.render.setRenderDefaultMachine(machineProperties, Constants.TEXTURE_PATH_BLOCK + "/" + name + "_");
		this.machineProperties = machineProperties;
	}

	<T extends TileMill> BlockTypeFactoryTesr(Supplier<FeatureTileType<? extends T>> teClass, String name, String renderMillTexture) {
		final VoxelShape pedestal = Block.box(0D, 0D, 0D, 16, 1, 16);
		final VoxelShape column = Block.box(5D, 1D, 4D, 11, 16, 12);
		final VoxelShape extension = Block.box(1D, 8D, 7D, 15, 10, 9);
		MachinePropertiesTesr<T> machineProperties = new MachinePropertiesTesr.Builder<>(teClass, name)
				.setParticleTexture(name + ".0")
				.setShape(() -> Shapes.or(pedestal, column, extension))
				.create();
		Proxies.render.setRenderMill(machineProperties, renderMillTexture);
		this.machineProperties = machineProperties;
	}

	@Override
	public IMachinePropertiesTesr<?> getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getSerializedName() {
		return getMachineProperties().getSerializedName();
	}
}
