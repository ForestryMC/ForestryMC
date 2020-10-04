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

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileEscritoire;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderEscritoire implements IForestryRenderer<TileEscritoire> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Constants.MOD_ID,
            Constants.TEXTURE_PATH_BLOCK + "escritoire.png"
    );

    //renderers
    private final ModelRenderer desk;
    private final ModelRenderer standRB;
    private final ModelRenderer standRF;
    private final ModelRenderer standLB;
    private final ModelRenderer standLF;
    private final ModelRenderer drawers;
    private final ModelRenderer standLowLF;
    private final ModelRenderer standLowRB;
    private final ModelRenderer standLowRF;
    private final ModelRenderer standLowLB;

    public RenderEscritoire() {
        int textureWidth = 64;
        int textureHeight = 32;

        desk = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        desk.addBox(-8F, 3F, -7.8F, 16, 2, 15);
        desk.setRotationPoint(0F, 0F, 0F);
        desk.setTextureSize(64, 32);
        desk.mirror = true;
        setRotation(desk, 0.0872665f, 0f, 0f);
        standRB = new ModelRenderer(textureWidth, textureHeight, 38, 18);
        standRB.addBox(5F, 4F, 5F, 2, 6, 2);
        standRB.setRotationPoint(0F, 0F, 0F);
        standRB.setTextureSize(64, 32);
        standRB.mirror = true;
        setRotation(standRB, 0F, 0F, 0F);
        standRF = new ModelRenderer(textureWidth, textureHeight, 38, 18);
        standRF.addBox(5F, 4F, -7F, 2, 6, 2);
        standRF.setRotationPoint(0F, 0F, 0F);
        standRF.setTextureSize(64, 32);
        standRF.mirror = true;
        setRotation(standRF, 0F, 0F, 0F);
        standLB = new ModelRenderer(textureWidth, textureHeight, 38, 18);
        standLB.addBox(-7F, 4F, 5F, 2, 6, 2);
        standLB.setRotationPoint(0F, 0F, 0F);
        standLB.setTextureSize(64, 32);
        standLB.mirror = true;
        setRotation(standLB, 0F, 0F, 0F);
        standLF = new ModelRenderer(textureWidth, textureHeight, 38, 18);
        standLF.addBox(-7F, 4F, -7F, 2, 6, 2);
        standLF.setRotationPoint(0F, 0F, 0F);
        standLF.setTextureSize(64, 32);
        standLF.mirror = true;
        setRotation(standLF, 0F, 0F, 0F);
        drawers = new ModelRenderer(textureWidth, textureHeight, 0, 18);
        drawers.addBox(-7.5F, -2F, 4.5F, 15, 5, 3);
        drawers.setRotationPoint(0F, 0F, 0F);
        drawers.setTextureSize(64, 32);
        drawers.mirror = true;
        setRotation(drawers, 0F, 0F, 0F);
        standLowLF = new ModelRenderer(textureWidth, textureHeight, 0, 26);
        standLowLF.addBox(-6.5F, 10F, -6.5F, 1, 4, 1);
        standLowLF.setRotationPoint(0F, 0F, 0F);
        standLowLF.setTextureSize(64, 32);
        standLowLF.mirror = true;
        setRotation(standLowLF, 0F, 0F, 0F);
        standLowRB = new ModelRenderer(textureWidth, textureHeight, 0, 26);
        standLowRB.addBox(5.5F, 10F, 5.5F, 1, 4, 1);
        standLowRB.setRotationPoint(0F, 0F, 0F);
        standLowRB.setTextureSize(64, 32);
        standLowRB.mirror = true;
        setRotation(standLowRB, 0F, 0F, 0F);
        standLowRF = new ModelRenderer(textureWidth, textureHeight, 0, 26);
        standLowRF.addBox(5.5F, 10F, -6.5F, 1, 4, 1);
        standLowRF.setRotationPoint(0F, 0F, 0F);
        standLowRF.setTextureSize(64, 32);
        standLowRF.mirror = true;
        setRotation(standLowRF, 0F, 0F, 0F);
        standLowLB = new ModelRenderer(textureWidth, textureHeight, 0, 26);
        standLowLB.addBox(-6.5F, 10F, 5.5F, 1, 4, 1);
        standLowLB.setRotationPoint(0F, 0F, 0F);
        standLowLB.setTextureSize(64, 32);
        standLowLB.mirror = true;
        setRotation(standLowLB, 0F, 0F, 0F);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void renderTile(TileEscritoire tile, RenderHelper helper) {
        World world = tile.getWorldObj();
        BlockState blockState = world.getBlockState(tile.getPos());
        if (blockState.getBlock() instanceof BlockBase) {
            Direction facing = blockState.get(BlockBase.FACING);
            render(tile.getIndividualOnDisplay(), world, facing, helper);
        }
    }

    @Override
    public void renderItem(ItemStack stack, RenderHelper helper) {
        render(ItemStack.EMPTY, null, Direction.SOUTH, helper);
    }

    private void render(ItemStack itemstack, @Nullable World world, Direction orientation, RenderHelper helper) {
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
                    standRB, standRF, standLB, standLF, drawers, standLowLF, standLowRB, standLowRF, standLowLB
            );
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
