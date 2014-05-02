/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.circuits;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import forestry.api.circuits.ICircuitLayout;
import forestry.core.circuits.ItemSolderingIron.CircuitRecipe;
import forestry.core.circuits.ItemSolderingIron.SolderingInventory;
import forestry.core.config.Defaults;
import forestry.core.gadgets.TileForestry;
import forestry.core.gui.GuiForestry;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StringUtil;

public class GuiSolderingIron extends GuiForestry<TileForestry> {

	private final IInventory inventory;

	public GuiSolderingIron(InventoryPlayer inventoryplayer, SolderingInventory inventory) {
		super(Defaults.TEXTURE_PATH_GUI + "/solder.png", new ContainerSolderingIron(inventoryplayer, inventory), inventory);

		this.inventory = inventory;

		xSize = 176;
		ySize = 205;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		ICircuitLayout layout = ((ContainerSolderingIron) inventorySlots).getLayout();
		String title = layout.getName();
		fontRendererObj.drawString(title, guiLeft + 8 + getCenteredOffset(title, 138), guiTop + 16, fontColor.get("gui.screen"));

		for (int l = 2; l < inventory.getSizeInventory(); l++) {
			String description;
			CircuitRecipe recipe = ItemSolderingIron.SolderManager.getMatchingRecipe(layout, inventory.getStackInSlot(l));
			if (recipe == null)
				description = "(" + StringUtil.localize("gui.noeffect") + ")";
			else
				description = StringUtil.localize(recipe.circuit.getName()) + " (" + recipe.circuit.getLimit() + ")";

			int row = (l - 2) * 20;
			fontRendererObj.drawString(description, guiLeft + 32, guiTop + 36 + row, fontColor.get("gui.screen"));
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

		if (guibutton.id == 1)
			((ContainerSolderingIron) inventorySlots).regressSelection(0, Proxies.common.getRenderWorld());
		else if (guibutton.id == 2)
			((ContainerSolderingIron) inventorySlots).advanceSelection(0, Proxies.common.getRenderWorld());
	}

}
