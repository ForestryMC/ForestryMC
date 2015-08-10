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

import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

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

	private final ISocketable tile;
	private final int slot;

	public SocketWidget(WidgetManager manager, int xPos, int yPos, ISocketable tile, int slot) {
		super(manager, xPos, yPos);
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public void draw(int startX, int startY) {
		ItemStack socketStack = tile.getSocket(slot);
		if (socketStack != null) {
			GuiForestry.getItemRenderer().renderItemIntoGUI(manager.minecraft.fontRenderer, manager.minecraft.renderEngine, socketStack, startX + xPos, startY + yPos);
		}
	}

	@Override
	public ToolTip getToolTip() {
		return toolTip;
	}

	private final ToolTip toolTip = new ToolTip(250) {
		@SuppressWarnings("unchecked")
		@Override
		public void refresh() {
			toolTip.clear();
			ItemStack stack = tile.getSocket(slot);
			if (stack != null) {
				for (String line : (List<String>) stack.getTooltip(Proxies.common.getClientInstance().thePlayer, false)) {
					toolTip.add(line);
				}
				toolTip.add(EnumChatFormatting.ITALIC + StringUtil.localize("gui.socket.remove"));
			} else {
				toolTip.add(StringUtil.localize("gui.emptysocket"));
			}
		}
	};

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {

		ItemStack itemstack = manager.minecraft.thePlayer.inventory.getItemStack();
		if (itemstack == null) {
			return;
		}

		Item held = itemstack.getItem();

		Container container = manager.gui.inventorySlots;
		if (!(container instanceof ContainerSocketed)) {
			return;
		}

		ContainerSocketed containerSocketed = (ContainerSocketed) container;

		// Insert chipsets
		if (held instanceof ItemCircuitBoard) {
			containerSocketed.handleChipsetClick(slot);
		} else if (held instanceof ISolderingIron) {
			containerSocketed.handleSolderingIronClick(slot);
		}
	}
}
