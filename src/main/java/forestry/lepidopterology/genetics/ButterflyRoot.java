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
package forestry.lepidopterology.genetics;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.FMLCommonHandler;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.lepidopterology.ButterflyChromosome;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMode;
import forestry.api.lepidopterology.IButterflyMutation;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.lepidopterology.IButterflyTracker;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.utils.EntityUtil;
import forestry.core.utils.Log;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.plugins.PluginArboriculture;
import forestry.plugins.PluginLepidopterology;

public class ButterflyRoot extends SpeciesRoot<ButterflyChromosome> implements IButterflyRoot {

	private static int butterflySpeciesCount = -1;
	public static final String UID = "rootButterflies";

	public ButterflyRoot() {
		super(ButterflyChromosome.class);
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public Class<IButterfly> getMemberClass() {
		return IButterfly.class;
	}

	@Override
	public int getSpeciesCount() {
		if (butterflySpeciesCount < 0) {
			butterflySpeciesCount = 0;
			for (Entry<String, IAllele> entry : AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet()) {
				if (entry.getValue() instanceof IAlleleButterflySpecies) {
					if (((IAlleleButterflySpecies) entry.getValue()).isCounted()) {
						butterflySpeciesCount++;
					}
				}
			}
		}

		return butterflySpeciesCount;
	}

	@Override
	public boolean isMember(ItemStack stack) {
		return getType(stack) != EnumFlutterType.NONE;
	}

	@Override
	public EnumFlutterType getType(ItemStack stack) {
		if (stack == null) {
			return EnumFlutterType.NONE;
		}

		Item item = stack.getItem();
		if (PluginLepidopterology.items.butterflyGE == item) {
			return EnumFlutterType.BUTTERFLY;
		} else if (PluginLepidopterology.items.serumGE == item) {
			return EnumFlutterType.SERUM;
		} else if (PluginLepidopterology.items.caterpillarGE == item) {
			return EnumFlutterType.CATERPILLAR;
		} else {
			return EnumFlutterType.NONE;
		}
	}

	@Override
	public boolean isMember(ItemStack stack, int type) {
		return getType(stack).ordinal() == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof IButterfly;
	}

	@Override
	public IButterfly getMember(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}
		if (!isMember(stack)) {
			return null;
		}

		return new Butterfly(stack.getTagCompound());
	}

	@Override
	public IButterfly getMember(NBTTagCompound compound) {
		return new Butterfly(compound);
	}

	@Override
	public ItemStack getMemberStack(IIndividual butterfly, int type) {

		Item butterflyItem;
		switch (EnumFlutterType.VALUES[type]) {
			case SERUM:
				butterflyItem = PluginLepidopterology.items.serumGE;
				break;
			case CATERPILLAR:
				butterflyItem = PluginLepidopterology.items.caterpillarGE;
				break;
			case BUTTERFLY:
			default:
				butterflyItem = PluginLepidopterology.items.butterflyGE;
				break;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		butterfly.writeToNBT(nbttagcompound);
		ItemStack stack = new ItemStack(butterflyItem);
		stack.setTagCompound(nbttagcompound);
		return stack;

	}

	@Override
	public EntityButterfly spawnButterflyInWorld(World world, IButterfly butterfly, double x, double y, double z) {
		return EntityUtil.spawnEntity(world, new EntityButterfly(world, butterfly), x, y, z);
	}

	@Override
	public boolean isMated(ItemStack stack) {
		IButterfly butterfly = getMember(stack);
		if (butterfly == null) {
			return false;
		}

		return butterfly.getMate() != null;
	}

	/* GENOME CONVERSIONS */
	@Nonnull
	@Override
	public IButterfly templateAsIndividual(ImmutableMap<ButterflyChromosome, IAllele> template) {
		return new Butterfly(templateAsGenome(template));
	}

	@Nonnull
	@Override
	public IButterfly templateAsIndividual(ImmutableMap<ButterflyChromosome, IAllele> templateActive, ImmutableMap<ButterflyChromosome, IAllele> templateInactive) {
		return new Butterfly(templateAsGenome(templateActive, templateInactive));
	}

	@Override
	public IButterflyGenome templateAsGenome(ImmutableMap<ButterflyChromosome, IAllele> template) {
		return new ButterflyGenome(templateAsChromosomes(template));
	}

	@Override
	public IButterflyGenome templateAsGenome(ImmutableMap<ButterflyChromosome, IAllele> templateActive, ImmutableMap<ButterflyChromosome, IAllele> templateInactive) {
		return new ButterflyGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	@Nonnull
	@Override
	public IButterflyGenome chromosomesAsGenome(ImmutableMap<ButterflyChromosome, IChromosome> chromosomes) {
		return new ButterflyGenome(chromosomes);
	}

	/* TEMPLATES */
	private static final ArrayList<IButterfly> butterflyTemplates = new ArrayList<>();

	@Override
	public ArrayList<IButterfly> getIndividualTemplates() {
		return butterflyTemplates;
	}

	@Override
	public ImmutableMap<ButterflyChromosome, IAllele> getDefaultTemplate() {
		return MothDefinition.Brimstone.getTemplate();
	}

	@Override
	public void registerTemplate(String identifier, ImmutableMap<ButterflyChromosome, IAllele> template) {
		super.registerTemplate(identifier, template);
		butterflyTemplates.add(ButterflyManager.butterflyRoot.templateAsIndividual(template));
	}

	/* MUTATIONS */
	private static final ArrayList<IButterflyMutation> butterflyMutations = new ArrayList<>();

	@Override
	public void registerMutation(IMutation<ButterflyChromosome> mutation) {
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getResultTemplate().get(ButterflyChromosome.SPECIES).getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getSpecies0().getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getSpecies1().getUID())) {
			return;
		}

		butterflyMutations.add((IButterflyMutation) mutation);
	}

	@Override
	public Collection<IButterflyMutation> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(butterflyMutations);
		}
		return butterflyMutations;
	}

	/* BREEDING TRACKER */
	@Nonnull
	@Override
	public IButterflyTracker getBreedingTracker(@Nonnull World world, @Nonnull GameProfile player) {
		String filename = "ButterflyTracker." + (player == null ? "common" : player.getId());
		ButterflyTracker tracker = (ButterflyTracker) world.loadItemData(ButterflyTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ButterflyTracker(filename);
			world.setItemData(filename, tracker);
		}

		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;
	}

	@Nonnull
	@Override
	public ButterflyChromosome[] getKaryotype() {
		return ButterflyChromosome.values();
	}

	@Nonnull
	@Override
	public ButterflyChromosome getKaryotypeKey() {
		return ButterflyChromosome.SPECIES;
	}

	/** Modes */
	private static IButterflyMode activeMode;
	@Nonnull
	private final List<IButterflyMode> modes = new ArrayList<>();

	@Nonnull
	@Override
	public List<IButterflyMode> getModes() {
		return this.modes;
	}

	@Override
	public void resetMode() {
		activeMode = null;
	}

	@Nonnull
	@Override
	public IButterflyMode getMode(@Nonnull World world) {
		if (activeMode != null) {
			return activeMode;
		}

		IButterflyTracker tracker = getBreedingTracker(world, null);
		String mode = tracker.getModeName();
		if (mode == null || mode.isEmpty()) {
			mode = PluginArboriculture.treekeepingMode;
		}

		setMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set Treekeeping mode for a world to " + mode);

		return activeMode;
	}

	@Override
	public void registerMode(@Nonnull IButterflyMode mode) {
		modes.add(mode);
	}

	@Override
	public void setMode(@Nonnull World world, @Nonnull String name) {
		activeMode = getMode(name);
		getBreedingTracker(world, null).setModeName(name);
	}

	@Nonnull
	@Override
	public IButterflyMode getMode(@Nonnull String name) {
		for (IButterflyMode mode : modes) {
			if (mode.getName().equals(name) || mode.getName().equals(name.toLowerCase(Locale.ENGLISH))) {
				return mode;
			}
		}

		Log.error("Failed to find a Butterfly mode called '%s', reverting to fallback.");
		return modes.get(0);
	}
}
