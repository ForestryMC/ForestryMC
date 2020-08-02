package forestry.core.genetics;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import forestry.api.genetics.products.IDynamicProductList;
import forestry.api.genetics.products.IMutableProductList;
import forestry.api.genetics.products.IProductList;
import forestry.api.genetics.products.Product;

public final class ProductList implements IDynamicProductList {

    private final ImmutableList<Product> constantProducts;
    private final ImmutableList<IDynamicProductList> dynamics;

    public ProductList(ImmutableList<Product> constantProducts, ImmutableList<IDynamicProductList> dynamics) {
        this.constantProducts = constantProducts;
        this.dynamics = dynamics;
    }

    @Override
    public Collection<Product> getConstantProducts() {
        return constantProducts;
    }

    @Override
    public Collection<Product> getPossibleProducts() {
        List<Product> products = new ArrayList<>(constantProducts);
        dynamics.forEach(IDynamicProductList::getPossibleProducts);
        return products;
    }

    @Override
    public void addProducts(NonNullList<ItemStack> stacks, Function<Product, Float> modifier, Random rand) {
        constantProducts.forEach(product -> {
            if (rand.nextFloat() < modifier.apply(product)) {
                stacks.add(product.copyStack());
            }
        });
        dynamics.forEach(child -> child.addProducts(stacks, modifier, rand));
    }

    @Override
    public void addProducts(IBlockReader reader, BlockPos pos, NonNullList<ItemStack> stacks, Function<Product, Float> modifier, Random rand) {
        constantProducts.forEach(product -> {
            if (rand.nextFloat() < modifier.apply(product)) {
                stacks.add(product.copyStack());
            }
        });
        dynamics.forEach(child -> child.addProducts(reader, pos, stacks, modifier, rand));
    }

    public static class Mutable implements IMutableProductList, IProductList {
        private final List<Product> products = new ArrayList<>();
        private final List<Product.Unbaked> productsUnbaked = new ArrayList<>();
        private final List<IMutableProductList> unbakedLists = new ArrayList<>();
        private final List<IDynamicProductList> dynamicChildren = new ArrayList<>();

        @Override
        public IMutableProductList addProduct(ItemStack stack, float chance) {
            products.add(new Product(stack, chance));
            return this;
        }

        @Override
        public IMutableProductList addProduct(Supplier<ItemStack> stack, float chance) {
            productsUnbaked.add(new Product.Unbaked(stack, chance));
            return this;
        }

        @Override
        public IMutableProductList addList(IMutableProductList list) {
            unbakedLists.add(list);
            return this;
        }

        @Override
        public IMutableProductList addDynamic(IDynamicProductList list) {
            dynamicChildren.add(list);
            return this;
        }

        @Override
        public Collection<Product> getConstantProducts() {
            return Collections.emptyList();
        }

        @Override
        public void addProducts(NonNullList<ItemStack> stacks, Function<Product, Float> modifier, Random rand) {
            //Add nothing because this is the unbaked version, only implemented so this and the baked version can be stored in one variable.
        }

        @Override
        public ProductList bake() {
            ImmutableList.Builder<Product> builder = new ImmutableList.Builder<>();
            builder.addAll(products);
            productsUnbaked.forEach(unbaked -> builder.add(unbaked.bake()));
            unbakedLists.forEach(list -> builder.addAll(list.bake().getConstantProducts()));
            return new ProductList(builder.build(), ImmutableList.copyOf(dynamicChildren));
        }
    }
}
