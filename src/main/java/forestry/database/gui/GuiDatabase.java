package forestry.database.gui;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiAnalyzerProvider;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.gui.widgets.WidgetScrollBar;
import forestry.database.DatabaseHelper;
import forestry.database.DatabaseItem;
import forestry.database.gui.buttons.DatabaseButton;
import forestry.database.gui.buttons.GuiDatabaseButton;
import forestry.database.gui.widgets.WidgetDatabaseSlot;
import forestry.database.tiles.TileDatabase;

public class GuiDatabase extends GuiAnalyzerProvider<ContainerDatabase> implements IScrollable {
	/* Attributes - Constants */
	private static final ResourceLocation CREATIVE_TABS = new ResourceLocation(Constants.TEXTURE_PATH_GUI + "/container/creative_inventory/tabs.png");
	private static final Drawable SCROLLBAR_SLIDER = new Drawable(CREATIVE_TABS, 232, 0, 12, 15);
	/*  Attributes - Final */
	public final TileDatabase tile;
	private final ArrayList<WidgetDatabaseSlot> slots;
	private final ArrayList<DatabaseItem> sorted = new ArrayList<>();
	/* Attributes - Gui Elements */
	@Nullable
	private GuiTextField searchField;
	private WidgetScrollBar scrollBar;
	/* Attributes - State */
	private boolean markedForSorting;
	@Nullable
	private DatabaseItem selectedItem;

	/* Constructors */
	public GuiDatabase(TileDatabase tile, EntityPlayer player) {
		super(Constants.TEXTURE_PATH_GUI + "/database_inventory.png", new ContainerDatabase(tile, player.inventory), tile, 7, 140, 20, true, tile.getInternalInventory().getSizeInventory(), 0);
		this.tile = tile;

		slots = new ArrayList<>();
		xSize = 218;
		ySize = 202;
		//Start at index 36, because all slots before 36 are player inventory slots
		Iterator<Slot> slotIterator = container.inventorySlots.listIterator(ContainerForestry.PLAYER_INV_SLOTS);
		while (slotIterator.hasNext()) {
			Slot slot = slotIterator.next();
			if (slot instanceof SlotFilteredInventory) {
				SlotFilteredInventory slotDatabase = (SlotFilteredInventory) slot;
				slotDatabase.setChangeWatcher(this);
			}
		}
		for (int i = 0; i < 24; i++) {
			WidgetDatabaseSlot slot = new WidgetDatabaseSlot(widgetManager);
			slots.add(slot);
			widgetManager.add(slot);
		}
		widgetManager.add(this.scrollBar = new WidgetScrollBar(widgetManager, 196, 19, 12, 90, SCROLLBAR_SLIDER));
		this.scrollBar.setParameters(this, 0, tile.getSizeInventory() / 4 - 6, 1);
		analyzer.init();
		analyzer.setSelectedSlot(-1);
	}


	/* Methods */
	@Nullable
	public DatabaseItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * @return the count of all valid item in {@link #sorted}
	 */
	public int getSize() {
		return sorted.size() - 1;//subtract one because the last entry is the empty item
	}

	public int getRealSize() {
		return sorted.size();
	}

	public ItemStack getSelectedItemStack() {
		if (selectedItem == null) {
			return ItemStack.EMPTY;
		}
		return selectedItem.itemStack;
	}

	public void markForSorting() {
		markedForSorting = true;
	}

	private void updateItems(String searchText) {
		if (markedForSorting) {
			sorted.clear();
			List<DatabaseItem> items = new ArrayList<>();
			boolean firstEmpty = false;
			for (int invIndex = 0; invIndex < tile.getSizeInventory(); invIndex++) {
				ItemStack stack = tile.getStackInSlot(invIndex).copy();
				if (!stack.isEmpty()) {
					items.add(new DatabaseItem(stack, invIndex));
					continue;
				}
				if (!firstEmpty) {
					firstEmpty = true;
					items.add(new DatabaseItem(stack, invIndex));
				}
			}
			DatabaseHelper.update(searchText, items, sorted);
			analyzer.updateSelected();
			updateViewedItems();

			markedForSorting = false;
		}
	}

	@Nullable
	public DatabaseItem getItem(int index) {
		if (sorted.isEmpty() || sorted.size() <= index || index < 0) {
			return null;
		}
		return sorted.get(index);
	}

	private void updateViewedItems() {
		int currentRow = scrollBar.getValue();
		if (currentRow < 0) {
			currentRow = 0;
		}
		//The inventory index of the first slot.
		int slotStart = currentRow * 4;
		//The inventory index of the last slot.
		int slotEnd = (currentRow + 6) * 4;
		if (slotEnd > tile.getSizeInventory()) {
			slotEnd = tile.getSizeInventory();
		}
		//The row of the first slot
		byte startRow = (byte) (currentRow % 2);
		//The index of the empty slot in the list.
		int emptySlot = sorted.size() - 1;
		for (int invIndex = 0; invIndex < tile.getSizeInventory(); invIndex++) {
			if (invIndex >= slotStart && invIndex < slotEnd) {
				int x = invIndex % 4;
				int y = invIndex / 4 - currentRow;
				int yOffset;
				int xOffset;
				if (startRow == 0) {
					yOffset = 25;
					xOffset = 17;
					if (y % 2 == 1) {
						xOffset = 38;
						yOffset = 38;
						y--;
					}
				} else {
					yOffset = 25;
					xOffset = 38;
					if (y % 2 == 1) {
						yOffset = 38;
						xOffset = 17;
						y--;
					}
				}
				int xPos = xOffset + x * 42;
				int yPos = yOffset + y / 2 * 25;
				//If the index is above the count of the valid items in the list, set the index to the same value like the index of the empty slot.
				int index = invIndex;
				if (sorted.size() <= index || sorted.isEmpty()) {
					index = emptySlot;
				}
				WidgetDatabaseSlot slot = slots.get(invIndex - slotStart);
				slot.update(xPos, yPos, index, index != emptySlot);
			}
		}
	}

	/* Methods - Implement GuiScreen */
	@Override
	public void initGui() {
		super.initGui();

		this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 101, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
		this.searchField.setMaxStringLength(50);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setTextColor(16777215);

		addButton(new GuiDatabaseButton<>(0, guiLeft - 18, guiTop, DatabaseHelper.ascending, this, DatabaseButton.SORT_DIRECTION_BUTTON));

		updateViewedItems();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String searchText = searchField != null ? searchField.getText() : "";
		updateItems(searchText);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (searchField != null && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
			scrollBar.setValue(0);
			markForSorting();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		Slot slot = getSlotAtPosition(mouseX, mouseY);
		if (slot != null && slot.getSlotIndex() == -1) {
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (searchField != null) {
			searchField.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof GuiDatabaseButton) {
			GuiDatabaseButton databaseButton = (GuiDatabaseButton) button;
			databaseButton.onPressed();
		}
	}

	/* Methods - Implement GuiContainer */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		if (searchField != null) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			this.searchField.drawTextBox();
		}
	}

	/* Methods - Implement GuiForestry */
	@Override
	protected void drawBackground() {
		bindTexture(textureFile);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void addLedgers() {
	}

	/* Methods - Implement GuiForestryTitled */

	@Override
	protected boolean centeredTitle() {
		return false;
	}

	/* Methods - Implement IGeneticAnalyzerProvider */
	@Override
	protected void drawSelectedSlot(int selectedSlot) {
		//Currently not used
	}

	@Override
	public ItemStack getSpecimen(int index) {
		DatabaseItem item = getSelectedItem();
		if (item == null) {
			return ItemStack.EMPTY;
		}
		return item.itemStack;
	}

	@Override
	public boolean onUpdateSelected() {
		int index = selectedItem == null ? -1 : sorted.indexOf(selectedItem);
		if (index >= 0) {
			analyzer.setSelectedSlot(index);
			return true;
		}
		return false;
	}

	@Override
	public void onSelection(int index, boolean changed) {
		if (index < 0) {
			selectedItem = null;
		} else {
			this.selectedItem = sorted != null && index >= sorted.size() ? null : sorted.get(index);
		}
	}

	@Override
	public int getSelectedSlot(int index) {
		DatabaseItem item = getItem(index);
		if (item == null) {
			return -1;
		}
		return 1 + item.invIndex;
	}

	/* Methods - Implement IScrollable */
	@Override
	public void onScroll(int value) {
		updateViewedItems();
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isPointInRegion(0, 0, xSize, ySize, mouseX + guiLeft, mouseY + guiTop);
	}

	/* Methods - Implement ISlotChangeWatcher */
	@Override
	public void onSlotChanged(IInventory inventory, int slot) {
		super.onSlotChanged(inventory, slot);
		markForSorting();
	}
}
