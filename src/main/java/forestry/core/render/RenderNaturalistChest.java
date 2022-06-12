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

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;

import forestry.core.blocks.BlockBase;
import forestry.core.tiles.TileNaturalistChest;

public class RenderNaturalistChest implements IForestryRenderer<TileNaturalistChest> {
	public static final ModelLayerLocation MODEL_LAYER = IForestryRenderer.register("naturalistChest");

	public RenderNaturalistChest(String textureName) {
	}

	@Override
	public void renderTile(TileNaturalistChest tile, RenderHelper helper) {
		Level worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getBlockPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.getValue(BlockBase.FACING);
			render(facing, tile.prevLidAngle, tile.lidAngle, helper, helper.partialTicks);
		}
	}

	@Override
	public void renderItem(ItemStack stack, RenderHelper helper) {
		render(Direction.SOUTH, 0, 0, helper, helper.partialTicks);
	}

	public void render(Direction orientation, float prevLidAngle, float lidAngle, RenderHelper helper, float partialTick) {
		helper.push();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		helper.translate(0.5D, 0.5D, 0.5D);

		helper.rotate(Vector3f.YP.rotationDegrees(-orientation.toYRot()));
		helper.translate(-0.5D, -0.5D, -0.5D);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		float rotation = -(angle * (float) Math.PI / 2.0F);

		helper.pop();
	}
}
