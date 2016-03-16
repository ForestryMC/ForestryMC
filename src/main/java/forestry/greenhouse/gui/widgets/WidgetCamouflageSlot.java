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

import forestry.api.core.EnumCamouflageType;
import forestry.api.core.ICamouflageHandler;
import forestry.api.greenhouse.GreenhouseManager;
import forestry.api.multiblock.IMultiblockController;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class WidgetCamouflageSlot extends Widget {
	
	private ICamouflageHandler camouflageHandler;
	private EnumCamouflageType type;

	public WidgetCamouflageSlot(WidgetManager manager, int xPos, int yPos, ICamouflageHandler camouflageHandler, EnumCamouflageType type) {
		super(manager, xPos, yPos);
		
		this.camouflageHandler = camouflageHandler;
		this.type = type;
	}

	@Override
	public void draw(int startX, int startY) {
		if (camouflageHandler != null && camouflageHandler.getCamouflageBlock(type) != null) {
			Proxies.render.bindTexture(TextureMap.locationBlocksTexture);
			RenderItem renderItem = Proxies.common.getClientInstance().getRenderItem();
			renderItem.renderItemIntoGUI(camouflageHandler.getCamouflageBlock(type), startX + xPos, startY + yPos);
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseClick(mouseX, mouseY, mouseButton);
		if(camouflageHandler == null){
			return;
		}
		if(GuiScreen.isShiftKeyDown()){
			camouflageHandler.setCamouflageBlock(type, null);
		}else{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			ItemStack stack = player.inventory.getItemStack();
			if(stack != null && Block.getBlockFromItem(stack.getItem()) != null){
				Block block = Block.getBlockFromItem(stack.getItem());
				
				if(!GreenhouseManager.greenhouseItemAccess.isOnCamouflageBlockBlackList(type, stack) && (type == EnumCamouflageType.DEFAULT && block.isOpaqueCube() && !block.hasTileEntity(block.getStateFromMeta(stack.getItemDamage())) && block.isNormalCube(player.worldObj, camouflageHandler.getCoordinates()) || type == EnumCamouflageType.GLASS && GreenhouseManager.greenhouseItemAccess.isGreenhouseGlass(stack))){
					camouflageHandler.setCamouflageBlock(type, stack);
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
		public void refresh() {
			toolTip.clear();
			String typeName = type.name().toLowerCase(Locale.ENGLISH);
			if(camouflageHandler instanceof IMultiblockController){
				toolTip.add(StatCollector.translateToLocal("for.gui.empty.slot.camouflage.multiblock." + typeName) + ": ");
			}else{
				toolTip.add(StatCollector.translateToLocal("for.gui.empty.slot.camouflage." + typeName) + ": ");
			}
			ItemStack camouflageBlock = camouflageHandler.getCamouflageBlock(type);
					
			if (camouflageHandler == null || camouflageBlock == null) {
				toolTip.add(EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal("for.gui.empty"));
			}else{
				toolTip.add(EnumChatFormatting.ITALIC.toString() + camouflageBlock.getTooltip(Proxies.common.getClientInstance().thePlayer, false));
			}
		}
	};

}
