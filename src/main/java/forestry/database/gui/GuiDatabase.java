package forestry.database.gui;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;
import forestry.core.gui.Drawable;
import forestry.core.gui.GuiForestry;
import forestry.core.gui.slots.SlotFilteredInventory;
import forestry.core.gui.widgets.IScrollable;
import forestry.core.gui.widgets.WidgetScrollBar;
import forestry.core.inventory.watchers.ISlotChangeWatcher;
import forestry.core.network.packets.PacketGuiSelectRequest;
import forestry.core.render.ColourProperties;
import forestry.core.utils.NetworkUtil;
import forestry.core.utils.Translator;
import forestry.database.DatabaseHelper;
import forestry.database.DatabaseItem;
import forestry.database.DatabaseManager;
import forestry.database.gui.buttons.DatabaseButton;
import forestry.database.gui.buttons.GuiDatabaseButton;
import forestry.database.gui.widgets.WidgetDatabaseScreen;
import forestry.database.gui.widgets.WidgetDatabaseSelectedItem;
import forestry.database.gui.widgets.WidgetDatabaseSlot;
import forestry.database.gui.widgets.WidgetDatabaseTabs;
import forestry.database.tiles.TileDatabase;
import org.lwjgl.input.Keyboard;

public class GuiDatabase extends GuiForestry<ContainerDatabase> implements ISlotChangeWatcher, IScrollable {

	private static final int SLOTS = 24;

	public final TileDatabase database;
	private final ArrayList<WidgetDatabaseSlot> slots;
	private final DatabaseManager manager;

	private GuiTextField searchField;
	private GuiDatabaseButton lockButton;
	private GuiDatabaseButton upButton;
	private GuiDatabaseButton downButton;
	private WidgetDatabaseScreen databaseScreen;
	private WidgetScrollBar scrollBar;

	public GuiDatabase(TileDatabase database, EntityPlayer player) {
		super(Constants.TEXTURE_PATH_GUI + "/database_inventory.png", new ContainerDatabase(database, player.inventory));
		this.database = database;

		slots = new ArrayList<>();
		xSize = 218;
		ySize = 202;
		manager = new DatabaseManager(database, this);

		for(Slot slot : container.inventorySlots){
			if(slot instanceof SlotFilteredInventory){
				SlotFilteredInventory slotDatabase = (SlotFilteredInventory) slot;
				slotDatabase.setChangeWatcher(this);
			}
		}
		for(int i = 0;i < SLOTS;i++){
			WidgetDatabaseSlot slot = new WidgetDatabaseSlot(widgetManager);
			slots.add(slot);
			widgetManager.add(slot);
		}
		int screenX = - 140;
		WidgetScrollBar scrollBar = new WidgetScrollBar(widgetManager, screenX + 106, 10, new Drawable(WidgetDatabaseScreen.TEXTURE, 202, 0, 3, 156), false, new Drawable(WidgetDatabaseScreen.TEXTURE, 205, 0, 3, 5));
		widgetManager.add(databaseScreen = new WidgetDatabaseScreen(widgetManager, screenX, 0, scrollBar));
		widgetManager.add(scrollBar);
		widgetManager.add(new WidgetDatabaseTabs(widgetManager, screenX, 177, databaseScreen));
		widgetManager.add(this.scrollBar = new WidgetScrollBar(widgetManager, 196, 19, 12, 90 , new Drawable(new ResourceLocation(Constants.TEXTURE_PATH_GUI + "/container/creative_inventory/tabs.png"), 232, 0, 12, 15)));
		this.scrollBar.setParameters(this, 0, database.getSizeInventory() / 4 - 6, 1);
		widgetManager.add(new WidgetDatabaseSelectedItem(widgetManager, 184, 158, manager));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String searchText = searchField != null ? searchField.getText() : "";
		manager.update(searchText);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(f, mouseX, mouseY);

		String name = Translator.translateToLocal(database.getUnlocalizedTitle());
		textLayout.line = 6;
		textLayout.drawLine(name, 8, ColourProperties.INSTANCE.get("gui.title"));

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		this.searchField.drawTextBox();
	}

	@Override
	protected void drawBackground() {
		bindTexture(textureFile);

		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
			scrollBar.setValue(0);
			manager.markForSorting();
		} else if((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_RIGHT) && downButton.enabled) {
			downButton.onPressed();
		} else if((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_LEFT) && upButton.enabled) {
			upButton.onPressed();
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		Slot slot = getSlotAtPosition(mouseX, mouseY);
		if(slot != null && slot.getSlotIndex() == -1){
			return;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Nullable
	public DatabaseItem getItem(int index){
		return manager.getItem(index);
	}

	@Nullable
	public DatabaseItem getSelectedItem(){
		return manager.getSelected();
	}

	public DatabaseManager getManager() {
		return manager;
	}

	public void onSortItems(List<DatabaseItem> sorted) {
		updateViewedItems(sorted);
	}

	public void onChangeLockState(boolean locked) {
		lockButton.setValue(locked);
	}

	@Override
	public void initGui() {
		super.initGui();

		this.guiLeft = 140 + (this.width - this.xSize - 125) / 2;

		this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 101, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
		this.searchField.setMaxStringLength(50);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setTextColor(16777215);

		//Left buttons
		this.buttonList.add(new GuiDatabaseButton(0, guiLeft - 18, guiTop, DatabaseHelper.ascending, manager, DatabaseButton.SORT_DIRECTION_BUTTON));
		//Selection buttons
		this.buttonList.add(upButton = new GuiDatabaseButton(1, guiLeft + 184, guiTop + 144, "", manager, DatabaseButton.SELECTED_UP));
		this.buttonList.add(downButton = new GuiDatabaseButton(2, guiLeft + 184, guiTop + 178, "", manager, DatabaseButton.SELECTED_DOWN));

		updateViewedItems();
	}

	public void onUpdateSelectedSlot(boolean changed){
		upButton.enabled = manager.canSubtract();
		downButton.enabled = manager.canAdd();
		if(changed){
			int index = getSelectedItem().invIndex;
			database.selectedSlot = manager.getSelectedSlot();
			NetworkUtil.sendToServer(new PacketGuiSelectRequest(ContainerDatabase.SELECT_ID, index));
			//databaseScreen.onChangeTabOrIndividual();
			databaseScreen.onItemChange(manager.getSelectedItemStack());
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button instanceof GuiDatabaseButton){
			GuiDatabaseButton databaseButton = (GuiDatabaseButton) button;
			databaseButton.onPressed();
		}
	}

	@Override
	protected void addLedgers() {
	}

	@Override
	public void onScroll(int value) {
		updateViewedItems();
	}

	@Override
	public boolean isFocused(int mouseX, int mouseY) {
		return isPointInRegion(0, 0, xSize, ySize, mouseX + guiLeft, mouseY + guiTop);
	}

	@Override
	protected boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop) {
		boolean isInsideScreen = databaseScreen.isMouseOver(mouseX - guiLeft, mouseY - guiTop);
		return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop) && !isInsideScreen;
	}

	@Override
	public void onSlotChanged(IInventory inventory, int slot) {
		if(manager.getSelected() != null && slot == manager.getSelected().invIndex){
			databaseScreen.onItemChange(manager.getSelectedItemStack());
		}
		manager.markForSorting();
	}

	public void updateViewedItems(){
		updateViewedItems(manager.getSorted());
	}

	public void updateViewedItems(List<DatabaseItem> sorted) {
		int currentRow = scrollBar.getValue();
		if (currentRow < 0) {
			currentRow = 0;
		}
		//The inventory index of the first slot.
		int slotStart = currentRow * 4;
		//The inventory index of the last slot.
		int slotEnd = (currentRow + 6) * 4;
		if(slotEnd > database.getSizeInventory()){
			slotEnd = database.getSizeInventory();
		}
		//The row of the first slot
		byte startRow = (byte)(currentRow % 2);
		//The index of the empty slot in the list.
		int emptySlot = sorted.size() - 1;
		for(int invIndex = 0;invIndex < database.getSizeInventory();invIndex++){
			if(invIndex >= slotStart && invIndex < slotEnd) {
				int x = invIndex % 4;
				int y = invIndex / 4 - currentRow;
				int yOffset;
				int xOffset;
				if(startRow == 0) {
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
				if(sorted.size() <= index || sorted.isEmpty()){
					index = emptySlot;
				}
				WidgetDatabaseSlot slot = slots.get(invIndex - slotStart);
				slot.update(xPos, yPos, index, index != emptySlot);
			}
		}
		//Create screen elements
		if(!sorted.isEmpty() && databaseScreen.size() == 0) {
			databaseScreen.onItemChange(manager.getSelectedItemStack());
			if(database.selectedSlot != -1) {
				manager.setSelectedSlot(database.selectedSlot);
			}
		}
	}
}
