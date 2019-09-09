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
package forestry.core.circuits;

import java.util.Locale;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.farming.FarmDirection;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.inventory.ItemInventorySolderingIron;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

public class GuiSolderingIron extends GuiForestry<ContainerSolderingIron> {
	private final ItemInventorySolderingIron itemInventory;

	public GuiSolderingIron(ContainerSolderingIron container, PlayerInventory inv, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/solder.png", container, inv, title);

		this.itemInventory = container.getItemInventory();
		this.xSize = 176;
		this.ySize = 205;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		ICircuitLayout layout = container.getLayout();
		String title = layout.getName();
		getFontRenderer().drawString(title, guiLeft + 8 + textLayout.getCenteredOffset(title, 138), guiTop + 16, ColourProperties.INSTANCE.get("gui.screen"));

		for (int i = 0; i < 4; i++) {
			String description;
			ItemStack tube = itemInventory.getStackInSlot(i + 2);
			CircuitRecipe recipe = SolderManager.getMatchingRecipe(layout, tube);
			if (recipe == null) {
				description = "(" + Translator.translateToLocal("for.gui.noeffect") + ")";
			} else {
				description = recipe.getCircuit().getLocalizedName();
			}

			int row = i * 20;
			getFontRenderer().drawString(description, guiLeft + 32, guiTop + 36 + row, ColourProperties.INSTANCE.get("gui.screen"));

			if (tube.isEmpty()) {
				ICircuitSocketType socketType = layout.getSocketType();
				if (CircuitSocketType.FARM.equals(socketType)) {
					FarmDirection farmDirection = FarmDirection.values()[i];
					String farmDirectionString = farmDirection.toString().toLowerCase(Locale.ENGLISH);
					String localizedDirection = Translator.translateToLocal("for.gui.solder." + farmDirectionString);
					getFontRenderer().drawString(localizedDirection, guiLeft + 17, guiTop + 36 + row, ColourProperties.INSTANCE.get("gui.screen"));
				}
			}
		}
	}

	@Override
	public void init() {
		super.init();

		buttons.add(new Button(guiLeft + 12, guiTop + 10, 12, 18, "<", b -> ContainerSolderingIron.regressSelection(0)));
		buttons.add(new Button(guiLeft + 130, guiTop + 10, 12, 18, ">", b -> ContainerSolderingIron.advanceSelection(0)));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger("soldering.iron");
	}
}
