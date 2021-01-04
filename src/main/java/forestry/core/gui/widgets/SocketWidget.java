/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.core.gui.widgets;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.core.tooltips.ToolTip;
import forestry.core.circuits.ISocketable;
import forestry.core.circuits.ISolderingIron;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.gui.IContainerSocketed;
import forestry.core.utils.ItemTooltipUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SocketWidget extends Widget {

    private final ISocketable tile;
    private final int slot;
    private final ToolTip toolTip = new ToolTip(250) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public void refresh() {
            toolTip.clear();
            ItemStack stack = tile.getSocket(slot);
            if (!stack.isEmpty()) {
                toolTip.addAll(ItemTooltipUtil.getInformation(stack));
                toolTip.add(new TranslationTextComponent("for.gui.socket.remove").mergeStyle(TextFormatting.ITALIC));
            } else {
                toolTip.add(new TranslationTextComponent("for.gui.emptysocket"));
            }
        }
    };

    public SocketWidget(WidgetManager manager, int xPos, int yPos, ISocketable tile, int slot) {
        super(manager, xPos, yPos);
        this.tile = tile;
        this.slot = slot;
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        ItemStack socketStack = tile.getSocket(slot);
        if (!socketStack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(socketStack, startX + xPos, startY + yPos);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        return toolTip;
    }

    @Override
    public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {

        ItemStack itemstack = manager.minecraft.player.inventory.getItemStack();
        if (itemstack.isEmpty()) {
            return;
        }

        Item held = itemstack.getItem();

        Container container = manager.gui.getContainer();
        if (!(container instanceof IContainerSocketed)) {
            return;
        }

        IContainerSocketed containerSocketed = (IContainerSocketed) container;

        // Insert chipsets
        if (held instanceof ItemCircuitBoard) {
            containerSocketed.handleChipsetClick(slot);
        } else if (held instanceof ISolderingIron) {
            containerSocketed.handleSolderingIronClick(slot);
        }
    }
}
