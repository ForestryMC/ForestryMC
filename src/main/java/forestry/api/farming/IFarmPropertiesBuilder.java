package forestry.api.farming;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

public interface IFarmPropertiesBuilder {
    IFarmPropertiesBuilder setIcon(Supplier<ItemStack> stackSupplier);

    default IFarmPropertiesBuilder setFertilizer(int fertilizer) {
        return setFertilizer((housing) -> fertilizer);
    }

    IFarmPropertiesBuilder setFertilizer(ToIntFunction<IFarmHousing> consumption);

    default IFarmPropertiesBuilder setWater(int waterConsumption) {
        return setWater((housing, hydrationModifier) -> waterConsumption);
    }

    default IFarmPropertiesBuilder setWater(ToIntFunction<Float> waterConsumption) {
        return setWater((housing, hydrationModifier) -> waterConsumption.applyAsInt(hydrationModifier));
    }

    IFarmPropertiesBuilder setWater(ToIntBiFunction<IFarmHousing, Float> waterConsumption);

	/*IFarmPropertiesBuilder setResourcePredicate(Predicate<ItemStack> isResource);

	IFarmPropertiesBuilder setSeedlingPredicate(Predicate<ItemStack> isSeedling);

	IFarmPropertiesBuilder setWindfallPredicate(Predicate<ItemStack> isWindfall);*/

    default IFarmPropertiesBuilder addSoil(Block block) {
        return addSoil(new ItemStack(block), block.getDefaultState());
    }

    IFarmPropertiesBuilder addSoil(ItemStack resource, BlockState soilState);

    IFarmPropertiesBuilder addSeedlings(ItemStack... seedling);

    IFarmPropertiesBuilder addSeedlings(Collection<ItemStack> seedling);

    IFarmPropertiesBuilder addProducts(ItemStack... products);

    IFarmPropertiesBuilder addProducts(Collection<ItemStack> products);

    IFarmPropertiesBuilder addFarmables(String... identifiers);

    IFarmPropertiesBuilder setFactory(BiFunction<IFarmProperties, Boolean, IFarmLogic> factory);

    IFarmPropertiesBuilder setTranslationKey(String translationKey);

    IFarmProperties create();
}
