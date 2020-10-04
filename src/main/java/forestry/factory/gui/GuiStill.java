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
package forestry.factory.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileStill;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiStill extends GuiForestryTitled<ContainerStill> {
    private final TileStill tile;

    public GuiStill(ContainerStill container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "still.png", container, inventory, title);
        this.tile = container.getTile();
        widgetManager.add(new TankWidget(this.widgetManager, 35, 15, 0));
        widgetManager.add(new TankWidget(this.widgetManager, 125, 15, 1));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        blit(transform, guiLeft + 81, guiTop + 57, 176, 60, 14, 14);

        if (tile.getWorkCounter() > 0) {
            int massRemaining = tile.getProgressScaled(16);
            blit(transform, guiLeft + 84, guiTop + 17 + massRemaining, 176, 74 + massRemaining, 4, 17 - massRemaining);
        }
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addHintLedger("still");
        addPowerLedger(tile.getEnergyManager());
    }
}
