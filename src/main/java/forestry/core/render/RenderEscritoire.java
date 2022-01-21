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

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileEscritoire;

public class RenderEscritoire implements IForestryRenderer<TileEscritoire> {

	private static final ResourceLocation TEXTURE = new ForestryResource(Constants.TEXTURE_PATH_BLOCK + "/escritoire.png");

	//renderers
	private final ModelPart desk;
	private final ModelPart standRB;
	private final ModelPart standRF;
	private final ModelPart standLB;
	private final ModelPart standLF;
	private final ModelPart drawers;
	private final ModelPart standLowLF;
	private final ModelPart standLowRB;
	private final ModelPart standLowRF;
	private final ModelPart standLowLB;

	public RenderEscritoire() {
		int textureWidth = 64;
		int textureHeight = 32;

		desk = new ModelPart(textureWidth, textureHeight, 0, 0);
		desk.addBox(-8F, 3F, -7.8F, 16, 2, 15);
		desk.setPos(0F, 0F, 0F);
		desk.setTexSize(64, 32);
		desk.mirror = true;
		setRotation(desk, 0.0872665f, 0f, 0f);
		standRB = new ModelPart(textureWidth, textureHeight, 38, 18);
		standRB.addBox(5F, 4F, 5F, 2, 6, 2);
		standRB.setPos(0F, 0F, 0F);
		standRB.setTexSize(64, 32);
		standRB.mirror = true;
		setRotation(standRB, 0F, 0F, 0F);
		standRF = new ModelPart(textureWidth, textureHeight, 38, 18);
		standRF.addBox(5F, 4F, -7F, 2, 6, 2);
		standRF.setPos(0F, 0F, 0F);
		standRF.setTexSize(64, 32);
		standRF.mirror = true;
		setRotation(standRF, 0F, 0F, 0F);
		standLB = new ModelPart(textureWidth, textureHeight, 38, 18);
		standLB.addBox(-7F, 4F, 5F, 2, 6, 2);
		standLB.setPos(0F, 0F, 0F);
		standLB.setTexSize(64, 32);
		standLB.mirror = true;
		setRotation(standLB, 0F, 0F, 0F);
		standLF = new ModelPart(textureWidth, textureHeight, 38, 18);
		standLF.addBox(-7F, 4F, -7F, 2, 6, 2);
		standLF.setPos(0F, 0F, 0F);
		standLF.setTexSize(64, 32);
		standLF.mirror = true;
		setRotation(standLF, 0F, 0F, 0F);
		drawers = new ModelPart(textureWidth, textureHeight, 0, 18);
		drawers.addBox(-7.5F, -2F, 4.5F, 15, 5, 3);
		drawers.setPos(0F, 0F, 0F);
		drawers.setTexSize(64, 32);
		drawers.mirror = true;
		setRotation(drawers, 0F, 0F, 0F);
		standLowLF = new ModelPart(textureWidth, textureHeight, 0, 26);
		standLowLF.addBox(-6.5F, 10F, -6.5F, 1, 4, 1);
		standLowLF.setPos(0F, 0F, 0F);
		standLowLF.setTexSize(64, 32);
		standLowLF.mirror = true;
		setRotation(standLowLF, 0F, 0F, 0F);
		standLowRB = new ModelPart(textureWidth, textureHeight, 0, 26);
		standLowRB.addBox(5.5F, 10F, 5.5F, 1, 4, 1);
		standLowRB.setPos(0F, 0F, 0F);
		standLowRB.setTexSize(64, 32);
		standLowRB.mirror = true;
		setRotation(standLowRB, 0F, 0F, 0F);
		standLowRF = new ModelPart(textureWidth, textureHeight, 0, 26);
		standLowRF.addBox(5.5F, 10F, -6.5F, 1, 4, 1);
		standLowRF.setPos(0F, 0F, 0F);
		standLowRF.setTexSize(64, 32);
		standLowRF.mirror = true;
		setRotation(standLowRF, 0F, 0F, 0F);
		standLowLB = new ModelPart(textureWidth, textureHeight, 0, 26);
		standLowLB.addBox(-6.5F, 10F, 5.5F, 1, 4, 1);
		standLowLB.setPos(0F, 0F, 0F);
		standLowLB.setTexSize(64, 32);
		standLowLB.mirror = true;
		setRotation(standLowLB, 0F, 0F, 0F);
	}

	private static void setRotation(ModelPart model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
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
			helper.renderModel(TEXTURE, new Vector3f(0.0872665F, 0, 0), desk);
			helper.renderModel(TEXTURE,
				standRB, standRF, standLB, standLF, drawers, standLowLF, standLowRB, standLowRF, standLowLB);
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
