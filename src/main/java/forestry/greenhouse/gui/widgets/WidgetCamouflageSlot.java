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

import forestry.core.gui.tooltips.ToolTip;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.ICamouflagedBlock;
import forestry.core.tiles.ICamouflagedBlock.CamouflageType;
import forestry.greenhouse.PluginGreenhouse;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class WidgetCamouflageSlot extends Widget {
	
	private ICamouflagedBlock camouflagedBlock;

	public WidgetCamouflageSlot(WidgetManager manager, int xPos, int yPos, ICamouflagedBlock camouflagedBlock) {
		super(manager, xPos, yPos);
		
		this.camouflagedBlock = camouflagedBlock;
	}

	@Override
	public void draw(int startX, int startY) {
		if (camouflagedBlock == null || camouflagedBlock.getCamouflageBlock() != null) {
			Proxies.render.bindTexture(TextureMap.locationBlocksTexture);
			RenderItem renderItem = Proxies.common.getClientInstance().getRenderItem();
			renderItem.renderItemIntoGUI(camouflagedBlock.getCamouflageBlock(), startX + xPos, startY + yPos);
		}
	}
	
	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseClick(mouseX, mouseY, mouseButton);
		if(GuiScreen.isShiftKeyDown()){
			camouflagedBlock.setCamouflageBlock(null);
		}else{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			ItemStack heldStack = player.getHeldItem();
			if(heldStack != null && Block.getBlockFromItem(heldStack.getItem()) != null){
				Block block = Block.getBlockFromItem(heldStack.getItem());
				if(camouflagedBlock.getType() == CamouflageType.DEFAULT && !block.isOpaqueCube() && block.isNormalCube(player.worldObj, camouflagedBlock.getCoordinates()) || camouflagedBlock.getType() == CamouflageType.GLASS && block.isOpaqueCube() && PluginGreenhouse.isGreenhouseGlass(heldStack)){
					camouflagedBlock.setCamouflageBlock(heldStack);
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
			if (camouflagedBlock == null || camouflagedBlock.getCamouflageBlock() == null) {
				return;
			}
			toolTip.add(camouflagedBlock.getCamouflageBlock().getTooltip(Proxies.common.getClientInstance().thePlayer, false));
		}
	};

}
