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
package forestry.core.render;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;

public class RenderMachine implements IForestryRenderer<TileBase> {

	public RenderMachine(String baseTexture) {
	}

	@Override
	public void renderTile(TileBase tile, RenderHelper helper) {
		IRenderableTile generator = (IRenderableTile) tile;
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(generator.getResourceTankInfo(), generator.getProductTankInfo(), facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(TankRenderInfo.EMPTY, TankRenderInfo.EMPTY, Direction.SOUTH, helper);
	}

	private void render(TankRenderInfo resourceTankInfo, TankRenderInfo productTankInfo, Direction orientation, RenderHelper helper) {
		Vector3f rotation = new Vector3f(0, 0, 0);

		switch (orientation) {
			case EAST:
				rotation.set(0, (float) Math.PI, (float) -Math.PI / 2);
				break;
			case WEST:
				rotation.set(0, 0, (float) Math.PI / 2);
				break;
			case UP:
				break;
			case DOWN:
				rotation.set(0, 0, (float) Math.PI);
				break;
			case SOUTH:
				rotation.set((float) Math.PI / 2, 0, (float) Math.PI / 2);
				break;
			case NORTH:
			default:
				rotation.set((float) -Math.PI / 2, 0, (float) Math.PI / 2);
				break;
		}

		helper.setRotation(rotation);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
