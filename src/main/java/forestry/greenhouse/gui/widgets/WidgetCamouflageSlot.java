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

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.utils.Translator;

public class WidgetCamouflageSlot extends Widget {
	
	private final ICamouflageHandler camouflageHandler;
	private final EnumCamouflageType type;

	public WidgetCamouflageSlot(WidgetManager manager, int xPos, int yPos, ICamouflageHandler camouflageHandler, EnumCamouflageType type) {
		super(manager, xPos, yPos);
		
		this.camouflageHandler = camouflageHandler;
		this.type = type;
	}

	@Override
	public void draw(int startX, int startY) {
		if (camouflageHandler != null && camouflageHandler.getCamouflageBlock(type) != null) {
			Proxies.render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderItem renderItem = Proxies.common.getClientInstance().getRenderItem();
			renderItem.renderItemIntoGUI(camouflageHandler.getCamouflageBlock(type), startX + xPos, startY + yPos);
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseClick(mouseX, mouseY, mouseButton);
		if (camouflageHandler == null) {
			return;
		}
		if (GuiScreen.isShiftKeyDown()) {
			camouflageHandler.setCamouflageBlock(type, camouflageHandler.getDefaultCamouflageBlock(type));
		} else {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.inventory.getItemStack();
			if (stack != null && Block.getBlockFromItem(stack.getItem()) != null) {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (!GreenhouseManager.greenhouseAccess.isOnCamouflageBlockBlackList(type, stack)) {
					IBlockState stateFromMeta = block.getStateFromMeta(stack.getItemDamage());
					if (type == EnumCamouflageType.DEFAULT && block.isOpaqueCube(stateFromMeta) && !block.hasTileEntity(stateFromMeta) && block.isNormalCube(stateFromMeta, player.worldObj, camouflageHandler.getCoordinates())) {
						camouflageHandler.setCamouflageBlock(type, stack);
					} else if (type == EnumCamouflageType.GLASS && GreenhouseManager.greenhouseAccess.isGreenhouseGlass(stack)) {
						camouflageHandler.setCamouflageBlock(type, stack);
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
			String typeName = type.name().toLowerCase(Locale.ENGLISH);
			if (camouflageHandler instanceof IMultiblockController) {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage.multiblock." + typeName) + ": ");
			} else {
				toolTip.add(Translator.translateToLocal("for.gui.empty.slot.camouflage") + ": ");
			}
			ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock(type);

			if (camouflageHandler == null || camouflageBlock == null) {
				toolTip.add(TextFormatting.ITALIC.toString() + Translator.translateToLocal("for.gui.empty"));
			} else {
				Minecraft minecraft = Proxies.common.getClientInstance();
				toolTip.add(TextFormatting.ITALIC.toString() + camouflageBlock.getTooltip(minecraft.thePlayer, minecraft.gameSettings.advancedItemTooltips));
			}
		}
	};

}
