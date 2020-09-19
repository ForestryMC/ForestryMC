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
package forestry.energy.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.core.config.Config;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.ledgers.Ledger;
import forestry.core.render.TextureManagerForestry;
import forestry.energy.tiles.TileEngine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class GuiEngine<C extends Container, I extends TileEngine> extends GuiForestryTitled<C> {
    protected final I tile;

    protected GuiEngine(String texture, C container, PlayerInventory inv, I tile, ITextComponent title) {
        super(texture, container, inv, title);
        this.tile = tile;
    }

    protected class EngineLedger extends Ledger {

        public EngineLedger() {
            super(ledgerManager, "power");
            maxHeight = 94;
        }

        @Override
        public void draw(MatrixStack transform, int y, int x) {

            // Draw background
            drawBackground(transform, y, x);

            // Draw icon
            drawSprite(transform, TextureManagerForestry.getInstance().getDefault("misc/energy"), x + 3, y + 4);

            if (!isFullyOpened()) {
                return;
            }

            drawHeader(transform, new TranslationTextComponent("for.gui.energy"), x + 22, y + 8);

            drawSubheader(transform, new TranslationTextComponent("for.gui.currentOutput").appendString(":"), x + 22, y + 20);
            drawText(transform, Config.energyDisplayMode.formatRate(tile.getCurrentOutput()), x + 22, y + 32);

            drawSubheader(transform, new TranslationTextComponent("for.gui.stored").appendString(":"), x + 22, y + 44);
            drawText(
                    transform,
                    Config.energyDisplayMode.formatEnergyValue(tile.getEnergyManager().getEnergyStored()),
                    x + 22,
                    y + 56
            );

            drawSubheader(transform, new TranslationTextComponent("for.gui.heat").appendString(":"), x + 22, y + 68);
            drawText(transform, (double) tile.getHeat() / (double) 10 + 20.0 + " C", x + 22, y + 80);
        }

        @Override
        public ITextComponent getTooltip() {
            return new StringTextComponent(Config.energyDisplayMode.formatRate(tile.getCurrentOutput()));
        }
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(tile);
        addHintLedger(tile.getHintKey());
        ledgerManager.add(new EngineLedger());
    }
}
