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
package forestry.core.circuits;

import com.mojang.blaze3d.matrix.MatrixStack;
import forestry.api.circuits.ChipsetManager;
import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.farming.FarmDirection;
import forestry.api.recipes.ISolderRecipe;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.inventory.ItemInventorySolderingIron;
import forestry.core.render.ColourProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

public class GuiSolderingIron extends GuiForestry<ContainerSolderingIron> {
    private final ItemInventorySolderingIron itemInventory;

    public GuiSolderingIron(ContainerSolderingIron container, PlayerInventory inv, ITextComponent title) {
        super(Constants.TEXTURE_PATH_GUI + "solder.png", container, inv, title);

        this.itemInventory = container.getItemInventory();
        this.xSize = 176;
        this.ySize = 205;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack transform, float partialTicks, int mouseY, int mouseX) {
        super.drawGuiContainerBackgroundLayer(transform, partialTicks, mouseY, mouseX);

        ICircuitLayout layout = container.getLayout();
        getFontRenderer().func_243248_b(
                transform,
                layout.getName(),
                guiLeft + 8 + textLayout.getCenteredOffset(layout.getName(), 138),
                guiTop + 16,
                ColourProperties.INSTANCE.get("gui.screen")
        );

        for (int i = 0; i < 4; i++) {
            ITextComponent description;
            ItemStack tube = itemInventory.getStackInSlot(i + 2);
            ISolderRecipe recipe = ChipsetManager.solderManager.getMatchingRecipe(
                    Minecraft.getInstance().world.getRecipeManager(),
                    layout,
                    tube
            );
            if (recipe == null) {
                description = new StringTextComponent(">")
                        .append(new TranslationTextComponent("for.gui.noeffect"))
                        .appendString(")");
            } else {
                description = recipe.getCircuit().getDisplayName();
            }

            int row = i * 20;
            getFontRenderer().func_243248_b(
                    transform,
                    description,
                    guiLeft + 32,
                    guiTop + 36 + row,
                    ColourProperties.INSTANCE.get("gui.screen")
            );

            if (tube.isEmpty()) {
                ICircuitSocketType socketType = layout.getSocketType();
                if (CircuitSocketType.FARM.equals(socketType)) {
                    FarmDirection farmDirection = FarmDirection.values()[i];
                    String farmDirectionString = farmDirection.toString().toLowerCase(Locale.ENGLISH);
                    getFontRenderer().func_243248_b(
                            transform,
                            new TranslationTextComponent("for.gui.solder." + farmDirectionString),
                            guiLeft + 17,
                            guiTop + 36 + row,
                            ColourProperties.INSTANCE.get("gui.screen")
                    );
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();

        addButton(new Button(
                guiLeft + 12,
                guiTop + 10,
                12,
                18,
                new StringTextComponent("<"),
                b -> ContainerSolderingIron.regressSelection(0)
        ));
        addButton(new Button(
                guiLeft + 130,
                guiTop + 10,
                12,
                18,
                new StringTextComponent(">"),
                b -> ContainerSolderingIron.advanceSelection(0)
        ));
    }

    @Override
    protected void addLedgers() {
        addErrorLedger(itemInventory);
        addHintLedger("soldering.iron");
    }
}
