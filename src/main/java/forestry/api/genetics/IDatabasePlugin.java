/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * An ISpeciesPlugin provides methods that are used in the alyzer and database to display information about an
 * individual.
 */
@SideOnly(Side.CLIENT)
public interface IDatabasePlugin<I extends IIndividual> {

	/**
	 * A map that contains a {@link ItemStack} for every possible {@link IAlleleSpecies}.
	 * This item stack represents the species in the database and alyzer gui.
	 *
	 * The key of the map is the uid of the species.
	 */
	Map<String, ItemStack> getIndividualStacks();

	/* ALYZER */
	List<String> getHints();

	IDatabaseTab[] getTabs();
}
