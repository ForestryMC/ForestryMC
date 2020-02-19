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

import com.mojang.blaze3d.systems.RenderSystem;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileNaturalistChest;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderNaturalistChest implements IForestryRenderer<TileNaturalistChest> {

	private final ResourceLocation texture;

	public RenderNaturalistChest(String textureName) {
		texture = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/" + textureName + ".png");
	}

	@Override
    public void renderTile(TileNaturalistChest tile, RenderHelper helper) {
		World worldObj = tile.getWorldObj();
		BlockState blockState = worldObj.getBlockState(tile.getPos());
		if (blockState.getBlock() instanceof BlockBase) {
			Direction facing = blockState.get(BlockBase.FACING);
            render(facing, tile.prevLidAngle, tile.lidAngle, helper, helper.partialTicks);
		}
	}

	@Override
    public void renderItem(ItemStack stack, RenderHelper helper) {
        render(Direction.SOUTH, 0, 0, helper, helper.partialTicks);
    }

    public void render(Direction orientation, float prevLidAngle, float lidAngle, RenderHelper helper, float partialTick) {
        helper.push();
		bindTexture(texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        helper.translate(0, 1.0F, 1.0F);
        helper.scale(1.0F, -1.0F, -1.0F);
        helper.translate(0.5F, 0.5F, 0.5F);

		int rotation;
		switch (orientation) {
			case EAST:
				rotation = -90;
				break;
			case NORTH:
				rotation = 180;
				break;
			case WEST:
				rotation = 90;
				break;
			default:
			case SOUTH:
				rotation = 0;
				break;
		}

        RenderSystem.rotatef(rotation, 0.0F, 1.0F, 0.0F);
        helper.translate(-0.5F, -0.5F, -0.5F);

		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		/*chestModel.getLid().rotateAngleX = -(angle * (float) Math.PI / 2.0F);
		chestModel.renderAll();
		ChestTileEntityRenderer*/

        helper.pop();
	}
}
