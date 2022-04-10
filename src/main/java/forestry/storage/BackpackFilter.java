package forestry.storage;

import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class BackpackFilter implements Predicate<ItemStack> {

	private final ITag<Item> accept;
	private final ITag<Item> reject;

	public BackpackFilter(ITag<Item> accept, ITag<Item> reject) {
		this.accept = accept;
		this.reject = reject;
	}

	@Override
	public boolean test(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		}

		// I think that the backpack denies anything except what is allowed, but from what is allowed you can say
		// what will be rejected (like an override)
		Item item = itemStack.getItem();
		return accept.contains(item) && !reject.contains(item);
	}
}
