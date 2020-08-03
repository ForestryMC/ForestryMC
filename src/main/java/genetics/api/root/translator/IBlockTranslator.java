package genetics.api.root.translator;

import genetics.api.individual.IIndividual;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Translates blockStates into genetic data.
 * Used by bees and butterflies to convert and pollinate foreign leaf blocks.
 */
public interface IBlockTranslator<I extends IIndividual> {
    @Nullable
    I getIndividualFromObject(BlockState blockState);

    default ItemStack getGeneticEquivalent(BlockState blockState) {
        return ItemStack.EMPTY;
    }
}
