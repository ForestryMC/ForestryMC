package genetics.api.root.translator;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

/**
 * Translates items into genetic data. Used by the treealyzer and the farm to convert foreign saplings.
 */
public interface IItemTranslator<I extends IIndividual> {
	@Nullable
	I getIndividualFromObject(ItemStack itemStack);

	default ItemStack getGeneticEquivalent(ItemStack itemStack) {
		return ItemStack.EMPTY;
	}
}
