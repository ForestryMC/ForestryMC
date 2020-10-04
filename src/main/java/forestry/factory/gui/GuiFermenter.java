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
import forestry.factory.tiles.TileFermenter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiFermenter extends GuiForestryTitled<ContainerFermenter> {
    private final TileFermenter tile;

    public GuiFermenter(ContainerFermenter container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "fermenter.png", container, inventory, title);
        this.tile = container.getTile();
        widgetManager.add(new TankWidget(this.widgetManager, 35, 19, 0));
        widgetManager.add(new TankWidget(this.widgetManager, 125, 19, 1));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        // Fuel remaining
        int fuelRemain = tile.getBurnTimeRemainingScaled(16);
        if (fuelRemain > 0) {
            blit(transform, guiLeft + 98, guiTop + 46 + 17 - fuelRemain, 176, 78 + 17 - fuelRemain, 4, fuelRemain);
        }

        // Raw bio mush remaining
        int bioRemain = tile.getFermentationProgressScaled(16);
        if (bioRemain > 0) {
            blit(transform, guiLeft + 74, guiTop + 32 + 17 - bioRemain, 176, 60 + 17 - bioRemain, 4, bioRemain);
        }
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addHintLedger("fermenter");
        addPowerLedger(tile.getEnergyManager());
    }
}
