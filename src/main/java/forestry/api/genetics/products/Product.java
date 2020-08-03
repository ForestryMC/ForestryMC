package forestry.api.genetics.products;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

/**
 * A pair that consists of a {@link ItemStack} and a float that represents the chance that the stack can be produced.
 * This pair is used in the {@link IProductList} to represent a product that can be produced.
 */
public final class Product {
    private final ItemStack stack;
    private final float chance;

    public Product(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }

    public ItemStack copyStack() {
        return stack.copy();
    }

    public ItemStack getStack() {
        return stack;
    }

    public Item getItem() {
        return stack.getItem();
    }

    public float getChance() {
        return chance;
    }

    @Override
    public int hashCode() {
        int code = 1;
        code = 31 * code + stack.getItem().hashCode();
        code = 31 * code + stack.getCount();
        if (stack.getTag() != null) {
            code = 31 * code + stack.getTag().hashCode();
        }
        code = 31 * code + Float.hashCode(code);
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Product)) {
            return false;
        }
        return ItemStack.areItemStacksEqual(stack, ((Product) obj).stack) && ((Product) obj).chance == chance;
    }

    /**
     * A unbaked variant of the product. It is needed to lazy load the item stacks because not all items are created at
     * the stage the {@link IMutableProductList} gets created.
     * It will be baked to a {@link Product} at {@link IMutableProductList#bake()}.
     */
    public static class Unbaked {
        private final Supplier<ItemStack> stackSupplier;
        private final float chance;

        public Unbaked(Supplier<ItemStack> stackSupplier, float chance) {
            this.stackSupplier = stackSupplier;
            this.chance = chance;
        }

        public Product bake() {
            return new Product(stackSupplier.get(), chance);
        }
    }
}
