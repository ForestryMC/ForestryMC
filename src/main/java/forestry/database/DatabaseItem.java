package forestry.database;

import net.minecraft.item.ItemStack;

public class DatabaseItem {
	public final ItemStack itemStack;
	public final int invIndex;

	public DatabaseItem(ItemStack itemStack, int invIndex) {
		this.itemStack = itemStack;
		this.invIndex = invIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DatabaseItem)) {
			return false;
		}
		return ((DatabaseItem) obj).invIndex == invIndex && ((DatabaseItem) obj).itemStack.isEmpty() == itemStack.isEmpty();
	}
}
