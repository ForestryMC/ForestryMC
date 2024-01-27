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
import java.util.Optional;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;

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
import forestry.core.utils.ClientUtils;
import forestry.core.utils.Translator;

public class GuiSolderingIron extends GuiForestry<ContainerSolderingIron> {

	private final ItemInventorySolderingIron itemInventory;

	public GuiSolderingIron(ContainerSolderingIron container, Inventory inv, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/solder.png", container, inv, title);

		this.itemInventory = container.getItemInventory();
		this.imageWidth = 176;
		this.imageHeight = 205;
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		ICircuitLayout layout = container.getLayout();
		String title = layout.getName();
		getFontRenderer().draw(transform, title, leftPos + 8 + textLayout.getCenteredOffset(title, 138), topPos + 16, ColourProperties.INSTANCE.get("gui.screen"));

		for (int i = 0; i < 4; i++) {
			String description;
			ItemStack tube = itemInventory.getItem(i + 2);
			Optional<ISolderRecipe> recipe = ChipsetManager.solderManager.getMatchingRecipe(ClientUtils.getRecipeManager(), layout, tube);
			if (recipe.isEmpty()) {
				description = "(" + Translator.translateToLocal("for.gui.noeffect") + ")";
			} else {
				description = recipe.get().getCircuit().getDisplayName().getString();
			}

			int row = i * 20;
			getFontRenderer().draw(transform, description, leftPos + 32, topPos + 36 + row, ColourProperties.INSTANCE.get("gui.screen"));

			if (tube.isEmpty()) {
				ICircuitSocketType socketType = layout.getSocketType();
				if (CircuitSocketType.FARM.equals(socketType)) {
					FarmDirection farmDirection = FarmDirection.values()[i];
					String farmDirectionString = farmDirection.toString().toLowerCase(Locale.ENGLISH);
					String localizedDirection = Translator.translateToLocal("for.gui.solder." + farmDirectionString);
					getFontRenderer().draw(transform, localizedDirection, leftPos + 17, topPos + 36 + row, ColourProperties.INSTANCE.get("gui.screen"));
				}
			}
		}
	}

	@Override
	public void init() {
		super.init();

		addRenderableWidget(new Button(leftPos + 12, topPos + 10, 12, 18, Component.literal("<"), b -> ContainerSolderingIron.regressSelection(0)));
		addRenderableWidget(new Button(leftPos + 130, topPos + 10, 12, 18, Component.literal(">"), b -> ContainerSolderingIron.advanceSelection(0)));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger("soldering.iron");
	}
}
