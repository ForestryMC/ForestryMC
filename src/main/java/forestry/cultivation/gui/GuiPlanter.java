package forestry.cultivation.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.TankWidget;
import forestry.cultivation.gui.widgets.GhostItemStackWidget;
import forestry.cultivation.inventory.InventoryPlanter;
import forestry.cultivation.tiles.TilePlanter;
import forestry.farming.gui.FarmLedger;

public class GuiPlanter extends GuiForestryTitled<ContainerPlanter> {
	private final TilePlanter tile;

	public GuiPlanter(ContainerPlanter container, PlayerInventory playerInventory, ITextComponent title) {
		super(Constants.TEXTURE_PATH_GUI + "/planter.png", container, playerInventory, container.getTile());
		this.tile = container.getTile();
		this.xSize = 202;
		this.ySize = 192;

		NonNullList<ItemStack> resourceStacks = tile.createResourceStacks();
		NonNullList<ItemStack> germlingStacks = tile.createGermlingStacks();
		NonNullList<ItemStack> productionStacks = tile.createProductionStacks();

		widgetManager.add(new TankWidget(widgetManager, 178, 44, 0).setOverlayOrigin(xSize, 18));

		// Resources
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (resourceStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 11 + j * 18, 65 + i * 18, resourceStacks.get(index), this.getContainer().getSlot(36 + InventoryPlanter.SLOT_RESOURCES_1 + index)));
				}
			}
		}

		// Germlings
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (germlingStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 71 + j * 18, 65 + i * 18, germlingStacks.get(index), this.getContainer().getSlot(36 + InventoryPlanter.SLOT_GERMLINGS_1 + index)));
				}
			}
		}

		// Production
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				int index = j + i * 2;
				if (productionStacks.size() == 4) {
					widgetManager.add(new GhostItemStackWidget(widgetManager, 131 + j * 18, 65 + i * 18, productionStacks.get(index), getContainer().getSlot(36 + InventoryPlanter.SLOT_PRODUCTION_1 + j + i * 2)));
				}
			}
		}

		widgetManager.add(new GhostItemStackWidget(widgetManager, 83, 22, new ItemStack(ModuleCore.getItems().fertilizerCompound), getContainer().getSlot(36 + InventoryPlanter.SLOT_FERTILIZER)));
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(tile);
		addClimateLedger(tile);
		ledgerManager.add(new FarmLedger(ledgerManager, tile.getFarmLedgerDelegate()));
		addOwnerLedger(tile);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);

		// Fuel remaining
		int fertilizerRemain = tile.getStoredFertilizerScaled(16);
		if (fertilizerRemain > 0) {
			blit(guiLeft + 101, guiTop + 21 + 17 - fertilizerRemain, xSize, 17 - fertilizerRemain, 4, fertilizerRemain);
		}
	}
}
