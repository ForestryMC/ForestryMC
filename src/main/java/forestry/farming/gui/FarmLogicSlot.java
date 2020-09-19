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
package forestry.farming.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.IFarmLogic;
import forestry.api.farming.IFarmProperties;
import forestry.core.config.Config;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.farming.multiblock.IFarmControllerInternal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class FarmLogicSlot extends Widget {

    private final IFarmControllerInternal farmController;
    private final FarmDirection farmDirection;

    public FarmLogicSlot(
            IFarmControllerInternal farmController,
            WidgetManager manager,
            int xPos,
            int yPos,
            FarmDirection farmDirection
    ) {
        super(manager, xPos, yPos);
        this.farmController = farmController;
        this.farmDirection = farmDirection;
    }

    private IFarmLogic getLogic() {
        return farmController.getFarmLogic(farmDirection);
    }

    private IFarmProperties getProperties() {
        return getLogic().getProperties();
    }

    private ItemStack getStackIndex() {
        return getProperties().getIcon();
    }

    @Override
    public void draw(MatrixStack transform, int startY, int startX) {
        if (!getStackIndex().isEmpty()) {
            Minecraft minecraft = Minecraft.getInstance();
            TextureManager textureManager = minecraft.getTextureManager();
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            ItemRenderer renderItem = minecraft.getItemRenderer();
            renderItem.renderItemIntoGUI(getStackIndex(), startX + xPos, startY + yPos);
        }
    }

    @Override
    public ToolTip getToolTip(int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            return toolTip;
        } else {
            return null;
        }
    }

    protected final ToolTip toolTip = new ToolTip(250) {
        @Override
        public void refresh() {
            toolTip.clear();
            toolTip.add(getProperties().getDisplayName(getLogic().isManual()));
            toolTip.add(
                    new TranslationTextComponent("for.gui.fertilizer")
                            .appendString(": ")
                            .appendString(String.valueOf(Math.round(getProperties().getFertilizerConsumption(
                                    farmController) * Config.fertilizerModifier
                            )))
            );
            toolTip.add(
                    new TranslationTextComponent("for.gui.water")
                            .appendString(": ")
                            .appendString(String.valueOf(getProperties().getWaterConsumption(
                                    farmController,
                                    farmController.getFarmLedgerDelegate().getHydrationModifier()
                            )))
            );
        }
    };
}
