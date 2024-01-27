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

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.tooltips.ToolTip;
import forestry.core.circuits.ISocketable;
import forestry.core.circuits.ISolderingIron;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gui.IContainerSocketed;
import forestry.core.utils.ItemTooltipUtil;

public class SocketWidget extends Widget {

	private final ISocketable tile;
	private final int slot;

	public SocketWidget(WidgetManager manager, int xPos, int yPos, ISocketable tile, int slot) {
		super(manager, xPos, yPos);
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public void draw(PoseStack transform, int startY, int startX) {
		ItemStack socketStack = tile.getSocket(slot);
		if (!socketStack.isEmpty()) {
			Minecraft.getInstance().getItemRenderer().renderGuiItem(socketStack, startX + xPos, startY + yPos);
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return toolTip;
	}

	private final ToolTip toolTip = new ToolTip(250) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public void refresh() {
			toolTip.clear();
			ItemStack stack = tile.getSocket(slot);
			if (!stack.isEmpty()) {
				toolTip.addAll(ItemTooltipUtil.getInformation(stack));
				toolTip.add(Component.translatable("for.gui.socket.remove").withStyle(ChatFormatting.ITALIC));
			} else {
				toolTip.add(Component.translatable("for.gui.emptysocket"));
			}
		}
	};

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		ItemStack itemstack = manager.minecraft.player.inventoryMenu.getCarried();

		if (itemstack.isEmpty()) {
			return;
		}

		Item held = itemstack.getItem();

		AbstractContainerMenu container = manager.gui.getMenu();
		if (!(container instanceof IContainerSocketed containerSocketed)) {
			return;
		}

		// Insert chipsets
		if (held instanceof ItemCircuitBoard) {
			containerSocketed.handleChipsetClick(slot);
		} else if (held instanceof ISolderingIron) {
			containerSocketed.handleSolderingIronClick(slot);
		}
	}
}
