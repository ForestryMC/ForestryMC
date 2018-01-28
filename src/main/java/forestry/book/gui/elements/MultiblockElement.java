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
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import forestry.book.data.BlockData;
import forestry.book.data.StructureBlockAccess;
import forestry.book.data.StructureInfo;
import forestry.core.gui.elements.GuiElement;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MultiblockElement extends GuiElement {
	public static final int BUTTON_ID_LAYER_UP = 0;
	public static final int BUTTON_ID_LAYER_DOWN = 1;
	public static final int BUTTON_ID_ANIMATE = 2;

	private float scale = 50.0F;
	private float xTranslate = 0F;
	private float yTranslate = 0F;

	private float w = 0F;
	private float h = 0F;

	public MultiblockElement(int x, int y, int width, int height, int[] size, BlockData[] structure) {
		super(x, y, width, height);
		if(size.length == 3) {
			scale = 100f / (float) IntStream.of(size).max().getAsInt();

			float sx = (float)width / (float) 108;
			float sy = (float)height / (float) 155;

			scale *= Math.min(sx, sy);

			xTranslate = x + width / 2;// - (size[0] * scale) / 2;
			yTranslate = y + height / 2;// - (size[1] * scale) / 2;

			w = size[0] * scale;
			h = size[1] * scale;
		}
		init(size, structure);
	}

	boolean canTick = false;
	int tick = 0;

	float rotX = 0;
	float rotY = 0;
	float rotZ = 0;

	public StructureInfo structureData;
	StructureBlockAccess blockAccess;

	public void init(int[] size, BlockData[] data) {
		int yOff = 0;

		structureData = new StructureInfo(size[0], size[1], size[2], data);
		blockAccess = new StructureBlockAccess(structureData);


		rotX = 25;
		rotY = -45;
/*
    boolean canRenderFormed = multiblock.canRenderFormedStructure();
    //			yOff = (structureHeight-1)*12+structureWidth*5+structureLength*5+16;
    //			yOff = Math.max(48, yOff);
    float f = (float)Math.sqrt(structureHeight*structureHeight + structureWidth*structureWidth + structureLength*structureLength);
    float scale = multiblock.getManualScale();
    yOff = (int)(multiblock.getManualScale()*Math.sqrt(structureHeight*structureHeight + structureWidth*structureWidth + structureLength*structureLength));
    yOff = Math.max(10+(canRenderFormed?12:0)+(structureHeight>1?36:0), yOff);
    yOff = 10+Math.max(10+(multiblock.canRenderFormedStructure()?12:0)+(structureHeight>1?36:0), (int) (f*scale));
    pageButtons.add(new GuiButtonManualNavigation(gui, 100, x+4,y+yOff/2-(canRenderFormed?11:5), 10,10, 4));
    if(canRenderFormed)
      pageButtons.add(new GuiButtonManualNavigation(gui, 103, x+4,y+yOff/2+1, 10,10, 6));
    if(structureHeight>1)
    {
      pageButtons.add(new GuiButtonManualNavigation(gui, 101, x+4,y+yOff/2-(canRenderFormed?14:8)-16, 10,16, 3));
      pageButtons.add(new GuiButtonManualNavigation(gui, 102, x+4,y+yOff/2+(canRenderFormed?14:8), 10,16, 2));
    }
/*
    IngredientStack[] totalMaterials = this.multiblock.getTotalMaterials();
    if(false && false)
    {
      componentTooltip = new ArrayList();
      componentTooltip.add(I18n.format("desc.immersiveengineering.info.reqMaterial"));
      int maxOff = 1;
      boolean hasAnyItems = false;
      boolean[] hasItems = new boolean[totalMaterials.length];
      for(int ss = 0; ss < totalMaterials.length; ss++)
        if(totalMaterials[ss] != null)
        {
          IngredientStack req = totalMaterials[ss];
          int reqSize = req.inputSize;
          for(int slot = 0; slot < ManualUtils.mc().thePlayer.inventory.getSizeInventory(); slot++)
          {
            ItemStack inSlot = ManualUtils.mc().thePlayer.inventory.getStackInSlot(slot);
            if(inSlot != null && req.matchesItemStackIgnoringSize(inSlot))
              if((reqSize -= inSlot.stackSize) <= 0)
                break;
          }
          if(reqSize <= 0)
          {
            hasItems[ss] = true;
            if(!hasAnyItems)
              hasAnyItems = true;
          }
          maxOff = Math.max(maxOff, ("" + req.inputSize).length());
        }
      for(int ss = 0; ss < totalMaterials.length; ss++)
        if(totalMaterials[ss] != null)
        {
          IngredientStack req = totalMaterials[ss];
          int indent = maxOff - ("" + req.inputSize).length();
          String sIndent = "";
          if(indent > 0)
            for(int ii = 0; ii < indent; ii++)
              sIndent += "0";
          String s = hasItems[ss] ? (TextFormatting.GREEN + TextFormatting.BOLD.toString() + "\u2713" + TextFormatting.RESET + " ") : hasAnyItems ? ("   ") : "";
          s += TextFormatting.GRAY + sIndent + req.inputSize + "x " + TextFormatting.RESET;
          ItemStack example = req.getExampleStack();
          if(example != null)
            s += example.getRarity().rarityColor + example.getDisplayName();
          else
            s += "???";
          componentTooltip.add(s);
        }
    }*/
		//    super.initPage(gui, x, y+yOff, pageButtons);
	}

	@Nullable
	int[] lastClick = null;
	private int fullStructureSteps = 5;

	@Override
	public void drawElement(int mouseX, int mouseY) {
		if(lastClick != null) {
			if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
				int dx = mouseX - lastClick[0];
				int dy = mouseY - lastClick[1];
				float maxSpeed = 10f;
				float changeX = Math.min(maxSpeed, dx / 10f);
				float changeY = Math.min(maxSpeed, dy / 10f);

				rotY += changeX;
				rotX += changeY;
			}
			else {
				lastClick = null;
			}
		}

		if(canTick) {
			if(++tick % 20 == 0) {
				if(structureData.canStep() || ++fullStructureSteps >= 5) {
					structureData.step();
					fullStructureSteps = 0;
				}
			}
		}
		else {
			structureData.reset();
			structureData.setShowLayer(9);
		}

		int structureLength = structureData.structureLength;
		int structureWidth = structureData.structureWidth;
		int structureHeight = structureData.structureHeight;

		int xHalf = (structureWidth * 5 - structureLength * 5);
		int yOffPartial = (structureHeight - 1) * 16 + structureWidth * 8 + structureLength * 8;
		int yOffTotal = Math.max(52, yOffPartial + 16);

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		//			GL11.glEnable(GL11.GL_DEPTH_TEST);
		//			GL11.glDepthFunc(GL11.GL_ALWAYS);
		//			GL11.glDisable(GL11.GL_CULL_FACE);
		int i = 0;
		ItemStack highlighted = null;

		final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

		float f = (float) Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);
		yOffTotal = 10 + Math.max(10 + (structureHeight > 1 ? 36 : 0), (int) (f * scale));
		//GlStateManager.translate(x + 60, y + 10 + f / 2 * scale, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		GlStateManager.translate(xTranslate, yTranslate, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		// todo: translate where it actually needs to be and to counter z-layer of the book
		GlStateManager.scale(scale, -scale, 1);
		GlStateManager.rotate(rotX, 1, 0, 0);
		GlStateManager.rotate(rotY, 0, 1, 0);

		GlStateManager.translate((float)structureLength/-2f, (float)structureHeight/-2f, (float)structureWidth/-2f);

		GlStateManager.disableLighting();

		if(Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		}
		else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		if(structureWidth % 2 == 1) {
			//GlStateManager.translate(-.5f, 0, 0);
		}
		int iterator = 0;

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		for(int h = 0; h < structureData.structureHeight; h++) {
			for(int l = 0; l < structureData.structureLength; l++) {
				for(int w = 0; w < structureData.structureWidth; w++) {
					BlockPos pos = new BlockPos(l, h, w);
					if(!blockAccess.isAirBlock(pos)) {
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
		//			GL11.glTranslated(0, 0, -i);
		GlStateManager.popMatrix();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
/*
    fontRenderer.setUnicodeFlag(true);
    //if(localizedText!=null&&!localizedText.isEmpty())
    //fontRenderer.drawSplitString(localizedText, x,y+yOffTotal, 120, manual.getTextColour());

    fontRenderer.setUnicodeFlag(false);
    if(componentTooltip != null) {
      //fontRenderer.drawString("?", x + 116, y + yOffTotal / 2 - 4, manual.getTextColour(), false);
      fontRenderer.drawString("?", x + 116, y + yOffTotal / 2 - 4, 0x000000, false);
      if(mouseX >= x + 116 && mouseX < x + 122 && mouseY >= y + yOffTotal / 2 - 4 && mouseY < y + yOffTotal / 2 + 4) {
        this.drawHoveringText(componentTooltip, mouseX, mouseY, fontRenderer);
      }
    }*/
	}

	private int lastX;
	private int lastY;

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		//lastX = mouseX;
		//lastY = mouseY;
		lastClick = new int[] {mouseX, mouseY};
	}

	@Override
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton) {
		int dx = mouseX - lastX;
		int dy = mouseX - lastY;

		float maxSpeed = 1f;
		float changeX = Math.min(maxSpeed, dx/100f);
		float changeY = Math.min(maxSpeed, dy/100f);

		//rotX += changeX;
		//rotY += changeX;

		//rotY = rotY + (dx / 104f) * 10;
		//rotX = rotX + (dy / 100f) * 10;

		//lastX = mouseX;
		//lastY = mouseY;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int clickedMouseButton) {
		super.mouseReleased(mouseX, mouseY, clickedMouseButton);
		lastClick = null;
	}

	/*@Override
	public void mouseDragged(int clickX, int clickY, int mx, int my, int lastX, int lastY, int button) {
		//if((clickX >= 40 && clickX < 144 && mx >= 20 && mx < 164) && (clickY >= 30 && clickY < 130 && my >= 30 && my < 180)) {
		int dx = mx - lastX;
		int dy = my - lastY;
		rotY = rotY + (dx / 104f);// * 80;
		rotX = rotX + (dy / 100f);// * 80;
		//}
	}*/
/*
  @Override
  public void buttonPressed(GuiManual gui, GuiButton button)
  {
    if(button.id==100)
    {
      canTick = !canTick;
      ((GuiButtonManualNavigation)button).type = ((GuiButtonManualNavigation)button).type == 4 ? 5 : 4;
    }
    else if(button.id==101)
    {
      showLayer = Math.min(showLayer+1, structureHeight-1);
      tick= (countPerLevel[showLayer])*40;
    }
    else if(button.id==102)
    {
      showLayer = Math.max(showLayer-1, -1);
      tick= (showLayer==-1?blockCount:countPerLevel[showLayer])*40;
    }
    else if(button.id==103)
      showCompleted = !showCompleted;
    super.buttonPressed(gui, button);
  }
*/

	/*public void changeActiveLayer(int direction) {

	}

	@Override
	public boolean onButtonClick(int buttonId, ElementButton button) {
		switch(buttonId) {
			case BUTTON_ID_ANIMATE:
				this.canTick = !this.canTick;
				return true;
		}
		return false;
	}*/

	/*private final MultiblockData data;
	private final MultiblockBlockAccess blockAccess;
	private boolean canTick = true;
	private int tick = 0;
	private int fullStructureSteps = 5;

	public MultiblockElement(int xPos, int yPos, IMultiblockBlueprint bluePrint) {
		super(xPos, yPos, 100, 1000);
		this.data = new MultiblockData(bluePrint.getBlockStates(), bluePrint.getXSize(), bluePrint.getYSize(), bluePrint.getZSize());
		this.blockAccess = new MultiblockBlockAccess(data);
	}

	@Override
	public void drawElement(int mouseX, int mouseY) {
		if (canTick) {
			if (++tick % 20 == 0) {
				if (data.canStep() || ++fullStructureSteps >= 5) {
					data.step();
					fullStructureSteps = 0;
				}
			}
		} else {
			data.reset();
		}

		int structureLength = data.length;
		int structureWidth = data.width;
		int structureHeight = data.height;

		int xHalf = (structureWidth * 5 - structureLength * 5);
		int yOffPartial = (structureHeight - 1) * 16 + structureWidth * 8 + structureLength * 8;
		int yOffTotal = Math.max(52, yOffPartial + 16);

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		final BlockRendererDispatcher blockRender = Minecraft.getMinecraft().getBlockRendererDispatcher();

		float f = (float) Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength);
		yOffTotal = 10 + Math.max(10 + (structureHeight > 1 ? 36 : 0), (int) (f * 50.0F));
		//GlStateManager.translate(x + 60, y + 10 + f / 2 * scale, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		GlStateManager.translate(108 / 2 + structureWidth / 2, 165 / 2 + (float) Math.sqrt(structureHeight * structureHeight + structureWidth * structureWidth + structureLength * structureLength) / 2, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
		// todo: translate where it actually needs to be and to counter z-layer of the book
		GlStateManager.scale(20.0F, -20.0F, 1);
		GlStateManager.rotate(25, 1, 0, 0);
		GlStateManager.rotate(-45, 0, 1, 0);

		GlStateManager.translate((float) structureLength / -2f, (float) structureHeight / -2f, (float) structureWidth / -2f);

		GlStateManager.disableLighting();

		if (Minecraft.isAmbientOcclusionEnabled()) {
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		} else {
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		if (structureWidth % 2 == 1) {
			//GlStateManager.translate(-.5f, 0, 0);
		}
		int iterator = 0;

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		for (int h = 0; h < data.height; h++) {
			for (int l = 0; l < data.length; l++) {
				for (int w = 0; w < data.width; w++) {
					BlockPos pos = new BlockPos(l, h, w);
					if (!blockAccess.isAirBlock(pos)) {
						IBlockState blockState = blockAccess.getBlockState(pos);
						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();
						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						blockRender.renderBlock(blockState, pos, blockAccess, buffer);
						tessellator.draw();
					}
				}
			}
		}
		//			GL11.glTranslated(0, 0, -i);
		GlStateManager.popMatrix();

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
	}*/
}
