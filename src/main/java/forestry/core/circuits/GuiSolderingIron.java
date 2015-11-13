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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import forestry.api.circuits.CircuitSocketType;
import forestry.api.circuits.ICircuitLayout;
import forestry.api.circuits.ICircuitSocketType;
import forestry.api.farming.FarmDirection;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestry;
import forestry.core.inventory.ItemInventorySolderingIron;
import forestry.core.utils.StringUtil;

public class GuiSolderingIron extends GuiForestry<ContainerSolderingIron, ItemInventorySolderingIron> {

	public GuiSolderingIron(EntityPlayer player, ItemInventorySolderingIron inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/solder.png", new ContainerSolderingIron(player, inventory), inventory);

		xSize = 176;
		ySize = 205;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		ICircuitLayout layout = ((ContainerSolderingIron) inventorySlots).getLayout();
		String title = layout.getName();
		fontRendererObj.drawString(title, guiLeft + 8 + textLayout.getCenteredOffset(title, 138), guiTop + 16, fontColor.get("gui.screen"));

		for (int i = 0; i < 4; i++) {
			String description;
			ItemStack tube = inventory.getStackInSlot(i + 2);
			CircuitRecipe recipe = SolderManager.getMatchingRecipe(layout, tube);
			if (recipe == null) {
				description = "(" + StringUtil.localize("gui.noeffect") + ")";
			} else {
				description = StringUtil.localize(recipe.getCircuit().getName()) + " (" + recipe.getCircuit().getLimit() + ")";
			}

			int row = i * 20;
			fontRendererObj.drawString(description, guiLeft + 32, guiTop + 36 + row, fontColor.get("gui.screen"));

			if (tube == null) {
				try {
					ICircuitSocketType socketType = layout.getSocketType();
					if (CircuitSocketType.FARM.equals(socketType)) {
						FarmDirection farmDirection = FarmDirection.values()[i];
						String farmDirectionString = farmDirection.toString().toLowerCase(Locale.ENGLISH);
						String localizedDirection = StringUtil.localize("gui.solder." + farmDirectionString);
						fontRendererObj.drawString(localizedDirection, guiLeft + 17, guiTop + 36 + row, fontColor.get("gui.screen"));
					}
				} catch (Throwable ignored) {
					// older circuit layouts do not have getSocketType()
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();

		buttonList.add(new GuiButton(1, guiLeft + 12, guiTop + 10, 12, 18, "<"));
		buttonList.add(new GuiButton(2, guiLeft + 130, guiTop + 10, 12, 18, ">"));

	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		super.actionPerformed(guibutton);

		if (guibutton.id == 1) {
			ContainerSolderingIron.regressSelection(0);
		} else if (guibutton.id == 2) {
			ContainerSolderingIron.advanceSelection(0);
		}
	}

}
