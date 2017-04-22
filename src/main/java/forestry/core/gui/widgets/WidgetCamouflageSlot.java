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
package forestry.core.gui.widgets;

import forestry.api.core.CamouflageManager;
import forestry.api.core.ICamouflageHandler;
import forestry.api.core.ICamouflageItemHandler;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.utils.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WidgetCamouflageSlot extends Widget {
	private final ICamouflageHandler camouflageHandler;
	private final String type;

	public WidgetCamouflageSlot(WidgetManager manager, int xPos, int yPos, ICamouflageHandler camouflageHandler, String type) {
		super(manager, xPos, yPos);

		this.camouflageHandler = camouflageHandler;
		this.type = type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void draw(int startX, int startY) {
		ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock(type);
		if (!camouflageBlock.isEmpty()) {
			Minecraft minecraft = Minecraft.getMinecraft();
			TextureManager textureManager = minecraft.getTextureManager();
			textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderItem renderItem = minecraft.getRenderItem();
			renderItem.renderItemIntoGUI(camouflageBlock, startX + xPos, startY + yPos);
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseClick(mouseX, mouseY, mouseButton);
		if (GuiScreen.isShiftKeyDown()) {
			camouflageHandler.setCamouflageBlock(type, camouflageHandler.getDefaultCamouflageBlock(type), true);
		} else {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.inventory.getItemStack();
			if (!stack.isEmpty()) {
				if (!CamouflageManager.camouflageAccess.isItemBlackListed(type, stack)) {
					for (ICamouflageItemHandler handler : CamouflageManager.camouflageAccess.getCamouflageItemHandler(type)) {
						if (handler != null && handler.canHandle(stack)) {
							camouflageHandler.setCamouflageBlock(handler.getType(), stack.copy(), true);
						}
					}
				}
			}
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

	protected final ToolTip toolTip = new ToolTip(250) {
		@Override
		@SideOnly(Side.CLIENT)
		public void refresh() {
			toolTip.clear();
			if (camouflageHandler instanceof IMultiblockController) {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage.multiblock." + type) + ": ");
			} else {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage") + ": ");
			}
			ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock(type);

			if (camouflageBlock.isEmpty()) {
				toolTip.add(TextFormatting.ITALIC.toString() + Translator.translateToLocal("for.gui.empty"));
			} else {
				Minecraft minecraft = Minecraft.getMinecraft();
				toolTip.add(TextFormatting.ITALIC.toString() + camouflageBlock.getTooltip(minecraft.player, minecraft.gameSettings.advancedItemTooltips));
			}
		}
	};

}
