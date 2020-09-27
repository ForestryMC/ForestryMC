package forestry.core.genetics;

import forestry.api.genetics.products.IDynamicProductList;
import forestry.api.genetics.products.IMutableProductList;
import forestry.api.genetics.products.Product;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Collection;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ProductListWrapper implements IDynamicProductList, IMutableProductList {
    private ProductListWrapper() {
    }

    public static ProductListWrapper create() {
        return new Mutable();
    }

    @Override
    public abstract ProductListWrapper bake();

    private static class Baked extends ProductListWrapper {
        private final ProductList list;

        private Baked(ProductList list) {
            this.list = list;
        }

        @Override
        public Baked bake() {
            return this;
        }

        @Override
        public Collection<Product> getPossibleProducts() {
            return list.getPossibleProducts();
        }

        @Override
        public Collection<Product> getConstantProducts() {
            return list.getConstantProducts();
        }

        @Override
        public void addProducts(NonNullList<ItemStack> stacks, Function<Product, Float> modifier, Random rand) {
            list.addProducts(stacks, modifier, rand);
        }

        @Override
        public void addProducts(
                IBlockReader reader,
                BlockPos pos,
                NonNullList<ItemStack> stacks,
                Function<Product, Float> modifier,
                Random rand
        ) {
            list.addProducts(reader, pos, stacks, modifier, rand);
        }

        @Override
        public IMutableProductList addProduct(ItemStack stack, float chance) {
            throw new IllegalStateException("This product list was already baked, you can no longer add items to it");
        }

        @Override
        public IMutableProductList addProduct(Supplier<ItemStack> stack, float chance) {
            throw new IllegalStateException("This product list was already baked, you can no longer add items to it");
        }

        @Override
        public IMutableProductList addList(IMutableProductList list) {
            throw new IllegalStateException("This product list was already baked, you can no longer add lists to it");
        }

        @Override
        public IMutableProductList addDynamic(IDynamicProductList list) {
            throw new IllegalStateException("This product list was already baked, you can no longer add lists to it");
        }
    }

    private static class Mutable extends ProductListWrapper {
        private final ProductList.Mutable list;

        private Mutable() {
            this.list = new ProductList.Mutable();
        }

        @Override
        public ProductListWrapper bake() {
            return new Baked(list.bake());
        }

        @Override
        public Collection<Product> getPossibleProducts() {
            throw new IllegalStateException(
                    "This product list is not baked yet, you can not get any product information from it.");
        }

        @Override
        public Collection<Product> getConstantProducts() {
            throw new IllegalStateException(
                    "This product list is not baked yet, you can not get any product information from it.");
        }

        @Override
        public void addProducts(NonNullList<ItemStack> stacks, Function<Product, Float> modifier, Random rand) {
            throw new IllegalStateException(
                    "This product list is not baked yet, you can not get any product information from it.");
        }

        @Override
        public IMutableProductList addProduct(ItemStack stack, float chance) {
            return list.addProduct(stack, chance);
        }

        @Override
        public IMutableProductList addProduct(Supplier<ItemStack> stack, float chance) {
            return list.addProduct(stack, chance);
        }

        @Override
        public IMutableProductList addList(IMutableProductList list) {
            return this.list.addList(list);
        }

        @Override
        public IMutableProductList addDynamic(IDynamicProductList list) {
            return this.list.addDynamic(list);
        }
    }
}
