package forestry.storage;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.oredict.OreDictionary;

import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.core.utils.ItemStackUtil;

public class BackpackFilter implements IBackpackFilterConfigurable {
	private final Set<String> acceptedItemStacks = new HashSet<>();
	private final Set<String> rejectedItemStacks = new HashSet<>();
	private final Set<Integer> validOreIds = new HashSet<>();

	@Override
	public void acceptItem(ItemStack validItem) {
		String itemStackString = ItemStackUtil.getStringForItemStack(validItem);
		if (itemStackString != null) {
			this.acceptedItemStacks.add(itemStackString);
		}
	}

	@Override
	public void acceptOreDictName(String oreDictName) {
		if (OreDictionary.doesOreNameExist(oreDictName)) {
			int oreId = OreDictionary.getOreID(oreDictName);
			this.validOreIds.add(oreId);
		}
	}

	@Override
	public void rejectOreDictName(String oreDictName) {
		if (OreDictionary.doesOreNameExist(oreDictName)) {
			int oreId = OreDictionary.getOreID(oreDictName);
			this.validOreIds.remove(oreId);
		}
	}

	@Override
	public void rejectItem(ItemStack invalidItem) {
		String itemStackString = ItemStackUtil.getStringForItemStack(invalidItem);
		if (itemStackString != null) {
			this.rejectedItemStacks.add(itemStackString);
		}
	}

	@Override
	public void clear() {
		acceptedItemStacks.clear();
		rejectedItemStacks.clear();
		validOreIds.clear();
	}

	@Override
	public boolean test(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		}

		Item item = itemStack.getItem();
		String itemStackStringWild = ItemStackUtil.getItemNameFromRegistryAsString(item);
		if (rejectedItemStacks.contains(itemStackStringWild)) {
			return false;
		}
		if (acceptedItemStacks.contains(itemStackStringWild)) {
			return true;
		}

		int meta = itemStack.getMetadata();
		if (meta != OreDictionary.WILDCARD_VALUE) {
			String itemStackString = itemStackStringWild + ':' + meta;
			if (rejectedItemStacks.contains(itemStackString)) {
				return false;
			}
			if (acceptedItemStacks.contains(itemStackString)) {
				return true;
			}
		}

		int[] oreIds = OreDictionary.getOreIDs(itemStack);
		for (int oreId : oreIds) {
			if (validOreIds.contains(oreId)) {
				acceptedItemStacks.add(itemStackStringWild);
				return true;
			}
		}

		return false;
	}

	public Set<Integer> getValidOreIds() {
		return validOreIds;
	}

	public Set<String> getAcceptedItemStacks() {
		return acceptedItemStacks;
	}

	public Set<String> getRejectedItemStacks() {
		return rejectedItemStacks;
	}
}
