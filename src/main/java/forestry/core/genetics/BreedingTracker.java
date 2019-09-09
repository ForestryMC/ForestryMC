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
import java.util.Optional;
import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import genetics.api.GeneticsAPI;
import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleSpecies;
import genetics.api.individual.IIndividual;
import genetics.api.mutation.IMutation;
import genetics.api.root.IRootDefinition;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.IBreedingTracker;
import forestry.core.advancements.SpeciesDiscoveredTrigger;
import forestry.core.network.packets.PacketGenomeTrackerSync;
import forestry.core.utils.NetworkUtil;

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

	protected BreedingTracker(String s, String defaultModeName) {
		super(s);
		this.modeName = defaultModeName;
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
	 * @param player used to getComb world
	 * @return common tracker for this breeding system
	 */
	protected abstract IBreedingTracker getBreedingTracker(PlayerEntity player);

	/**
	 * Tag stored in NBT to identify the type of the tracker being synced
	 */
	protected abstract String speciesRootUID();

	@Override
	public void synchToPlayer(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
			IBreedingTracker breedingTracker = getBreedingTracker(player);
			String modeName = breedingTracker.getModeName();
			setModeName(modeName);

			CompoundNBT CompoundNBT = new CompoundNBT();
			encodeToNBT(CompoundNBT);
			PacketGenomeTrackerSync packet = new PacketGenomeTrackerSync(CompoundNBT);
			NetworkUtil.sendToPlayer(packet, player);
		}
	}

	private void syncToPlayer(Collection<String> discoveredSpecies, Collection<String> discoveredMutations, Collection<String> researchedMutations) {
		if (world != null && username != null && username.getName() != null) {
			PlayerEntity player = world.getPlayerByUuid(username.getId());
			if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
				IBreedingTracker breedingTracker = getBreedingTracker(player);
				String modeName = breedingTracker.getModeName();
				setModeName(modeName);

				CompoundNBT compound = new CompoundNBT();
				writeToNBT(compound, discoveredSpecies, discoveredMutations, researchedMutations);
				PacketGenomeTrackerSync packet = new PacketGenomeTrackerSync(compound);
				NetworkUtil.sendToPlayer(packet, player);

				for (String species : discoveredSpecies) {
					Optional<IAllele> optionalAllele = GeneticsAPI.apiInstance.getAlleleRegistry().getAllele(species);
					optionalAllele.ifPresent(allele -> SpeciesDiscoveredTrigger.INSTANCE.trigger((ServerPlayerEntity) player, allele));
				}
			}
		}
	}

	/* HELPER FUNCTIONS TO PREVENT OBFUSCATION OF INTERFACE METHODS */
	@Override
	public void decodeFromNBT(CompoundNBT compound) {
		read(compound);
	}

	@Override
	public void encodeToNBT(CompoundNBT compound) {
		write(compound);
	}

	@Override
	public void read(CompoundNBT CompoundNBT) {

		if (CompoundNBT.contains(MODE_NAME_KEY)) {
			modeName = CompoundNBT.getString(MODE_NAME_KEY);
		}

		readValuesFromNBT(CompoundNBT, discoveredSpecies, SPECIES_COUNT_KEY, SPECIES_KEY);
		readValuesFromNBT(CompoundNBT, discoveredMutations, MUTATIONS_COUNT_KEY, MUTATIONS_KEY);
		readValuesFromNBT(CompoundNBT, researchedMutations, RESEARCHED_COUNT_KEY, RESEARCHED_KEY);
	}


	@Override
	public CompoundNBT write(CompoundNBT CompoundNBT) {
		writeToNBT(CompoundNBT, discoveredSpecies, discoveredMutations, researchedMutations);
		return CompoundNBT;
	}

	private void writeToNBT(CompoundNBT CompoundNBT, Collection<String> discoveredSpecies, Collection<String> discoveredMutations, Collection<String> researchedMutations) {
		if (modeName != null && !modeName.isEmpty()) {
			CompoundNBT.putString(MODE_NAME_KEY, modeName);
		}

		CompoundNBT.putString(TYPE_KEY, speciesRootUID());

		writeValuesToNBT(CompoundNBT, discoveredSpecies, SPECIES_COUNT_KEY, SPECIES_KEY);
		writeValuesToNBT(CompoundNBT, discoveredMutations, MUTATIONS_COUNT_KEY, MUTATIONS_KEY);
		writeValuesToNBT(CompoundNBT, researchedMutations, RESEARCHED_COUNT_KEY, RESEARCHED_KEY);
	}

	private static void readValuesFromNBT(CompoundNBT CompoundNBT, Set<String> values, String countKey, String key) {
		if (CompoundNBT.contains(countKey)) {
			final int count = CompoundNBT.getInt(countKey);
			for (int i = 0; i < count; i++) {
				if (CompoundNBT.contains(key + i)) {
					String value = CompoundNBT.getString(key + i);
					if (!value.isEmpty()) {
						values.add(value);
					}
				}
			}
		}
	}

	private static void writeValuesToNBT(CompoundNBT CompoundNBT, Collection<String> values, String countKey, String key) {
		final int count = values.size();
		CompoundNBT.putInt(countKey, count);
		Iterator<String> iterator = values.iterator();
		for (int i = 0; i < count; i++) {
			String value = iterator.next();
			if (value != null && !value.isEmpty()) {
				CompoundNBT.putString(key + i, value);
			}
		}
	}

	private static String getMutationString(IMutation mutation) {
		String species0 = mutation.getFirstParent().getRegistryName().toString();
		String species1 = mutation.getSecondParent().getRegistryName().toString();
		String resultSpecies = mutation.getResultingSpecies().getRegistryName().toString();
		return String.format(MUTATION_FORMAT, species0, species1, resultSpecies);
	}

	@Override
	public void registerMutation(IMutation mutation) {
		String mutationString = getMutationString(mutation);
		if (!discoveredMutations.contains(mutationString)) {
			discoveredMutations.add(mutationString);
			markDirty();

			IRootDefinition speciesRoot = GeneticsAPI.apiInstance.getRoot(speciesRootUID());
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
		return discoveredSpecies.contains(species.getRegistryName().toString());
	}

	@Override
	public Set<String> getDiscoveredSpecies() {
		return discoveredSpecies;
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
		String registryName = species.getRegistryName().toString();
		if (!discoveredSpecies.contains(registryName)) {
			discoveredSpecies.add(registryName);

			IRootDefinition speciesRoot = GeneticsAPI.apiInstance.getRoot(speciesRootUID());
			ForestryEvent event = new ForestryEvent.SpeciesDiscovered(speciesRoot, username, species, this);
			MinecraftForge.EVENT_BUS.post(event);

			syncToPlayer(Collections.singleton(registryName), emptyStringCollection, emptyStringCollection);
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
