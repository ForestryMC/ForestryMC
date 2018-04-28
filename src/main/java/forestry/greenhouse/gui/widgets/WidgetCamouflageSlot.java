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
package forestry.greenhouse.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.ItemTooltipUtil;
import forestry.core.utils.Translator;

public class WidgetCamouflageSlot extends Widget {
	private final ICamouflageHandler camouflageHandler;
	protected final ToolTip toolTip = new ToolTip(250) {
		@Override
		@SideOnly(Side.CLIENT)
		public void refresh() {
			toolTip.clear();
			if (camouflageHandler instanceof IMultiblockController) {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage.multiblock") + ": ");
			} else {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage") + ": ");
			}
			ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock();

			if (camouflageBlock.isEmpty()) {
				toolTip.add(Translator.translateToLocal("for.gui.empty"), TextFormatting.ITALIC);
			} else {
				Minecraft minecraft = Minecraft.getMinecraft();
				toolTip.add(ItemTooltipUtil.getInformation(camouflageBlock));
			}
		}
	};

	public WidgetCamouflageSlot(WidgetManager manager, int xPos, int yPos, ICamouflageHandler camouflageHandler) {
		super(manager, xPos, yPos);

		this.camouflageHandler = camouflageHandler;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void draw(int startX, int startY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock();
		if (!camouflageBlock.isEmpty()) {
			Minecraft minecraft = Minecraft.getMinecraft();
			TextureManager textureManager = minecraft.getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderHelper.enableGUIStandardItemLighting();
			GuiUtil.drawItemStack(manager.gui, camouflageBlock, startX + xPos, startY + yPos);
			RenderHelper.disableStandardItemLighting();
		}
	}

	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		if (isMouseOver(mouseX, mouseY)) {
			return toolTip;
		} else {
			return null;
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseClick(mouseX, mouseY, mouseButton);
		if (GuiScreen.isShiftKeyDown()) {
			camouflageHandler.setCamouflageBlock(camouflageHandler.getDefaultCamouflageBlock(), true);
		} else {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.inventory.getItemStack();
			if (!stack.isEmpty()) {
				if (!CamouflageManager.camouflageAccess.isItemBlackListed(stack)) {
					for (ICamouflageItemHandler handler : CamouflageManager.camouflageAccess.getItemHandlers()) {
						if (handler != null && handler.canHandle(stack)) {
							camouflageHandler.setCamouflageBlock(stack.copy(), true);
						}
					}
				}
			}
		}
	}

}
