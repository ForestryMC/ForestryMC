package forestry.core.inventory;

import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class InvIterator implements Iterator<IInvSlot> {
	private final IItemHandler inv;
	private int slot = 0;

	public InvIterator(IItemHandler inv) {
		this.inv = inv;
	}

	@Override
	public boolean hasNext() {
		return slot < inv.getSlots();
	}

	@Override
	public IInvSlot next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return new InvSlot(inv, slot++);
	}
}
