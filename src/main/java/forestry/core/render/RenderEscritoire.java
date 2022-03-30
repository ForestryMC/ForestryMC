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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileEscritoire;

public class RenderEscritoire implements IForestryRenderer<TileEscritoire> {

	private static final ResourceLocation TEXTURE = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/escritoire.png");

	public RenderEscritoire() {
	}

	@Override
	public void renderTile(TileEscritoire tile, RenderHelper helper) {
		Level world = tile.getWorldObj();
		BlockState blockState = world.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(tile.getIndividualOnDisplay(), world, facing, helper);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(ItemStack.EMPTY, null, Direction.SOUTH, helper);
	}

	private void render(ItemStack itemstack, @Nullable Level world, Direction orientation, RenderHelper helper) {
		helper.push();
		{
			helper.translate(0.5f, 0.875f, 0.5f);

			Vector3f rotation = new Vector3f((float) Math.PI, 0.0f, 0.0f);

			switch (orientation) {
				case EAST:
					rotation.setY((float) Math.PI / 2);
					break;
				case SOUTH:
					break;
				case NORTH:
					rotation.setY((float) Math.PI);
					break;
				case WEST:
				default:
					rotation.setY((float) -Math.PI / 2);
					break;
			}
			helper.setRotation(rotation);
		}
		helper.pop();

		if (!itemstack.isEmpty() && world != null) {

			float renderScale = 0.75f;

			helper.push();
			{
				helper.translate(0.5f, 0.6f, 0.5f);
				helper.scale(renderScale, renderScale, renderScale);
				helper.renderItem(itemstack, world);
			}
			helper.pop();
		}
	}
}
