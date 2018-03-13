package forestry.core.inventory;

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
		return remainder.isEmpty() || remainder.getCount() < stack.getCount();
	}

	@Override
	public boolean canTakeStackFromSlot(ItemStack stack) {
		return !inv.extractItem(slot, 1, true).isEmpty();
	}

	@Override
	public ItemStack decreaseStackInSlot() {
		return inv.extractItem(slot, 1, false);
	}

	@Override
	public ItemStack getStackInSlot() {
		return inv.getStackInSlot(slot);
	}

}
