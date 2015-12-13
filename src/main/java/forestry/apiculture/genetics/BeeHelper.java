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
package forestry.apiculture.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.apiculture.BeeHousingListener;
import forestry.apiculture.BeeHousingModifier;
import forestry.apiculture.BeekeepingLogic;
import forestry.core.genetics.SpeciesRoot;
import forestry.plugins.PluginApiculture;

public class BeeHelper extends SpeciesRoot implements IBeeRoot {

	private static int beeSpeciesCount = -1;
	public static final String UID = "rootBees";

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public Class<? extends IIndividual> getMemberClass() {
		return IBee.class;
	}

	@Override
	public int getSpeciesCount() {
		if (beeSpeciesCount < 0) {
			beeSpeciesCount = 0;
			for (Entry<String, IAllele> entry : AlleleManager.alleleRegistry.getRegisteredAlleles().entrySet()) {
				if (entry.getValue() instanceof IAlleleBeeSpecies) {
					if (((IAlleleBeeSpecies) entry.getValue()).isCounted()) {
						beeSpeciesCount++;
					}
				}
			}
		}

		return beeSpeciesCount;
	}

	@Override
	public boolean isMember(ItemStack stack) {
		return getType(stack) != EnumBeeType.NONE;
	}

	@Override
	public boolean isMember(ItemStack stack, int type) {
		return getType(stack).ordinal() == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof IBee;
	}

	@Override
	public ItemStack getMemberStack(IIndividual individual, int type) {
		if (!isMember(individual)) {
			return null;
		}
		IBee bee = (IBee) individual;

		Item beeItem;
		switch (EnumBeeType.VALUES[type]) {
			case QUEEN:
				beeItem = PluginApiculture.items.beeQueenGE;
				// ensure a queen is always mated
				if (bee.getMate() == null) {
					bee.mate(bee);
				}
				break;
			case PRINCESS:
				beeItem = PluginApiculture.items.beePrincessGE;
				break;
			case DRONE:
				beeItem = PluginApiculture.items.beeDroneGE;
				break;
			case LARVAE:
				beeItem = PluginApiculture.items.beeLarvaeGE;
				break;
			default:
				throw new RuntimeException("Cannot instantiate a bee of type " + type);
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		bee.writeToNBT(nbttagcompound);
		ItemStack beeStack = new ItemStack(beeItem);
		beeStack.setTagCompound(nbttagcompound);
		return beeStack;
	}

	@Override
	public EnumBeeType getType(ItemStack stack) {
		if (stack == null) {
			return EnumBeeType.NONE;
		}

		Item item = stack.getItem();

		if (PluginApiculture.items.beeDroneGE == item) {
			return EnumBeeType.DRONE;
		} else if (PluginApiculture.items.beePrincessGE == item) {
			return EnumBeeType.PRINCESS;
		} else if (PluginApiculture.items.beeQueenGE == item) {
			return EnumBeeType.QUEEN;
		} else if (PluginApiculture.items.beeLarvaeGE == item) {
			return EnumBeeType.LARVAE;
		}

		return EnumBeeType.NONE;
	}

	@Override
	public boolean isDrone(ItemStack stack) {
		return getType(stack) == EnumBeeType.DRONE;
	}

	@Override
	public boolean isMated(ItemStack stack) {
		if (getType(stack) != EnumBeeType.QUEEN) {
			return false;
		}

		NBTTagCompound nbt = stack.getTagCompound();
		return nbt != null && nbt.hasKey("Mate");
	}

	@Override
	public IBee getMember(ItemStack stack) {
		if (!isMember(stack)) {
			return null;
		}

		return new Bee(stack.getTagCompound());
	}

	@Override
	public IBee getMember(NBTTagCompound compound) {
		return new Bee(compound);
	}

	@Override
	public IBee getBee(World world, IBeeGenome genome) {
		return new Bee(genome);
	}

	@Override
	public IBee getBee(World world, IBeeGenome genome, IBee mate) {
		return new Bee(genome, mate);
	}

	/* GENOME CONVERSIONS */
	@Override
	public IBeeGenome templateAsGenome(IAllele[] template) {
		IChromosome[] chromosomes = templateAsChromosomes(template);
		return new BeeGenome(chromosomes);
	}

	@Override
	public IBeeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new BeeGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	@Override
	public IBee templateAsIndividual(IAllele[] template) {
		return new Bee(templateAsGenome(template));
	}

	@Override
	public IBee templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive) {
		return new Bee(templateAsGenome(templateActive, templateInactive));
	}

	/* TEMPLATES */
	private static final ArrayList<IBee> beeTemplates = new ArrayList<>();

	@Override
	public ArrayList<IBee> getIndividualTemplates() {
		return beeTemplates;
	}

	@Override
	public void registerTemplate(String identifier, IAllele[] template) {
		IBeeGenome beeGenome = BeeManager.beeRoot.templateAsGenome(template);
		IBee bee = new Bee(beeGenome);
		beeTemplates.add(bee);
		speciesTemplates.put(identifier, template);
	}

	@Override
	public IAllele[] getDefaultTemplate() {
		return BeeDefinition.FOREST.getTemplate();
	}

	/* MUTATIONS */
	/**
	 * List of possible mutations on species alleles.
	 */
	private static final ArrayList<IBeeMutation> beeMutations = new ArrayList<>();

	@Override
	public Collection<IBeeMutation> getMutations(boolean shuffle) {
		if (shuffle) {
			Collections.shuffle(beeMutations);
		}
		return beeMutations;
	}

	@Override
	public void registerMutation(IMutation mutation) {
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getTemplate()[0].getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele0().getUID())) {
			return;
		}
		if (AlleleManager.alleleRegistry.isBlacklisted(mutation.getAllele1().getUID())) {
			return;
		}

		beeMutations.add((IBeeMutation) mutation);
	}

	/* BREEDING MODES */
	private final ArrayList<IBeekeepingMode> beekeepingModes = new ArrayList<>();
	private static IBeekeepingMode activeBeekeepingMode;

	@Override
	public void resetBeekeepingMode() {
		activeBeekeepingMode = null;
	}

	@Override
	public ArrayList<IBeekeepingMode> getBeekeepingModes() {
		return this.beekeepingModes;
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(World world) {
		if (activeBeekeepingMode != null) {
			return activeBeekeepingMode;
		}

		// No beekeeping mode yet, get it.
		IApiaristTracker tracker = getBreedingTracker(world, null);
		String mode = tracker.getModeName();
		if (mode == null || mode.isEmpty()) {
			mode = PluginApiculture.beekeepingMode;
		}

		setBeekeepingMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set beekeeping mode for a world to " + mode);

		return activeBeekeepingMode;
	}

	@Override
	public void registerBeekeepingMode(IBeekeepingMode mode) {
		beekeepingModes.add(mode);
	}

	@Override
	public void setBeekeepingMode(World world, String name) {
		activeBeekeepingMode = getBeekeepingMode(name);
		getBreedingTracker(world, null).setModeName(name);
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(String name) {
		for (IBeekeepingMode mode : beekeepingModes) {
			if (mode.getName().equals(name) || mode.getName().equals(name.toLowerCase(Locale.ENGLISH))) {
				return mode;
			}
		}

		FMLCommonHandler.instance().getFMLLogger().debug("Failed to find a beekeeping mode called '%s', reverting to fallback.");
		return beekeepingModes.get(0);
	}

	@Override
	public IApiaristTracker getBreedingTracker(World world, GameProfile player) {
		String filename = "ApiaristTracker." + (player == null ? "common" : player.getId());
		ApiaristTracker tracker = (ApiaristTracker) world.loadItemData(ApiaristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ApiaristTracker(filename);
			world.setItemData(filename, tracker);
		}

		tracker.setUsername(player);
		tracker.setWorld(world);

		return tracker;
	}

	@Override
	public IBeekeepingLogic createBeekeepingLogic(IBeeHousing housing) {
		return new BeekeepingLogic(housing);
	}

	@Override
	public IBeeModifier createBeeHousingModifier(IBeeHousing housing) {
		return new BeeHousingModifier(housing);
	}

	@Override
	public IBeeListener createBeeHousingListener(IBeeHousing housing) {
		return new BeeHousingListener(housing);
	}

	@Override
	public IChromosomeType[] getKaryotype() {
		return EnumBeeChromosome.values();
	}

	@Override
	public IChromosomeType getKaryotypeKey() {
		return EnumBeeChromosome.SPECIES;
	}
}
