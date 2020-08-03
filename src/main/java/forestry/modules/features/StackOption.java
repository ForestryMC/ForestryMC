package forestry.modules.features;

import net.minecraft.item.ItemStack;

import java.util.function.Consumer;

public enum StackOption implements Consumer<ItemStack> {
    MAX_COUNT {
        @Override
        public void accept(ItemStack stack) {
            int maxCount = stack.isStackable() ? 64 : 1;
            stack.setCount(maxCount);
        }
    }
}
