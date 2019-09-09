package forestry.sorting.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import forestry.sorting.tiles.TileGeneticFilter;

public class ItemHandlerFilter implements IItemHandler {
	private final TileGeneticFilter filter;
	private final IItemHandler itemHandler;
	private final Direction facing;

	public ItemHandlerFilter(TileGeneticFilter filter, Direction facing) {
		this.filter = filter;
		this.facing = facing;
		this.itemHandler = new InvWrapper(filter);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack remaining = stack;
		for (Direction facing : filter.getValidDirections(stack, facing)) {
			remaining = itemHandler.insertItem(facing.getIndex(), stack, simulate);
			if (remaining.isEmpty()) {
				return ItemStack.EMPTY;
			}
		}
		return remaining;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	//TODO
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return itemHandler.isItemValid(slot, stack);
	}
}
