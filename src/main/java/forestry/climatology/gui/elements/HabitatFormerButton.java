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
package forestry.climatology.gui.elements;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.climatology.gui.GuiHabitatFormer;
import forestry.core.features.CoreItems;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiUtil;
import forestry.core.gui.elements.ButtonElement;
import forestry.core.items.EnumElectronTube;

public class HabitatFormerButton extends ButtonElement {
    private static final Drawable ENABLED_BUTTON = new Drawable(GuiHabitatFormer.TEXTURE, 234, 0, 22, 22);
    private static final Drawable DISABLED_BUTTON = new Drawable(GuiHabitatFormer.TEXTURE, 212, 0, 22, 22);

    private final ItemStack iconStack;

    public HabitatFormerButton(int xPos, int yPos, boolean selectionButton, Consumer<Boolean> onClicked) {
        super(xPos, yPos, 22, 22, DISABLED_BUTTON, ENABLED_BUTTON, button -> onClicked.accept(selectionButton));
        this.iconStack = selectionButton ? CoreItems.ELECTRON_TUBES.stack(EnumElectronTube.GOLD, 1) : CoreItems.GEAR_BRONZE.stack();
    }

    @Override
    public void drawElement(MatrixStack transform, int mouseY, int mouseX) {
        super.drawElement(transform, mouseY, mouseX);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        GuiUtil.drawItemStack(fontRenderer, iconStack, 3, 3);
    }
}
