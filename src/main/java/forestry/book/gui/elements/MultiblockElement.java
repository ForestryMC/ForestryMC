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
import java.util.stream.IntStream;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.gui.events.GuiEvent;
import forestry.book.data.structure.BlockData;
import forestry.book.data.structure.StructureBlockAccess;
import forestry.book.data.structure.StructureInfo;
import forestry.book.gui.GuiForesterBook;
import forestry.core.gui.elements.GuiElement;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class MultiblockElement extends GuiElement {
	private float scale = 50.0F;
	private float xTranslate = 0F;
	private float yTranslate = 0F;
	private boolean canTick = false;
	private int tick = 0;

	private float rotX = 0;
	private float rotY = 0;

	private StructureInfo structureData;
	private StructureBlockAccess blockAccess;

	@Nullable
	private int[] lastClick = null;
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

		addSelfEventHandler(GuiEvent.DownEvent.class, event -> lastClick = new int[]{event.getX(), event.getY()});
		addSelfEventHandler(GuiEvent.UpEvent.class, event -> lastClick = null);
	}

	@Override
	public boolean canMouseOver() {
		return true;
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		if (lastClick != null) {
			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
				int dx = mouseX - lastClick[0];
				int dy = mouseY - lastClick[1];
				float maxSpeed = 10f;
				float changeY = Math.min(maxSpeed, dx / 10f);
				float changeX = Math.min(maxSpeed, dy / 10f);

				rotY += changeY;
				rotX += changeX;
			} else {
				lastClick = null;
			}
		}

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

		final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		GlStateManager.translate(xTranslate, yTranslate, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		GlStateManager.scale(scale, -scale, 1);
		GlStateManager.rotate(rotX, 1, 0, 0);
		GlStateManager.rotate(rotY, 0, 1, 0);

		GlStateManager.translate((float) structureLength / -2f, (float) structureHeight / -2f, (float) structureWidth / -2f);

		GlStateManager.disableLighting();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		for (int h = 0; h < structureData.structureHeight; h++) {
			for (int l = 0; l < structureData.structureLength; l++) {
				for (int w = 0; w < structureData.structureWidth; w++) {
					BlockPos pos = new BlockPos(l, h, w);
					if (!blockAccess.isAirBlock(pos)) {
						IBlockState state = blockAccess.getBlockState(pos);
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();
						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						blockRender.renderBlock(state, pos, blockAccess, buffer);
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
