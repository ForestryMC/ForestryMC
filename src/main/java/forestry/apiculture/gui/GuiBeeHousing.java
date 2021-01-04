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
package forestry.apiculture.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.GuiAnalyzerProvider;
import forestry.core.gui.slots.SlotWatched;
import forestry.core.render.EnumTankLevel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class GuiBeeHousing<C extends ContainerForestry & IContainerBeeHousing> extends GuiAnalyzerProvider<C> {
    private final IGuiBeeHousingDelegate delegate;

    public enum Icon {
        APIARY("apiary.png"),
        BEE_HOUSE("alveary.png");

        private final String path;

        Icon(String path) {
            this.path = path;
        }
    }

    //TODO be hacky and use title to get the icon?
    public GuiBeeHousing(C container, PlayerInventory inv, ITextComponent title) {
        super(
                Constants.TEXTURE_PATH_GUI + container.getIcon().path,
                container,
                inv,
                container.getDelegate(),
                25,
                7,
                2,
                0
        );
        this.delegate = container.getDelegate();
        this.ySize = 190;

        for (int i = 0; i < 2; i++) {
            Slot queenSlot = container.getForestrySlot(1 + i);
            if (queenSlot instanceof SlotWatched) {
                SlotWatched watched = (SlotWatched) queenSlot;
                watched.setChangeWatcher(this);
            }
        }
        analyzer.init();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseX, mouseY);

        bindTexture(textureFile);
        drawHealthMeter(
                transform,
                guiLeft + 20,
                guiTop + 37,
                delegate.getHealthScaled(46),
                EnumTankLevel.rateTankLevel(delegate.getHealthScaled(100))
        );
    }

    @Override
    protected void drawSelectedSlot(MatrixStack transform, int selectedSlot) {
        Slot slot = container.getForestrySlot(1 + selectedSlot);
        SELECTED_COMB_SLOT.draw(transform, guiTop + slot.yPos - 3, guiLeft + slot.xPos - 3);
    }

    private void drawHealthMeter(MatrixStack transform, int x, int y, int height, EnumTankLevel rated) {
        int i = 176 + rated.getLevelScaled(16);
        int k = 0;

        this.blit(transform, x, y + 46 - height, i, k + 46 - height, 4, height);
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(delegate);
        addClimateLedger(delegate);
        addHintLedger(delegate.getHintKey());
        addOwnerLedger(delegate);
    }

    @Override
    public ItemStack getSpecimen(int index) {
        Slot slot = container.getForestrySlot(getSelectedSlot(index));
        return slot.getStack();
    }

    @Override
    protected boolean hasErrors() {
        return delegate.getErrorLogic().hasErrors();
    }
}
