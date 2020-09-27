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
import forestry.apiculture.multiblock.IAlvearyControllerInternal;
import forestry.apiculture.multiblock.TileAlveary;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.render.EnumTankLevel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiAlveary extends GuiForestryTitled<ContainerAlveary> {
    private final TileAlveary tile;

    public GuiAlveary(ContainerAlveary container, PlayerInventory inventory, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "/alveary.png", container, inventory, title);
        this.tile = container.getTile();
        this.ySize = 190;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        IAlvearyControllerInternal alvearyController = tile.getMultiblockLogic().getController();
        drawHealthMeter(
                transform,
                guiLeft + 20,
                guiTop + 37,
                alvearyController.getHealthScaled(46),
                EnumTankLevel.rateTankLevel(alvearyController.getHealthScaled(100))
        );
    }

    private void drawHealthMeter(MatrixStack transform, int x, int y, int height, EnumTankLevel rated) {
        int i = 176 + rated.getLevelScaled(16);
        int k = 0;

        this.blit(transform, x, y + 46 - height, i, k + 46 - height, 4, height);
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addClimateLedger(tile);
        addHintLedger("apiary");
        addOwnerLedger(tile);
    }
}
