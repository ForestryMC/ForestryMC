package forestry.core.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public class TagUtil {
    public static Optional<Holder<Item>> getHolder(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return Optional.empty();
        }
        Item item = itemStack.getItem();
        return getHolder(item, Registry.ITEM);
    }

    public static Optional<Holder<Fluid>> getHolder(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return Optional.empty();
        }
        Fluid fluid = fluidStack.getFluid();
        return getHolder(fluid, Registry.FLUID);
    }

    public static Optional<Holder<Block>> getHolder(BlockState blockState) {
        Block block = blockState.getBlock();
        return getHolder(block, Registry.BLOCK);
    }

    public static <T> Optional<Holder<T>> getHolder(T value, Registry<T> registry) {
        return registry.getResourceKey(value)
            .flatMap(registry::getHolder);
    }
}
