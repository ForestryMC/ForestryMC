/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.gui.widgets;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.core.circuits.ISolderingIron;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gui.ContainerSocketed;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.WidgetManager;
import forestry.core.gui.tooltips.ToolTip;
import forestry.core.interfaces.ISocketable;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class SocketWidget extends Widget {

	ISocketable tile;
	int slot = 0;

	public SocketWidget(WidgetManager manager, int xPos, int yPos, ISocketable tile, int slot) {
		super(manager, xPos, yPos);
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public void draw(int startX, int startY) {
		ItemStack socketStack = tile.getSocket(slot);
		if (socketStack != null)
			GuiForestry.getItemRenderer().renderItemIntoGUI(manager.minecraft.fontRenderer, manager.minecraft.renderEngine, socketStack, startX + xPos, startY
					+ yPos);
	}

	@Override
	public ToolTip getToolTip() {
		return toolTip;
	}

	protected final ToolTip toolTip = new ToolTip(500) {
		@SuppressWarnings("unchecked")
		@Override
		public void refresh() {
			toolTip.clear();
			ItemStack stack = tile.getSocket(slot);
			if (stack != null) {
				for (String line : (List<String>) stack.getTooltip(Proxies.common.getClientInstance().thePlayer, false)) {
					toolTip.add(line);
				}
			} else {
				toolTip.add(StringUtil.localize("gui.emptysocket"));
			}
		}
	};

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {

		ItemStack itemstack = manager.minecraft.thePlayer.inventory.getItemStack();
		if (itemstack == null)
			return;

		Item held = itemstack.getItem();

		// Insert chipsets
		if (held instanceof ItemCircuitBoard)
			((ContainerSocketed) manager.gui.inventorySlots).handleChipsetClick(slot, manager.minecraft.thePlayer, itemstack);
		else if (held instanceof ISolderingIron)
			((ContainerSocketed) manager.gui.inventorySlots).handleSolderingIronClick(slot, manager.minecraft.thePlayer, itemstack);
	}
}
