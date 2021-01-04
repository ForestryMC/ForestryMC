/*
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 */
package forestry.api.genetics;

import forestry.api.apiculture.IBeekeepingMode;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Collection;
import java.util.Collections;

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
     */
    void registerBirth(IIndividual individual);

    /**
     * Register the pickup of an individual.
     */
    void registerPickup(IIndividual individual);

    /**
     * Marks a species as discovered. Should only be called from registerIndividual normally.
     */
    void registerSpecies(IAlleleSpecies species);

    /**
     * Register a successful mutation. Will mark it as discovered.
     */
    void registerMutation(IMutation mutation);

    /**
     * Queries the tracker for discovered species.
     *
     * @param mutation Mutation to query for.
     * @return true if the mutation has been discovered.
     */
    boolean isDiscovered(IMutation mutation);

    /**
     * Queries the tracker for discovered species.
     *
     * @param species Species to check.
     * @return true if the species has been bred.
     */
    boolean isDiscovered(IAlleleSpecies species);

    /**
     * @return A collection that contains the {@link IAllele#getRegistryName()}s of all discovered species.
     */
    default Collection<String> getDiscoveredSpecies() {
        return Collections.emptyList();
    }

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
    void synchToPlayer(PlayerEntity player);

    /* LOADING & SAVING */
    void decodeFromNBT(CompoundNBT compound);

    void encodeToNBT(CompoundNBT compound);

}
