/*
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 */
package forestry.energy.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Constants;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.render.EnumTankLevel;
import forestry.energy.tiles.TileEngineElectric;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiEngineElectric extends GuiEngine<ContainerEngineElectric, TileEngineElectric> {

    public GuiEngineElectric(ContainerEngineElectric container, PlayerInventory inventory, ITextComponent name) {
        super(Constants.TEXTURE_PATH_GUI + "electricalengine.png", container, inventory, container.getTile(), name);
        widgetManager.add(new SocketWidget(this.widgetManager, 30, 40, tile, 0));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        TileEngineElectric engine = tile;
        int storageHeight = engine.getStorageScaled(46);
        int storageMaxHeight = engine.getStorageScaled(100);
        EnumTankLevel rated = EnumTankLevel.rateTankLevel(storageMaxHeight);

        drawHealthMeter(transform, guiLeft + 74, guiTop + 25, storageHeight, rated);
    }

    private void drawHealthMeter(MatrixStack transform, int x, int y, int height, EnumTankLevel rated) {
        int i = 176 + rated.getLevelScaled(16);
        int k = 0;

        this.blit(transform, x, y + 46 - height, i, k + 46 - height, 4, height);
    }

}
