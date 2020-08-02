package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.gui.Drawable;
import forestry.core.utils.ResourceUtil;

public class TankElement extends GuiElement {
    /* Attributes - Final */
    @Nullable
    private final Drawable background;
    @Nullable
    private final Drawable overlay;
    private final FluidStack contents;
    private final int capacity;

    public TankElement(int xPos, int yPos, @Nullable Drawable background, FluidStack contents, int capacity) {
        this(xPos, yPos, background, contents, capacity, null);
    }

    public TankElement(int xPos, int yPos, @Nullable Drawable background, FluidStack contents, int capacity, @Nullable Drawable overlay) {
        this(xPos, yPos, background, contents, capacity, overlay, 16, 58);
    }

    public TankElement(int xPos, int yPos, @Nullable Drawable background, FluidStack contents, int capacity, @Nullable Drawable overlay, int width, int height) {
        super(xPos, yPos, width, height);
        this.background = background;
        this.contents = contents;
        this.capacity = capacity;
        this.overlay = overlay;
        addTooltip(((tooltip, element, mouseX, mouseY) -> {
            ToolTip toolTip = new ToolTip();
            int amount = contents.getAmount();
            Fluid fluidType = contents.getFluid();
            FluidAttributes attributes = fluidType.getAttributes();
            Rarity rarity = attributes.getRarity(contents);
            if (rarity == null) {
                rarity = Rarity.COMMON;
            }
            toolTip.translated(attributes.getTranslationKey(contents)).style(rarity.color);
            toolTip.translated("for.gui.tooltip.liquid.amount", amount, capacity);
        }));
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        if (background != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            background.draw(transform, 0, 0);
        }
        if (contents.isEmpty() || capacity <= 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        TextureManager textureManager = minecraft.getTextureManager();
        if (contents.getAmount() > 0 && contents.getFluid() != null) {
            Fluid fluid = contents.getFluid();
            FluidAttributes attributes = fluid.getAttributes();
            ResourceLocation fluidStill = fluid.getAttributes().getStillTexture(contents);
            TextureAtlasSprite fluidStillSprite = null;
            if (fluidStill != null) {
                fluidStillSprite = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(fluidStill);
            }
            if (fluidStillSprite == null) {
                fluidStillSprite = ResourceUtil.getMissingTexture();
            }

            int fluidColor = attributes.getColor(contents);

            int scaledAmount = contents.getAmount() * height / capacity;
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

            final int yStart = height;

            for (int xTile = 0; xTile <= xTileCount; xTile++) {
                for (int yTile = 0; yTile <= yTileCount; yTile++) {
                    int width = xTile == xTileCount ? xRemainder : 16;
                    int height = yTile == yTileCount ? yRemainder : 16;
                    int x = xTile * 16;
                    int y = yStart - (yTile + 1) * 16;
                    if (width > 0 && height > 0) {
                        int maskTop = 16 - height;
                        int maskRight = 16 - width;

                        drawFluidTexture(x, y, fluidStillSprite, maskTop, maskRight, 100);
                    }
                }
            }
        }

        if (overlay != null) {
            RenderSystem.disableDepthTest();
            overlay.draw(transform, 0, 0);
            RenderSystem.enableDepthTest();
        }

        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.disableAlphaTest();
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        RenderSystem.color4f(red, green, blue, 1.0F);
    }

    private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
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
}
