package genetics.api.root;

import java.util.Optional;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.individual.IIndividual;

public interface IIndividualRootHelper {
	/**
	 * Retrieve a matching {@link IRootDefinition} for the given itemstack.
	 *
	 * @param stack An itemstack possibly containing NBT data which can be converted by a species root.
	 * @return {@link IRootDefinition} if found, empty otherwise.
	 */
	<R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(ItemStack stack);

	<R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(ItemStack stack, Class<? extends R> rootClass);

	/**
	 * Retrieve a matching {@link IRootDefinition} for the given {@link IIndividual}-class.
	 *
	 * @param individualClass Class extending {@link IIndividual}.
	 * @return {@link IRootDefinition} if found, null otherwise.
	 */
	IRootDefinition getSpeciesRoot(Class<? extends IIndividual> individualClass);

	<R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(Class<? extends IIndividual> individualClass, Class<? extends R> rootClass);

	/**
	 * Retrieve a matching {@link IRootDefinition} for the given {@link IIndividual}
	 */
	<R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(IIndividual individual);

	<R extends IIndividualRoot> IRootDefinition<R> getSpeciesRoot(IIndividual individual, Class<? extends R> rootClass);

	boolean isIndividual(ItemStack stack);

	Optional<IIndividual> getIndividual(ItemStack stack);

	IAlleleTemplateBuilder createTemplate(String uid);
}
