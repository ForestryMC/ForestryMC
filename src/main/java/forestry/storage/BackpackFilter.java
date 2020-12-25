package forestry.storage;

import forestry.api.storage.IBackpackFilterConfigurable;
import forestry.core.utils.ItemStackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;

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
    public void acceptTagName(String tag) {
//        BlockTags.LOGS
        //		if (OreDictionary.doesOreNameExist(oreDictName)) {
        //			int oreId = OreDictionary.getOreID(oreDictName);
        //			this.validOreIds.add(oreId);
        //		}	//TODO tags
    }

    @Override
    public void rejectTagName(String tag) {
        //		if (OreDictionary.doesOreNameExist(oreDictName)) {
        //			int oreId = OreDictionary.getOreID(oreDictName);
        //			this.validOreIds.remove(oreId);
        //		} //TODO tags
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
        return acceptedItemStacks.contains(itemStackStringWild);

        //		int meta = itemStack.getMetadata();
        //		if (meta != OreDictionary.WILDCARD_VALUE) {
        //			String itemStackString = itemStackStringWild + ':' + meta;
        //			if (rejectedItemStacks.contains(itemStackString)) {
        //				return false;
        //			}
        //			if (acceptedItemStacks.contains(itemStackString)) {
        //				return true;
        //			}
        //		}
        //
        //		int[] oreIds = OreDictionary.getOreIDs(itemStack);
        //		for (int oreId : oreIds) {
        //			if (validOreIds.contains(oreId)) {
        //				acceptedItemStacks.add(itemStackStringWild);
        //				return true;
        //			}
        //		}	//TODO tags
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
