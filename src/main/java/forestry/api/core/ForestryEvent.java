/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.entity.player.PlayerEntity;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.eventbus.api.Event;

import genetics.api.alleles.IAlleleSpecies;
import genetics.api.mutation.IMutation;
import genetics.api.root.IRootDefinition;

import forestry.api.genetics.IBreedingTracker;

public abstract class ForestryEvent extends Event {

	private static abstract class BreedingEvent extends ForestryEvent {
		public final IRootDefinition root;
		public final IBreedingTracker tracker;
		public final GameProfile username;

		private BreedingEvent(IRootDefinition root, GameProfile username, IBreedingTracker tracker) {
			this.root = root;
			this.username = username;
			this.tracker = tracker;
		}
	}

	public static class SpeciesDiscovered extends BreedingEvent {
		public final IAlleleSpecies species;

		public SpeciesDiscovered(IRootDefinition root, GameProfile username, IAlleleSpecies species, IBreedingTracker tracker) {
			super(root, username, tracker);
			this.species = species;
		}
	}

	public static class MutationDiscovered extends BreedingEvent {
		public final IMutation allele;

		public MutationDiscovered(IRootDefinition root, GameProfile username, IMutation allele, IBreedingTracker tracker) {
			super(root, username, tracker);
			this.allele = allele;
		}
	}

	public static class SyncedBreedingTracker extends ForestryEvent {
		public final IBreedingTracker tracker;
		public final PlayerEntity player;

		public SyncedBreedingTracker(IBreedingTracker tracker, PlayerEntity player) {
			this.tracker = tracker;
			this.player = player;
		}
	}
}
