package forestry.core.gui.elements;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraftforge.fluids.FluidType;
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
			FluidType attributes = fluidType.getFluidType();
			Rarity rarity = attributes.getRarity(contents);
			if (rarity == null) {
				rarity = Rarity.COMMON;
			}
			toolTip.add(contents.getDisplayName().copy().withStyle(rarity.getStyleModifier()));
			toolTip.translated("for.gui.tooltip.liquid.amount", amount, capacity);
		}));
	}

	@Override
	public void drawElement(PoseStack transform, int mouseX, int mouseY) {
		RenderSystem.disableBlend();
		// RenderSystem.enableAlphaTest();
		if (background != null) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			background.draw(transform, 0, 0);
		}
		if (contents.isEmpty() || capacity <= 0) {
			return;
		}


		if (contents.getAmount() > 0 && contents.getFluid() != null) {
			Fluid fluid = contents.getFluid();
			FluidType attributes = fluid.getFluidType();
			ResourceLocation fluidStill = fluid.getFluidType().getStillTexture(contents);
			TextureAtlasSprite fluidStillSprite = null;
			if (fluidStill != null) {
				fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
			}
			if (fluidStillSprite == null) {
				fluidStillSprite = ResourceUtil.getMissingTexture();
			}

			int fluidColor = attributes.getColor(contents);

			int scaledAmount = contents.getAmount() * getHeight() / capacity;
			if (contents.getAmount() > 0 && scaledAmount < 1) {
				scaledAmount = 1;
			}
			if (scaledAmount > getHeight()) {
				scaledAmount = getHeight();
			}

			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
			setGLColorFromInt(fluidColor);

			final int xTileCount = getWidth() / 16;
			final int xRemainder = getWidth() - xTileCount * 16;
			final int yTileCount = scaledAmount / 16;
			final int yRemainder = scaledAmount - yTileCount * 16;

			final int yStart = getHeight();

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

		RenderSystem.setShaderColor(1, 1, 1, 1);
		// RenderSystem.disableAlphaTest();
	}

	private static void setGLColorFromInt(int color) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;

		RenderSystem.setShaderColor(red, green, blue, 1.0F);
	}

	private static void drawFluidTexture(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
		float uMin = textureSprite.getU0();
		float uMax = textureSprite.getU1();
		float vMin = textureSprite.getV0();
		float vMax = textureSprite.getV1();
		uMax = uMax - maskRight / 16.0F * (uMax - uMin);
		vMax = vMax - maskTop / 16.0F * (vMax - vMin);

		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
		buffer.vertex(xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
		buffer.vertex(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
		buffer.vertex(xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
		tessellator.end();
	}
}
