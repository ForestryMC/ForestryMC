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
package forestry.core.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import forestry.api.core.IToolPipette;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.fluids.StandardTank;
import forestry.core.gui.IContainerLiquidTanks;
import forestry.core.utils.ResourceUtil;
import forestry.farming.gui.ContainerFarm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;

/**
 * Slot for liquid tanks
 */
@OnlyIn(Dist.CLIENT)
public class TankWidget extends Widget {

    protected boolean drawOverlay = true;
    private int overlayTexX = 176;
    private int overlayTexY = 0;
    private int slot = 0;

    public TankWidget(WidgetManager manager, int xPos, int yPos, int slot) {
        super(manager, xPos, yPos);
        this.slot = slot;
        this.height = 58;
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        RenderSystem.color4f(red, green, blue, 1.0F);
    }

    private static void drawFluidTexture(
            double xCoord,
            double yCoord,
            TextureAtlasSprite textureSprite,
            int maskTop,
            int maskRight,
            double zLevel
    ) {
        float uMin = textureSprite.getMinU();
        float uMax = textureSprite.getMaxU();
        float vMin = textureSprite.getMinV();
        float vMax = textureSprite.getMaxV();
        uMax = uMax - maskRight / 16.0F * (uMax - uMin);
        vMax = vMax - maskTop / 16.0F * (vMax - vMin);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        buffer.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        buffer.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        buffer.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    public TankWidget setOverlayOrigin(int x, int y) {
        overlayTexX = x;
        overlayTexY = y;
        return this;
    }

    @Nullable
    public IFluidTank getTank() {
        Container container = manager.gui.getContainer();
        if (container instanceof IContainerLiquidTanks) {
            return ((IContainerLiquidTanks) container).getTank(slot);
        } else if (container instanceof ContainerFarm) {
            return ((ContainerFarm) container).getTank(slot);
        }
        return null;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        RenderSystem.disableBlend();
        IFluidTank tank = getTank();
        if (tank == null || tank.getCapacity() <= 0) {
            return;
        }

        FluidStack contents = tank.getFluid();
        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();
        if (!contents.isEmpty() && contents.getAmount() > 0 && contents.getFluid() != null) {
            Fluid fluid = contents.getFluid();
            if (fluid != null) {
                ResourceLocation fluidStill = fluid.getAttributes().getStillTexture(contents);
                TextureAtlasSprite fluidStillSprite = null;
                if (fluidStill != null) {
                    fluidStillSprite = Minecraft.getInstance()
                                                .getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE)
                                                .apply(fluidStill);
                }
                if (fluidStillSprite == null) {
                    fluidStillSprite = ResourceUtil.getMissingTexture();
                }

                int fluidColor = fluid.getAttributes().getColor(contents);

                int scaledAmount = contents.getAmount() * height / tank.getCapacity();
                if (contents.getAmount() > 0 && scaledAmount < 1) {
                    scaledAmount = 1;
                }
                if (scaledAmount > height) {
                    scaledAmount = height;
                }

                textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                setGLColorFromInt(fluidColor);

                final int xTileCount = width / 16;
                final int xRemainder = width - xTileCount * 16;
                final int yTileCount = scaledAmount / 16;
                final int yRemainder = scaledAmount - yTileCount * 16;

                final int yStart = startY + height;

                for (int xTile = 0; xTile <= xTileCount; xTile++) {
                    for (int yTile = 0; yTile <= yTileCount; yTile++) {
                        int width = xTile == xTileCount ? xRemainder : 16;
                        int height = yTile == yTileCount ? yRemainder : 16;
                        int x = startX + xTile * 16;
                        int y = yStart - (yTile + 1) * 16;
                        if (width > 0 && height > 0) {
                            int maskTop = 16 - height;
                            int maskRight = 16 - width;

                            drawFluidTexture(x + xPos, y + yPos, fluidStillSprite, maskTop, maskRight, 100);
                        }
                    }
                }
            }
        }

        if (drawOverlay) {
            RenderSystem.enableAlphaTest();
            RenderSystem.disableDepthTest();
            textureManager.bindTexture(manager.gui.textureFile);
            manager.gui.blit(transform, startX + xPos, startY + yPos, overlayTexX, overlayTexY, 16, 60);
            RenderSystem.enableDepthTest();
            RenderSystem.disableAlphaTest();
        }

        RenderSystem.color4f(1, 1, 1, 1);
    }

    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        IFluidTank tank = getTank();
        if (!(tank instanceof StandardTank)) {
            return null;
        }
        StandardTank standardTank = (StandardTank) tank;
        return standardTank.getToolTip();
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
        PlayerEntity player = manager.minecraft.player;
        ItemStack itemstack = player.inventory.getItemStack();
        if (itemstack.isEmpty()) {
            return;
        }

        Item held = itemstack.getItem();
        Container container = manager.gui.getContainer();
        if (held instanceof IToolPipette && container instanceof IContainerLiquidTanks) {
            ((IContainerLiquidTanks) container).handlePipetteClickClient(slot, player);
        }
    }
}
