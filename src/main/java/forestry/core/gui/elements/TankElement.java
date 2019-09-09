package forestry.core.gui.elements;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import forestry.core.gui.Drawable;

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
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		if (background != null) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			background.draw(0, 0);
		}
		if (contents.isEmpty() || capacity <= 0) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		TextureManager textureManager = minecraft.getTextureManager();
		if (contents.getAmount() > 0 && contents.getFluid() != null) {
			Fluid fluid = contents.getFluid();
			FluidAttributes attributes = fluid.getAttributes();
			AtlasTexture textureMapBlocks = minecraft.getTextureMap();
			ResourceLocation fluidStill = fluid.getAttributes().getStill(contents);
			TextureAtlasSprite fluidStillSprite = null;
			if (fluidStill != null) {
				fluidStillSprite = textureMapBlocks.getSprite(fluidStill);
			}
			if (fluidStillSprite == null) {
				fluidStillSprite = textureMapBlocks.missingImage;
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
			GlStateManager.disableDepthTest();
			overlay.draw(0, 0);
			GlStateManager.enableDepthTest();
		}

		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.disableAlphaTest();
	}

	@Override
	public List<ITextComponent> getTooltip(int mouseX, int mouseY) {
		if (contents.isEmpty()) {
			return Collections.emptyList();
		}
		List<ITextComponent> toolTip = new ArrayList<>();
		int amount = contents.getAmount();
		Fluid fluidType = contents.getFluid();
		FluidAttributes attributes = fluidType.getAttributes();
		Rarity rarity = attributes.getRarity(contents);
		if (rarity == null) {
			rarity = Rarity.COMMON;
		}
		toolTip.add(new TranslationTextComponent(attributes.getTranslationKey(contents)).setStyle((new Style()).setColor(rarity.color)));
		toolTip.add(new TranslationTextComponent("for.gui.tooltip.liquid.amount", amount, capacity));
		return toolTip;
	}

	@Override
	public boolean hasTooltip() {
		return true;
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		GlStateManager.color4f(red, green, blue, 1.0F);
	}

	private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		double uMin = textureSprite.getMinU();
		double uMax = textureSprite.getMaxU();
		double vMin = textureSprite.getMinV();
		double vMax = textureSprite.getMaxV();
		uMax = uMax - maskRight / 16.0 * (uMax - uMin);
		vMax = vMax - maskTop / 16.0 * (vMax - vMin);

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
