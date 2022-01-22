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

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileAnalyzer;

public class RenderAnalyzer implements IForestryRenderer<TileAnalyzer> {

	public RenderAnalyzer() {
	}

	@Override
	public void renderTile(TileAnalyzer tile, RenderHelper helper) {
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), worldObj, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(ItemStack.EMPTY, null, Direction.WEST, helper);
	}

	private void render(ItemStack itemstack, @Nullable Level world, Direction orientation, RenderHelper helper) {
		Vector3f rotation = new Vector3f(0, 0, 0);
		switch (orientation) {
			case EAST:
				rotation.setY((float) Math.PI / 2);
				break;
			case WEST:
				rotation.setY((float) -Math.PI / 2);
				break;
			case SOUTH:
				break;
			case NORTH:
			default:
				rotation.setY((float) Math.PI);
				break;
		}
		helper.setRotation(rotation);
		helper.push();

		helper.pop();
		if (itemstack.isEmpty() || world == null) {
			return;
		}
		float renderScale = 1.0f;

		helper.push();
		helper.translate(0.5f, 0.2f, 0.5f);
		helper.scale(renderScale, renderScale, renderScale);

		helper.renderItem(itemstack, world);
		helper.pop();
	}

}
