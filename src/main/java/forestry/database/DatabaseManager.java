package forestry.database;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.database.gui.GuiDatabase;
import forestry.database.tiles.TileDatabase;

public class DatabaseManager {

	private final ArrayList<DatabaseItem> sorted = new ArrayList<>();
	private final TileDatabase tile;
	public final GuiDatabase gui;
	private boolean markedForSorting;
	/**
	 * the index of the selected item in {@link #sorted}
	 */
	private int selectedSlot;
	@Nullable
	private DatabaseItem selected;

	public DatabaseManager(TileDatabase tile, GuiDatabase gui) {
		this.tile = tile;
		this.gui = gui;
	}

	public boolean canAdd(){
		return selectedSlot + 1 < getSize();
	}

	public boolean canSubtract(){
		int selectedSlot = this.selectedSlot -1;
		return selectedSlot < getSize() && selectedSlot >= 0;
	}

	/**
	 * @return the count of all valid item in {@link #sorted}
	 */
	public int getSize(){
		return sorted.size() - 1;//subtract one because the last entry is the empty item
	}

	public int getRealSize(){
		return sorted.size();
	}

	public void setSelectedSlot(int selectedSlot) {
		int oldSelected = this.selectedSlot;
		this.selectedSlot = selectedSlot;
		if(selectedSlot < 0){
			selected = null;
		}else {
			this.selected = sorted.get(selectedSlot);
		}
		gui.onUpdateSelectedSlot(oldSelected != selectedSlot && selected != null);
	}

	public int getSelectedSlot() {
		return selectedSlot;
	}

	@Nullable
	public DatabaseItem getSelected() {
		return selected;
	}

	public ItemStack getSelectedItemStack() {
		if(selected == null){
			return ItemStack.EMPTY;
		}
		return selected.itemStack;
	}

	/**
	 * Check if the selected slot is contained in {@link #sorted} if not set {@link #selectedSlot} to the index of the first valid item (0).
	 */
	public void updateSelected(){
		int index = sorted.indexOf(selected);
		if(index >= 0) {
			setSelectedSlot(sorted.indexOf(selected));
			return;
		}
		if(sorted.isEmpty()){
			setSelectedSlot(-1);
			return;
		}
		int size = getSize();
		if(size <= 0){
			setSelectedSlot(-1);
			return;
		}
		if(size > selectedSlot && index != -1){
			return;
		}
		setSelectedSlot(0);
	}

	public void markForSorting(){
		markedForSorting = true;
	}

	public void update(String searchText){
		if(markedForSorting){
			markedForSorting = false;
			sortItems(searchText);
		}
	}

	public void sortItems(String searchText){
		sorted.clear();
		List<DatabaseItem> items = new ArrayList<>();
		boolean firstEmpty = false;
		for(int invIndex = 0;invIndex < tile.getSizeInventory();invIndex++){
			ItemStack stack = tile.getStackInSlot(invIndex).copy();
			if(!stack.isEmpty()) {
				items.add(new DatabaseItem(stack, invIndex));
				continue;
			}
			if(!firstEmpty){
				firstEmpty = true;
				items.add(new DatabaseItem(stack, invIndex));
			}
		}
		DatabaseHelper.update(searchText, items, sorted);
		updateSelected();
		gui.onSortItems(sorted);
	}

	@Nullable
	public DatabaseItem getItem(int index){
		if(sorted.isEmpty() || sorted.size() <= index || index < 0){
			return null;
		}
		return sorted.get(index);
	}

	public ArrayList<DatabaseItem> getSorted() {
		return sorted;
	}
}
