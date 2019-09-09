/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.IWorld;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.individual.IIndividual;
import genetics.api.organism.IOrganismType;
import genetics.api.root.IIndividualRoot;

/**
 * Describes a class of species (i.e. bees, trees, butterflies), provides helper functions and access to common functionality.
 */
public interface IForestrySpeciesRoot<I extends IIndividual> extends IIndividualRoot<I> {

	/**
	 * @return Integer denoting the number of (counted) species of this type in the world.
	 */
	int getSpeciesCount();

	/**
	 * Used to check whether the given {@link IIndividual} is member of this class.
	 *
	 * @param individual {@link IIndividual} to check.
	 * @return true if the individual is member of this class, false otherwise.
	 */
	boolean isMember(IIndividual individual);

	/**
	 * Species type used to represent this species in icons
	 */
	IOrganismType getIconType();

	/* BREEDING TRACKER */
	IBreedingTracker getBreedingTracker(IWorld world, @Nullable GameProfile player);

	/**
	 * The type of the species that will be used at the given position of the mutation recipe in the gui.
	 *
	 * @param position 0 = first parent, 1 = second parent, 2 = result
	 */
	default IOrganismType getTypeForMutation(int position) {
		return getIconType();
	}

	/**
	 * Plugin to add information for the handheld genetic analyzer.
	 */
	IAlyzerPlugin getAlyzerPlugin();

	/**
	 * Plugin to add information for the handheld genetic analyzer and the database.
	 *
	 * @since 5.7
	 */
	@Nullable
	@OnlyIn(Dist.CLIENT)
	default IDatabasePlugin getSpeciesPlugin() {
		return null;
	}
}
