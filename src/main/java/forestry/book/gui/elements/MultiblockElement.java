/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Any alternate licenses are noted where appropriate.
 ******************************************************************************/
package forestry.book.gui.elements;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import forestry.api.gui.events.GuiEvent;
import forestry.book.data.structure.BlockData;
import forestry.book.data.structure.StructureBlockAccess;
import forestry.book.data.structure.StructureInfo;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.elements.GuiElement;

import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class MultiblockElement extends GuiElement {
	private float scale = 50.0F;
	private float xTranslate = 0F;
	private float yTranslate = 0F;
	private int tick = 0;

	private float rotX;
	private float rotY;

	private StructureInfo structureData;
	private StructureBlockAccess blockAccess;

	@Nullable
	private double[] lastClick = null;
	private int fullStructureSteps = 5;

	public MultiblockElement(int x, int y, int width, int height, int[] size, BlockData[] structure) {
		super(x, y, width, height);
		if (size.length == 3) {
			scale = 100f / (float) IntStream.of(size).max().getAsInt();

			float sx = (float) width / (float) GuiForesterBook.PAGE_WIDTH;
			float sy = (float) height / (float) GuiForesterBook.PAGE_HEIGHT;

			scale *= Math.min(sx, sy);

			xTranslate = x + (float) width / 2.0F;
			yTranslate = y + (float) height / 2.0F;
		}
		structureData = new StructureInfo(size[0], size[1], size[2], structure);
		blockAccess = new StructureBlockAccess(structureData);


		rotX = 25;
		rotY = -45;

		addSelfEventHandler(GuiEvent.DownEvent.class, event -> lastClick = new double[]{event.getX(), event.getY()});
		addSelfEventHandler(GuiEvent.UpEvent.class, event -> lastClick = null);
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		if (lastClick != null) {
			if (Minecraft.getInstance().mouseHelper.isLeftDown() || Minecraft.getInstance().mouseHelper.isRightDown()) {
				double dx = mouseX - lastClick[0];
				double dy = mouseY - lastClick[1];
				float maxSpeed = 10f;
				double changeY = Math.min(maxSpeed, dx / 10f);
				double changeX = Math.min(maxSpeed, dy / 10f);

				rotY += changeY;
				rotX += changeX;
			} else {
				lastClick = null;
			}
		}

		boolean canTick = false;
		if (canTick) {
			if (++tick % 20 == 0 && (structureData.canStep() || ++fullStructureSteps >= 5)) {
				structureData.step();
				fullStructureSteps = 0;
			}
		} else {
			structureData.reset();
			structureData.setShowLayer(9);
		}

		int structureLength = structureData.structureLength;
		int structureWidth = structureData.structureWidth;
		int structureHeight = structureData.structureHeight;

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();

		final BlockRendererDispatcher blockRender = Minecraft.getInstance().getBlockRendererDispatcher();
		final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

		GlStateManager.translatef(xTranslate, yTranslate, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		GlStateManager.scalef(scale, -scale, 1);
		GlStateManager.rotatef(rotX, 1, 0, 0);
		GlStateManager.rotatef(rotY, 0, 1, 0);

		GlStateManager.translatef((float) structureLength / -2f, (float) structureHeight / -2f, (float) structureWidth / -2f);

		GlStateManager.disableLighting();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		for (int h = 0; h < structureData.structureHeight; h++) {
			for (int l = 0; l < structureData.structureLength; l++) {
				for (int w = 0; w < structureData.structureWidth; w++) {
					BlockPos pos = new BlockPos(l, h, w);
					if (!blockAccess.isAirBlock(pos)) {
						BlockState state = blockAccess.getBlockState(pos);
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();
						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						blockRender.renderBlock(state, pos, blockAccess, buffer, new Random(), EmptyModelData.INSTANCE);
						tessellator.draw();
					}
				}
			}
		}
		GlStateManager.popMatrix();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
	}
}
