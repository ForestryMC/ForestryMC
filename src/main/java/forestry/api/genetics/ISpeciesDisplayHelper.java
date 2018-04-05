package forestry.api.genetics;

import java.util.Optional;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpeciesDisplayHelper {
	/**
	 * Plugin to add information for the genetic database.
	 */
	Optional<IDatabasePlugin> getDatabasePlugin();

	/**
	 * Plugin to add information for the handheld genetic analyzer.
	 */
	Optional<IAlyzerPlugin> getAlyzerPlugin();

	/**
	 * Retrieves a stack that can and should only be used on the client side in a gui.
	 *
	 * @return A empty stack, if the species was not registered before the creation of this handler or if the species is
	 * 			not a species of the {@link ISpeciesRoot}.
	 */
	ItemStack getDisplayStack(IAlleleSpecies species, ISpeciesType type);

	ItemStack getDisplayStack(IAlleleSpecies species);
}
