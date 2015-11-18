/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.genetics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.packets.PacketGenomeTrackerUpdate;
import forestry.core.proxy.Proxies;

public abstract class BreedingTracker extends WorldSavedData implements IBreedingTracker {

	private static final String SPECIES_COUNT_KEY = "SpeciesCount";
	private static final String MUTATIONS_COUNT_KEY = "MutationsCount";
	private static final String RESEARCHED_COUNT_KEY = "ResearchedCount";
	private static final String SPECIES_KEY = "SD";
	private static final String MUTATIONS_KEY = "MD";
	private static final String RESEARCHED_KEY = "RD";
	private static final String MODE_NAME_KEY = "BMS";
	private static final String MUTATION_FORMAT = "%s-%s=%s";

	public static final String TYPE_KEY = "TYPE";

	private final Set<String> discoveredSpecies = new HashSet<>();
	private final Set<String> discoveredMutations = new HashSet<>();
	private final Set<String> researchedMutations = new HashSet<>();
	private String modeName;

	private final GameProfile username;

	protected BreedingTracker(String s, GameProfile username) {
		super(s);
		this.username = username;
	}

	@Override
	public String getModeName() {
		return modeName;
	}

	@Override
	public void setModeName(String name) {
		this.modeName = name;
		markDirty();
	}

	/**
	 * Returns the common tracker
	 *
	 * @param player used to get worldObj
	 * @return common tracker for this breeding system
	 */
	protected abstract IBreedingTracker getBreedingTracker(EntityPlayer player);

	/**
	 * Tag stored in NBT to identify the type of the tracker being synced
	 */
	protected abstract String speciesRootUID();

	@Override
	public void synchToPlayer(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			IBreedingTracker breedingTracker = getBreedingTracker(player);
			String modeName = breedingTracker.getModeName();
			setModeName(modeName);

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			encodeToNBT(nbttagcompound);
			Proxies.net.sendToPlayer(new PacketGenomeTrackerUpdate(nbttagcompound), (EntityPlayerMP) player);
		}
	}

	/* HELPER FUNCTIONS TO PREVENT OBFUSCATION OF INTERFACE METHODS */
	@Override
	public void decodeFromNBT(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	@Override
	public void encodeToNBT(NBTTagCompound nbttagcompound) {
		writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		if (nbttagcompound.hasKey(MODE_NAME_KEY)) {
			modeName = nbttagcompound.getString(MODE_NAME_KEY);
		}

		readValuesFromNBT(nbttagcompound, discoveredSpecies, SPECIES_COUNT_KEY, SPECIES_KEY);
		readValuesFromNBT(nbttagcompound, discoveredMutations, MUTATIONS_COUNT_KEY, MUTATIONS_KEY);
		readValuesFromNBT(nbttagcompound, researchedMutations, RESEARCHED_COUNT_KEY, RESEARCHED_KEY);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		if (modeName != null && !modeName.isEmpty()) {
			nbttagcompound.setString(MODE_NAME_KEY, modeName);
		}

		nbttagcompound.setString(TYPE_KEY, speciesRootUID());

		writeValuesToNBT(nbttagcompound, discoveredSpecies, SPECIES_COUNT_KEY, SPECIES_KEY);
		writeValuesToNBT(nbttagcompound, discoveredMutations, MUTATIONS_COUNT_KEY, MUTATIONS_KEY);
		writeValuesToNBT(nbttagcompound, researchedMutations, RESEARCHED_COUNT_KEY, RESEARCHED_KEY);
	}

	private static void readValuesFromNBT(NBTTagCompound nbttagcompound, Collection<String> values, String countKey, String key) {
		values.clear();
		if (nbttagcompound.hasKey(countKey)) {
			final int count = nbttagcompound.getInteger(countKey);
			for (int i = 0; i < count; i++) {
				String value = nbttagcompound.getString(key + i);
				if (value != null && value.length() > 0) {
					values.add(value);
				}
			}
		}
	}

	private static void writeValuesToNBT(NBTTagCompound nbttagcompound, Collection<String> values, String countKey, String key) {
		final int count = values.size();
		nbttagcompound.setInteger(countKey, count);
		Iterator<String> iterator = values.iterator();
		for (int i = 0; i < count; i++) {
			String value = iterator.next();
			if (value != null && value.length() > 0) {
				nbttagcompound.setString(key + i, value);
			}
		}
	}

	private static String getMutationString(IMutation mutation) {
		String species0 = mutation.getAllele0().getUID();
		String species1 = mutation.getAllele1().getUID();
		String resultSpecies = mutation.getTemplate()[0].getUID();
		return String.format(MUTATION_FORMAT, species0, species1, resultSpecies);
	}

	@Override
	public void registerMutation(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		if (!discoveredMutations.contains(mutationString)) {
			discoveredMutations.add(mutationString);
			markDirty();

			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRootUID());
			ForestryEvent event = new ForestryEvent.MutationDiscovered(speciesRoot, username, mutation, this);
			MinecraftForge.EVENT_BUS.post(event);
		}
	}

	@Override
	public boolean isDiscovered(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		return discoveredMutations.contains(mutationString);
	}

	@Override
	public boolean isDiscovered(IAlleleSpecies species) {
		return discoveredSpecies.contains(species.getUID());
	}

	@Override
	public int getSpeciesBred() {
		return discoveredSpecies.size();
	}

	@Override
	public void registerBirth(IIndividual individual) {
		registerSpecies(individual.getGenome().getPrimary());
		registerSpecies(individual.getGenome().getSecondary());
	}

	@Override
	public void registerSpecies(IAlleleSpecies species) {
		if (!discoveredSpecies.contains(species.getUID())) {
			discoveredSpecies.add(species.getUID());

			ISpeciesRoot speciesRoot = AlleleManager.alleleRegistry.getSpeciesRoot(speciesRootUID());
			ForestryEvent event = new ForestryEvent.SpeciesDiscovered(speciesRoot, username, species, this);
			MinecraftForge.EVENT_BUS.post(event);
		}
	}

	@Override
	public void researchMutation(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		if (!researchedMutations.contains(mutationString)) {
			researchedMutations.add(mutationString);
			markDirty();

			registerMutation(mutation);
		}
	}

	@Override
	public boolean isResearched(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		return researchedMutations.contains(mutationString);
	}
}
