package forestry.core.inventory.iterators;

import net.minecraft.item.ItemStack;

import net.minecraftforge.items.IItemHandler;

public class InvSlot implements IInvSlot {

	private final IItemHandler inv;
	protected final int slot;

	public InvSlot(IItemHandler inv, int slot) {
		this.inv = inv;
		this.slot = slot;
	}

	@Override
	public int getIndex() {
		return slot;
	}

	@Override
	public boolean canPutStackInSlot(ItemStack stack) {
		ItemStack remainder = inv.insertItem(slot, stack, true);
		return remainder == null || remainder.stackSize < stack.stackSize;
	}

	@Override
	public boolean canTakeStackFromSlot(ItemStack stack) {
		return inv.extractItem(slot, 1, true) != null;
	}

	@Override
	public ItemStack decreaseStackInSlot() {
		return inv.extractItem(slot, 1, false);
	}

	@Override
	public ItemStack getStackInSlot() {
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack == null || stack.stackSize <= 0) {
			return null;
		}
		return stack;
	}

}
