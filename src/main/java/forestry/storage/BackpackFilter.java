package forestry.storage;

import forestry.core.utils.TagUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BackpackFilter implements Predicate<ItemStack> {

	private final TagKey<Item> acceptKey;
	private final TagKey<Item> rejectKey;
	@Nullable
	private HolderSet<Item> cachedAccept;
	@Nullable
	private HolderSet<Item> cachedReject;

	public BackpackFilter(TagKey<Item> acceptKey, TagKey<Item> rejectKey) {
		this.acceptKey = acceptKey;
		this.rejectKey = rejectKey;
	}

	private HolderSet<Item> getAccept() {
		if (cachedAccept == null) {
			cachedAccept = getHolderSet(acceptKey);
		}
		return cachedAccept;
	}

	private HolderSet<Item> getReject() {
		if (cachedReject == null) {
			cachedReject = getHolderSet(rejectKey);
		}
		return cachedReject;
	}

	private static HolderSet<Item> getHolderSet(TagKey<Item> tagKey) {
		return Registry.ITEM.getTag(tagKey)
				.orElseThrow(() -> new IllegalArgumentException("No tag holder set found for tag key: " + tagKey));
	}

	@Override
	public boolean test(ItemStack itemStack) {
		// The backpack denies anything except what is allowed,
		// but from what is allowed you can say what will be rejected (like an override)
		// This allows broad wildcard "accept" types where you can still reject certain ones.
		return TagUtil.getHolder(itemStack)
			.map(itemHolder -> getAccept().contains(itemHolder) && !getReject().contains(itemHolder))
			.orElse(false);
	}
}
