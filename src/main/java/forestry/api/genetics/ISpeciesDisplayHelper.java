package forestry.api.genetics;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;

@OnlyIn(Dist.CLIENT)
public interface ISpeciesDisplayHelper<I extends IIndividual, S extends IAlleleSpecies> {
	/**
	 * Retrieves a stack that can and should only be used on the client side in a gui.
	 *
	 * @return A empty stack, if the species was not registered before the creation of this handler or if the species is
	 * not a species of the {@link IForestrySpeciesRoot}.
	 */
	ItemStack getDisplayStack(IAlleleSpecies species, IOrganismType type);

	ItemStack getDisplayStack(IAlleleSpecies species);
}
