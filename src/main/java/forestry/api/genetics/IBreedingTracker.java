/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 * 
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.apiculture.IBeekeepingMode;

/**
 * Keeps track of who bred, discovered, and researched which species in a world.
 * 
 * @author SirSengir
 */
public interface IBreedingTracker {

	/**
	 * @return Name of the current {@link IBeekeepingMode}.
	 */
	String getModeName();

	/**
	 * Set the current {@link IBeekeepingMode}.
	 */
	void setModeName(String name);

	/**
	 * @return Amount of species discovered.
	 */
	int getSpeciesBred();

	/**
	 * Register the birth of an individual. Will mark it as discovered.
	 * 
	 * @param individual
	 */
	void registerBirth(IIndividual individual);

	/**
	 * Register the pickup of an individual.
	 * 
	 * @param individual
	 */
	void registerPickup(IIndividual individual);
	
	/**
	 * Marks a species as discovered. Should only be called from registerIndividual normally.
	 * 
	 * @param species
	 */
	void registerSpecies(IAlleleSpecies species);

	/**
	 * Register a successful mutation. Will mark it as discovered.
	 * 
	 * @param mutation
	 */
	void registerMutation(IMutation mutation);

	/**
	 * Queries the tracker for discovered species.
	 * 
	 * @param mutation
	 *            Mutation to query for.
	 * @return true if the mutation has been discovered.
	 */
	boolean isDiscovered(IMutation mutation);

	/**
	 * Queries the tracker for discovered species.
	 * 
	 * @param species
	 *            Species to check.
	 * @return true if the species has been bred.
	 */
	boolean isDiscovered(IAlleleSpecies species);

	/**
	 * Register a successfully researched mutation.
	 * Mutations are normally researched in the Escritoire.
	 * Researched mutations may have bonuses such as occurring at a higher rate.
	 * Researched mutations count as discovered.
	 */
	void researchMutation(IMutation mutation);

	/**
	 * @return true if the mutation has been researched.
	 */
	boolean isResearched(IMutation mutation);

	/**
	 * Synchronizes the tracker to the client side.
	 * Before Forestry 4.2.1: Should be called before opening any gui needing that information.
	 * Since Forestry 4.2.1: Breeding tracker should be automatically synced, only Forestry should need to call this.
	 */
	void synchToPlayer(EntityPlayer player);

	/* LOADING & SAVING */
	void decodeFromNBT(NBTTagCompound nbttagcompound);

	void encodeToNBT(NBTTagCompound nbttagcompound);

}
