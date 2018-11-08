package forestry.sorting.gui;

import javax.annotation.Nullable;
import java.io.IOException;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import forestry.api.genetics.IFilterLogic;
import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestryTitled;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetScrollBar;
import forestry.sorting.gui.widgets.RuleWidget;
import forestry.sorting.gui.widgets.SelectionWidget;
import forestry.sorting.gui.widgets.SpeciesWidget;
import forestry.sorting.tiles.IFilterContainer;

public class GuiGeneticFilter extends GuiForestryTitled<ContainerGeneticFilter> {

	private final IFilterContainer tile;
	private final WidgetScrollBar scrollBar;
	public final SelectionWidget selection;
	@Nullable
	private GuiTextField searchField;

	public GuiGeneticFilter(IFilterContainer tile, InventoryPlayer inventory) {
		super(Constants.TEXTURE_PATH_GUI + "/filter.png", new ContainerGeneticFilter(tile, inventory), tile);
		ySize = 222;
		xSize = 212;
		this.tile = tile;

		for (int i = 0; i < 6; i++) {
			EnumFacing facing = EnumFacing.byIndex(i);
			widgetManager.add(new RuleWidget(widgetManager, 8 + 36, 18 + i * 18, facing, this));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 2; k++) {
					widgetManager.add(new SpeciesWidget(widgetManager, 44 + 36 + j * 45 + k * 18, 18 + i * 18, EnumFacing.byIndex(i), j, k == 0, this));
				}
			}
		}

		this.scrollBar = new WidgetScrollBar(widgetManager, 157 + 36, 150, 12, 64, new Drawable(new ResourceLocation(Constants.TEXTURE_PATH_GUI + "/container/creative_inventory/tabs.png"), 232, 0, 12, 15));
		widgetManager.add(this.selection = new SelectionWidget(widgetManager, 0, 134, scrollBar, this));
		widgetManager.add(scrollBar);
		scrollBar.setVisible(false);
	}

	public <S> void onModuleClick(ISelectableProvider<S> provider) {
		if (selection.isSame(provider)) {
			deselectFilter();
		} else {
			selectFilter(provider);
		}
	}

	private <S> void selectFilter(ISelectableProvider<S> provider) {
		selection.setProvider(provider);
		if (searchField != null) {
			searchField.setEnabled(true);
			searchField.setVisible(true);
		}
		selection.filterEntries(searchField != null ? searchField.getText() : "");
		for (Slot slot : inventorySlots.inventorySlots) {
			if (slot instanceof SlotGeneticFilter) {
				SlotGeneticFilter filter = (SlotGeneticFilter) slot;
				filter.setEnabled(false);
			}
		}
	}

	private void deselectFilter() {
		this.selection.setProvider(null);
		if (searchField != null) {
			searchField.setEnabled(false);
			searchField.setVisible(false);
		}
		scrollBar.setVisible(false);
		for (Slot slot : inventorySlots.inventorySlots) {
			if (slot instanceof SlotGeneticFilter) {
				SlotGeneticFilter filter = (SlotGeneticFilter) slot;
				filter.setEnabled(true);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		String oldString = searchField != null ? searchField.getText() : "";

		this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + selection.getX() + 89 + 36, selection.getY() + this.guiTop + 4, 80, this.fontRenderer.FONT_HEIGHT);
		this.searchField.setMaxStringLength(50);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setTextColor(16777215);
		this.searchField.setText(oldString);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		if (searchField != null) {
			this.searchField.drawTextBox();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (searchField != null && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
			scrollBar.setValue(0);
			selection.filterEntries(searchField.getText());
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Nullable
	@Override
	protected Slot getSlotAtPosition(int mouseX, int mouseY) {
		Slot slot = super.getSlotAtPosition(mouseX, mouseY);
		if (slot instanceof SlotGeneticFilter && selection.getLogic() != null) {
			return null;
		}
		return slot;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (searchField != null) {
			searchField.mouseClicked(mouseX, mouseY, mouseButton);
		}
		Widget widget = widgetManager.getAtPosition(mouseX - guiLeft, mouseY - guiTop);
		if (widget == null) {
			deselectFilter();
		}
	}

	@Override
	protected void addLedgers() {
		addHintLedger("filter");
	}

	public IFilterContainer getContainer() {
		return tile;
	}

	public IFilterLogic getLogic() {
		return tile.getLogic();
	}
}
