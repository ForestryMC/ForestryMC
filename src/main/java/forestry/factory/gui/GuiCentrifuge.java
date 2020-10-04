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
import forestry.core.gui.widgets.SocketWidget;
import forestry.factory.tiles.TileCentrifuge;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiCentrifuge extends GuiForestryTitled<ContainerCentrifuge> {
    private final TileCentrifuge tile;

    public GuiCentrifuge(ContainerCentrifuge container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "centrifugesocket2.png", container, inventory, title);
        this.tile = container.getTile();
        widgetManager.add(new SocketWidget(this.widgetManager, 79, 37, tile, 0));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        int progress = tile.getProgressScaled(16);
        blit(transform, guiLeft + 43, guiTop + 36 + 17 - progress, 176, 17 - progress, 4, progress);
        blit(transform, guiLeft + 67, guiTop + 36 + 17 - progress, 176, 17 - progress, 4, progress);
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addHintLedger("centrifuge");
        addPowerLedger(tile.getEnergyManager());
    }
}
