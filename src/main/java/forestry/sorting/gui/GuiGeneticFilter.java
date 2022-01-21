package forestry.sorting.gui;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import forestry.api.genetics.filter.IFilterLogic;
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
	private EditBox searchField;

	public GuiGeneticFilter(ContainerGeneticFilter container, Inventory inventory, Component title) {
		super(Constants.TEXTURE_PATH_GUI + "/filter.png", container, inventory, title);
		imageHeight = 222;
		imageWidth = 212;
		this.tile = container.getTile();

		for (int i = 0; i < 6; i++) {
			Direction facing = Direction.from3DDataValue(i);
			widgetManager.add(new RuleWidget(widgetManager, 8 + 36, 18 + i * 18, facing, this));
		}

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 2; k++) {
					widgetManager.add(new SpeciesWidget(widgetManager, 44 + 36 + j * 45 + k * 18, 18 + i * 18, Direction.from3DDataValue(i), j, k == 0, this));
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
			searchField.setEditable(true);
			searchField.setVisible(true);
		}
		selection.filterEntries(searchField != null ? searchField.getValue() : "");
		for (Slot slot : this.container.slots) {
			if (slot instanceof SlotGeneticFilter) {
				SlotGeneticFilter filter = (SlotGeneticFilter) slot;
				filter.setEnabled(false);
			}
		}
	}

	private void deselectFilter() {
		this.selection.setProvider(null);
		if (searchField != null) {
			searchField.setEditable(false);
			searchField.setVisible(false);
		}
		scrollBar.setVisible(false);
		for (Slot slot : this.container.slots) {
			if (slot instanceof SlotGeneticFilter) {
				SlotGeneticFilter filter = (SlotGeneticFilter) slot;
				filter.setEnabled(true);
			}
		}
	}

	@Override
	public void init() {
		super.init();

		String oldString = searchField != null ? searchField.getValue() : "";

		this.searchField = new EditBox(this.minecraft.font, this.leftPos + selection.getX() + 89 + 36, selection.getY() + this.topPos + 4, 80, this.minecraft.font.lineHeight, null);
		this.searchField.setMaxLength(50);
		this.searchField.setBordered(false);
		this.searchField.setTextColor(16777215);
		this.searchField.setValue(oldString);
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseY, int mouseX) {
		super.renderBg(transform, partialTicks, mouseY, mouseX);

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableLighting();
		if (searchField != null) {
			this.searchField.render(transform, mouseX, mouseY, partialTicks);    //TODO correct?
		}
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (searchField != null && this.searchField.keyPressed(key, scanCode, modifiers)) {
			scrollBar.setValue(0);
			selection.filterEntries(searchField.getValue());
			return true;
		} else {
			return super.keyPressed(key, scanCode, modifiers);
		}
	}

	@Nullable
	@Override
	protected Slot getSlotAtPosition(double mouseX, double mouseY) {
		Slot slot = super.getSlotAtPosition(mouseX, mouseY);
		if (slot instanceof SlotGeneticFilter && selection.getLogic() != null) {
			return null;
		}
		return slot;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
			return true;
		}

		if (searchField != null) {
			searchField.mouseClicked(mouseX, mouseY, mouseButton);
		}
		Widget widget = widgetManager.getAtPosition(mouseX - leftPos, mouseY - topPos);
		if (widget == null) {
			deselectFilter();
		}
		return true;
	}

	@Override
	protected void addLedgers() {
		addHintLedger("filter");
	}

	//TODO not sure about this
	//	public IFilterContainer getContainer() {
	//		return tile;
	//	}

	public IFilterLogic getLogic() {
		return tile.getLogic();
	}
}
