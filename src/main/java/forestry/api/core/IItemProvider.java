package forestry.api.core;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import forestry.modules.features.StackOption;

public interface IItemProvider<I extends Item> {
    boolean hasItem();

    @Nullable
    I getItem();

    I item();

    default ItemStack stack() {
        return stack(1);
    }

    default ItemStack stack(int amount) {
        if (hasItem()) {
            return new ItemStack(item(), amount);
        }
        throw new IllegalStateException("This feature has no item to create a stack for.");
    }

    default ItemStack stack(StackOption... options) {
        ItemStack stack = stack();
        for (StackOption option : options) {
            option.accept(stack);
        }
        return stack;
    }

    default boolean itemEqual(ItemStack stack) {
        return !stack.isEmpty() && itemEqual(stack.getItem());
    }

    default boolean itemEqual(Item item) {
        return hasItem() && item() == item;
    }
}
