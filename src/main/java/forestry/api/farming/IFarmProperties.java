package forestry.api.farming;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

public interface IFarmProperties {

    /**
     * @return The amount of fertilizer that the {@link IFarmHousing} automatically removes after this logic cultivated
     * a block or harvested a crop.
     */
    int getFertilizerConsumption(IFarmHousing housing);

    /**
     * @param hydrationModifier A modifier that depends on the weather and the biome of the farm.
     * @return The amount of water that the {@link IFarmHousing} automatically removes after this logic cultivated
     * a block or harvested a crop.
     */
    int getWaterConsumption(IFarmHousing housing, float hydrationModifier);

    ITextComponent getDisplayName(boolean manual);

    String getTranslationKey();

    /**
     * @return the itemStack that represents this farm logic. Used as an icon for the farm logic.
     */
    ItemStack getIcon();

    /**
     * @return true if the given block state is a valid soil state.
     */
    boolean isAcceptedSoil(BlockState block);

    /**
     * @return true if the given stack is the {@link ItemStack} of a soil.
     */
    boolean isAcceptedResource(ItemStack itemStack);

    /**
     * Checks if the given stack is a seedling (plantable sapling, seed, etc.) for any {@link IFarmable} of this farm.
     */
    boolean isAcceptedSeedling(ItemStack itemstack);

    boolean isAcceptedWindfall(ItemStack itemstack);

    Collection<Soil> getSoils();

    Collection<IFarmable> getFarmables();

    Collection<IFarmableInfo> getFarmableInfo();

    /**
     * Returns the instance of the manual or managed {@link IFarmLogic}.
     */
    IFarmLogic getLogic(boolean manuel);
}
