package forestry.storage;

import java.util.function.Predicate;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;

public class BackpackFilter implements Predicate<ItemStack> {

	private final Tag<Item> accept;
	private final Tag<Item> reject;

	public BackpackFilter(Tag<Item> accept, Tag<Item> reject) {
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
