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

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import forestry.api.genetics.IAlyzerPlugin;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IDatabasePlugin;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesType;
import forestry.apiculture.BeeHousingListener;
import forestry.apiculture.BeeHousingModifier;
import forestry.apiculture.BeekeepingLogic;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.ItemRegistryApiculture;
import forestry.core.genetics.SpeciesRoot;

public class BeeRoot extends SpeciesRoot implements IBeeRoot {

	private static int beeSpeciesCount = -1;
	private static final List<IBee> beeTemplates = new ArrayList<>();
	/**
	 * List of possible mutations on species alleles.
	 */
	private static final List<IBeeMutation> beeMutations = new ArrayList<>();
	public static final String UID = "rootBees";

	private final List<IBeekeepingMode> beekeepingModes = new ArrayList<>();

	@Nullable
	private static IBeekeepingMode activeBeekeepingMode;

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
		return getType(stack) != null;
	}

	@Override
	public boolean isMember(ItemStack stack, ISpeciesType type) {
		return getType(stack) == type;
	}

	@Override
	public boolean isMember(IIndividual individual) {
		return individual instanceof IBee;
	}

	@Override
	public ItemStack getMemberStack(IIndividual individual, ISpeciesType type) {
		Preconditions.checkArgument(individual instanceof IBee, "individual is not a bee");
		Preconditions.checkArgument(type instanceof EnumBeeType, "type is not an EnumBeeType");
		ItemRegistryApiculture apicultureItems = ModuleApiculture.getItems();

		IBee bee = (IBee) individual;
		Item beeItem;
		switch ((EnumBeeType) type) {
			case QUEEN:
				beeItem = apicultureItems.beeQueenGE;
				// ensure a queen is always mated
				if (bee.getMate() == null) {
					bee.mate(bee);
				}
				break;
			case PRINCESS:
				beeItem = apicultureItems.beePrincessGE;
				break;
			case DRONE:
				beeItem = apicultureItems.beeDroneGE;
				break;
			case LARVAE:
				beeItem = apicultureItems.beeLarvaeGE;
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

	@Nullable
	@Override
	public EnumBeeType getType(ItemStack stack) {
		Item item = stack.getItem();
		ItemRegistryApiculture apicultureItems = ModuleApiculture.getItems();

		if (apicultureItems.beeDroneGE == item) {
			return EnumBeeType.DRONE;
		} else if (apicultureItems.beePrincessGE == item) {
			return EnumBeeType.PRINCESS;
		} else if (apicultureItems.beeQueenGE == item) {
			return EnumBeeType.QUEEN;
		} else if (apicultureItems.beeLarvaeGE == item) {
			return EnumBeeType.LARVAE;
		}

		return null;
	}

	@Override
	public EnumBeeType getIconType() {
		return EnumBeeType.DRONE;
	}

	@Override
	public ISpeciesType[] getTypes() {
		return EnumBeeType.values();
	}

	@Override
	public ISpeciesType getTypeForMutation(int position) {
		switch (position){
			case 0:
				return EnumBeeType.PRINCESS;
			case 1:
				return EnumBeeType.DRONE;
			case 2:
				return EnumBeeType.QUEEN;
		}
		return getIconType();
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
	@Nullable
	public IBee getMember(ItemStack stack) {
		if (!isMember(stack) || stack.getTagCompound() == null) {
			return null;
		}

		return new Bee(stack.getTagCompound());
	}

	@Override
	public IBee getMember(NBTTagCompound compound) {
		return new Bee(compound);
	}

	@Override
	public IBee getBee(IBeeGenome genome) {
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
	@Override
	public List<IBee> getIndividualTemplates() {
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
	@Override
	public List<IBeeMutation> getMutations(boolean shuffle) {
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
	@Override
	public void resetBeekeepingMode() {
		activeBeekeepingMode = null;
	}

	@Override
	public List<IBeekeepingMode> getBeekeepingModes() {
		return this.beekeepingModes;
	}

	@Override
	public IBeekeepingMode getBeekeepingMode(World world) {
		if (activeBeekeepingMode != null) {
			return activeBeekeepingMode;
		}

		// No beekeeping mode yet, get it.
		IApiaristTracker tracker = getBreedingTracker(world, null);
		String modeName = tracker.getModeName();
		IBeekeepingMode mode = getBeekeepingMode(modeName);
		Preconditions.checkNotNull(mode);

		setBeekeepingMode(world, mode);
		FMLCommonHandler.instance().getFMLLogger().debug("Set beekeeping mode for a world to " + mode);

		return activeBeekeepingMode;
	}

	@Override
	public void registerBeekeepingMode(IBeekeepingMode mode) {
		beekeepingModes.add(mode);
	}

	@Override
	public void setBeekeepingMode(World world, IBeekeepingMode mode) {
		Preconditions.checkNotNull(world);
		Preconditions.checkNotNull(mode);
		activeBeekeepingMode = mode;
		getBreedingTracker(world, null).setModeName(mode.getName());
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
	public IApiaristTracker getBreedingTracker(World world, @Nullable GameProfile player) {
		String filename = "ApiaristTracker." + (player == null ? "common" : player.getId());
		ApiaristTracker tracker = (ApiaristTracker) world.loadData(ApiaristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new ApiaristTracker(filename);
			world.setData(filename, tracker);
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
	public IChromosomeType getSpeciesChromosomeType() {
		return EnumBeeChromosome.SPECIES;
	}

	@Override
	public IAlyzerPlugin getAlyzerPlugin() {
		return BeeAlyzerPlugin.INSTANCE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IDatabasePlugin getSpeciesPlugin() {
		return BeePlugin.INSTANCE;
	}
}
