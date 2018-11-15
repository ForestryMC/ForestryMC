package forestry.api.genetics;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpeciesDisplayHelper<I extends IIndividual, S extends IAlleleSpecies> {
	/**
	 * Retrieves a stack that can and should only be used on the client side in a gui.
	 *
	 * @return A empty stack, if the species was not registered before the creation of this handler or if the species is
	 * not a species of the {@link ISpeciesRoot}.
	 */
	ItemStack getDisplayStack(IAlleleSpecies species, ISpeciesType type);

	ItemStack getDisplayStack(IAlleleSpecies species);
}
