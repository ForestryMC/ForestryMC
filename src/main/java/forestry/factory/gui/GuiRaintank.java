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
import forestry.factory.tiles.TileRaintank;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiRaintank extends GuiForestryTitled<ContainerRaintank> {
    private final TileRaintank tile;

    //TODO these all store a tile. Make a superclass to automatically do it.
    public GuiRaintank(ContainerRaintank container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "/raintank.png", container, inventory, title);
        this.tile = container.getTile();
        widgetManager.add(new TankWidget(this.widgetManager, 53, 17, 0));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        if (tile.isFilling()) {
            int progress = tile.getFillProgressScaled(24);
            blit(transform, guiLeft + 80, guiTop + 39, 176, 74, progress, 16);
        }
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addHintLedger("raintank");
    }
}
