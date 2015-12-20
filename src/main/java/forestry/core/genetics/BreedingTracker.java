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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.network.packets.PacketGenomeTrackerSync;
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
	private static final Collection<String> emptyStringCollection = Collections.emptyList();

	public static final String TYPE_KEY = "TYPE";

	private final Set<String> discoveredSpecies = new HashSet<>();
	private final Set<String> discoveredMutations = new HashSet<>();
	private final Set<String> researchedMutations = new HashSet<>();
	private String modeName;

	@Nullable
	private GameProfile username;
	@Nullable
	private World world;

	protected BreedingTracker(String s) {
		super(s);
	}

	public void setUsername(@Nullable GameProfile username) {
		this.username = username;
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
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
		if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
			IBreedingTracker breedingTracker = getBreedingTracker(player);
			String modeName = breedingTracker.getModeName();
			setModeName(modeName);

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			encodeToNBT(nbttagcompound);
			PacketGenomeTrackerSync packet = new PacketGenomeTrackerSync(nbttagcompound);
			Proxies.net.sendToPlayer(packet, player);
		}
	}

	private void syncToPlayer(Collection<String> discoveredSpecies, Collection<String> discoveredMutations, Collection<String> researchedMutations) {
		if (world != null && username != null && username.getName() != null) {
			EntityPlayer player = world.getPlayerEntityByName(username.getName());
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer)) {
				IBreedingTracker breedingTracker = getBreedingTracker(player);
				String modeName = breedingTracker.getModeName();
				setModeName(modeName);

				NBTTagCompound nbtTagCompound = new NBTTagCompound();
				writeToNBT(nbtTagCompound, discoveredSpecies, discoveredMutations, researchedMutations);
				PacketGenomeTrackerSync packet = new PacketGenomeTrackerSync(nbtTagCompound);
				Proxies.net.sendToPlayer(packet, player);
			}
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
		writeToNBT(nbttagcompound, discoveredSpecies, discoveredMutations, researchedMutations);
	}

	private void writeToNBT(NBTTagCompound nbtTagCompound, Collection<String> discoveredSpecies, Collection<String> discoveredMutations, Collection<String> researchedMutations) {
		if (modeName != null && !modeName.isEmpty()) {
			nbtTagCompound.setString(MODE_NAME_KEY, modeName);
		}

		nbtTagCompound.setString(TYPE_KEY, speciesRootUID());

		writeValuesToNBT(nbtTagCompound, discoveredSpecies, SPECIES_COUNT_KEY, SPECIES_KEY);
		writeValuesToNBT(nbtTagCompound, discoveredMutations, MUTATIONS_COUNT_KEY, MUTATIONS_KEY);
		writeValuesToNBT(nbtTagCompound, researchedMutations, RESEARCHED_COUNT_KEY, RESEARCHED_KEY);
	}

	private static void readValuesFromNBT(NBTTagCompound nbttagcompound, Set<String> values, String countKey, String key) {
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

			syncToPlayer(emptyStringCollection, Collections.singleton(mutationString), emptyStringCollection);
		}
	}

	@Override
	public boolean isDiscovered(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		return discoveredMutations.contains(mutationString) || researchedMutations.contains(mutationString);
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

			syncToPlayer(Collections.singleton(species.getUID()), emptyStringCollection, emptyStringCollection);
		}
	}

	@Override
	public void researchMutation(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		if (!researchedMutations.contains(mutationString)) {
			researchedMutations.add(mutationString);
			markDirty();

			registerMutation(mutation);

			syncToPlayer(emptyStringCollection, emptyStringCollection, Collections.singleton(mutationString));
		}
	}

	@Override
	public boolean isResearched(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		return researchedMutations.contains(mutationString);
	}
}
