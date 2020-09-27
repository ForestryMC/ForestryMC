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
import forestry.core.fluids.ForestryFluids;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.awt.*;
import java.util.EnumMap;
import java.util.Locale;

public class RenderMachine implements IForestryRenderer<TileBase> {

    private final ModelRenderer basefront;
    private final ModelRenderer baseback;
    private final ModelRenderer resourceTank;
    private final ModelRenderer productTank;

    private final ResourceLocation textureBase;
    private final ResourceLocation textureResourceTank;
    private final ResourceLocation textureProductTank;

    private final EnumMap<EnumTankLevel, ResourceLocation> texturesTankLevels = new EnumMap<>(EnumTankLevel.class);

    public RenderMachine(String baseTexture) {
        int textureWidth = 64;
        int textureHeight = 32;

        basefront = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        basefront.addBox(-8F, -8F, -8F, 16, 4, 16);
        basefront.rotationPointX = 8;
        basefront.rotationPointY = 8;
        basefront.rotationPointZ = 8;

        baseback = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        baseback.addBox(-8F, 4F, -8F, 16, 4, 16);
        baseback.rotationPointX = 8;
        baseback.rotationPointY = 8;
        baseback.rotationPointZ = 8;

        resourceTank = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        resourceTank.addBox(-6F, -8F, -6F, 12, 16, 6);
        resourceTank.rotationPointX = 8;
        resourceTank.rotationPointY = 8;
        resourceTank.rotationPointZ = 8;

        productTank = new ModelRenderer(textureWidth, textureHeight, 0, 0);
        productTank.addBox(-6F, -8F, 0F, 12, 16, 6);
        productTank.rotationPointX = 8;
        productTank.rotationPointY = 8;
        productTank.rotationPointZ = 8;

        textureBase = new ResourceLocation(Constants.MOD_ID, baseTexture + "base.png");
        textureProductTank = new ResourceLocation(Constants.MOD_ID, baseTexture + "tank_product_empty.png");
        textureResourceTank = new ResourceLocation(Constants.MOD_ID, baseTexture + "tank_resource_empty.png");

        for (EnumTankLevel tankLevel : EnumTankLevel.values()) {
            if (tankLevel == EnumTankLevel.EMPTY) {
                continue;
            }
            String tankLevelString = tankLevel.toString().toLowerCase(Locale.ENGLISH);
            texturesTankLevels.put(
                    tankLevel,
                    new ResourceLocation(Constants.MOD_ID, "textures/block/machine_tank_" + tankLevelString + ".png")
            );
        }
    }

    @Override
    public void renderTile(TileBase tile, RenderHelper helper) {
        IRenderableTile generator = (IRenderableTile) tile;
        World worldObj = tile.getWorldObj();
        BlockState blockState = worldObj.getBlockState(tile.getPos());
        if (blockState.getBlock() instanceof BlockBase) {
            Direction facing = blockState.get(BlockBase.FACING);
            render(generator.getResourceTankInfo(), generator.getProductTankInfo(), facing, helper);
        }
    }

    @Override
    public void renderItem(ItemStack stack, RenderHelper helper) {
        render(TankRenderInfo.EMPTY, TankRenderInfo.EMPTY, Direction.SOUTH, helper);
    }

    private void render(
            TankRenderInfo resourceTankInfo,
            TankRenderInfo productTankInfo,
            Direction orientation,
            RenderHelper helper
    ) {
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
        helper.renderModel(textureBase, basefront, baseback);

        renderTank(resourceTank, textureResourceTank, resourceTankInfo, helper);
        renderTank(productTank, textureProductTank, productTankInfo, helper);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderTank(
            ModelRenderer tankModel,
            ResourceLocation textureBase,
            TankRenderInfo renderInfo,
            RenderHelper helper
    ) {
        helper.renderModel(textureBase, tankModel);

        ResourceLocation textureResourceTankLevel = texturesTankLevels.get(renderInfo.getLevel());
        if (textureResourceTankLevel == null) {
            return;
        }

        // TODO: render fluid overlay on tank
        ForestryFluids fluidDefinition = ForestryFluids.getFluidDefinition(renderInfo.getFluidStack());
        Color primaryTankColor = fluidDefinition == null ? Color.BLUE : fluidDefinition.getParticleColor();
        float[] colors = new float[3];
        primaryTankColor.getRGBColorComponents(colors);
        RenderSystem.color4f(colors[0], colors[1], colors[2], 1.0f);

        helper.renderModel(textureResourceTankLevel, tankModel);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
