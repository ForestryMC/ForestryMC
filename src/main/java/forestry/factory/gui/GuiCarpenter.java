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
package forestry.factory.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.factory.tiles.TileCarpenter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiCarpenter extends GuiForestryTitled<ContainerCarpenter> {
    private final TileCarpenter tile;

    public GuiCarpenter(ContainerCarpenter container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "carpenter.png", container, inventory, title);

        this.tile = container.getTile();
        this.ySize = 218;
        this.widgetManager.add(new TankWidget(this.widgetManager, 150, 17, 0));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);
        int progressScaled = tile.getProgressScaled(16);
        blit(
                transform,
                guiLeft + 98,
                guiTop + 51 + 16 - progressScaled,
                176,
                60 + 16 - progressScaled,
                4,
                progressScaled
        );
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addPowerLedger(tile.getEnergyManager());
        addHintLedger("carpenter");
    }
}
