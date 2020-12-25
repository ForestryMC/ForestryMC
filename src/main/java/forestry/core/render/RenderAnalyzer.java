/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.render;

import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileAnalyzer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RenderAnalyzer implements IForestryRenderer<TileAnalyzer> {

    private final ModelRenderer pedestal;
    private final ModelRenderer cover;
    private final ModelRenderer tower1;
    private final ModelRenderer tower2;

    private final ResourceLocation[] textures;

    public RenderAnalyzer() {
        int textureWidth = 64;
        int textureHeight = 32;

        textures = new ResourceLocation[]{
                new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_BLOCK + "analyzer_pedestal.png"),
                new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_BLOCK + "analyzer_tower1.png"),
                new ResourceLocation(Constants.MOD_ID, Constants.TEXTURE_PATH_BLOCK + "analyzer_tower2.png"),
                };

        pedestal = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        pedestal.addBox(-8F, -8F, -8F, 16, 1, 16);
        pedestal.setRotationPoint(8, 8, 8);

        cover = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        cover.addBox(-8F, -8F, -8F, 16, 1, 16);
        cover.setRotationPoint(8, 8, 8);

        tower1 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        tower1.addBox(-8, -7, -7, 2, 14, 14);
        tower1.setRotationPoint(8, 8, 8);

        tower2 = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        tower2.addBox(6, -7, -7, 2, 14, 14);
        tower2.setRotationPoint(8, 8, 8);
    }

    @Override
    public void renderTile(TileAnalyzer tile, RenderHelper helper) {
        World worldObj = tile.getWorldObj();
        BlockState blockState = worldObj.getBlockState(tile.getPos());
        if (blockState.getBlock() instanceof BlockBase) {
            Direction facing = blockState.get(BlockBase.FACING);
            render(tile.getIndividualOnDisplay(), worldObj, facing, helper);
        }
    }

    @Override
    public void renderItem(ItemStack stack, RenderHelper helper) {
        render(ItemStack.EMPTY, null, Direction.WEST, helper);
    }

    private void render(ItemStack itemstack, @Nullable World world, Direction orientation, RenderHelper helper) {
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

        helper.renderModel(textures[0], pedestal);
        helper.renderModel(textures[0], new Vector3f(0, 0, (float) Math.PI), cover);

        helper.renderModel(textures[1], tower1);

        helper.renderModel(textures[2], tower2);
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
