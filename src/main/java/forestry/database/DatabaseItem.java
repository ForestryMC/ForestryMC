package forestry.database;

import net.minecraft.world.item.ItemStack;

public class DatabaseItem {
	public final ItemStack itemStack;
	public final int invIndex;

	public DatabaseItem(ItemStack itemStack, int invIndex) {
		this.itemStack = itemStack;
		this.invIndex = invIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DatabaseItem other)) {
			return false;
		}
		return other.invIndex == invIndex && other.itemStack.isEmpty() == itemStack.isEmpty();
	}
}
