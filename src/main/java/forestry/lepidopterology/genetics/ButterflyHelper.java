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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMutation;
import forestry.api.lepidopterology.IButterflyRoot;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.SpeciesRoot;
import forestry.core.utils.Utils;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.plugins.PluginLepidopterology;

public class ButterflyHelper extends SpeciesRoot implements IButterflyRoot {

	public static int butterflySpeciesCount = -1;
	public static final String UID = "rootButterflies";

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public Class<? extends IIndividual> getMemberClass() {
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

		if (ForestryItem.butterflyGE.isItemEqual(stack)) {
			return EnumFlutterType.BUTTERFLY;
		} else if (ForestryItem.serumGE.isItemEqual(stack)) {
			return EnumFlutterType.SERUM;
		} else if (ForestryItem.caterpillarGE.isItemEqual(stack)) {
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
				butterflyItem = ForestryItem.serumGE.item();
				break;
			case CATERPILLAR:
				butterflyItem = ForestryItem.caterpillarGE.item();
				break;
			case BUTTERFLY:
			default:
				butterflyItem = ForestryItem.butterflyGE.item();
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
		return Utils.spawnEntity(world, new EntityButterfly(world, butterfly), x, y, z);
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
	@Override
	public IButterfly templateAsIndividual(IAllele[] template) {
		return new Butterfly(templateAsGenome(template));
	}

	@Override
	public IButterfly templateAsIndividual(IAllele[] templateActive, IAllele[] templateInactive) {
		return new Butterfly(templateAsGenome(templateActive, templateInactive));
	}

	@Override
	public IButterflyGenome templateAsGenome(IAllele[] template) {
		return new ButterflyGenome(templateAsChromosomes(template));
	}

	@Override
	public IButterflyGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new ButterflyGenome(templateAsChromosomes(templateActive, templateInactive));
	}

	/* TEMPLATES */
	public static final ArrayList<IButterfly> butterflyTemplates = new ArrayList<IButterfly>();

	@Override
	public ArrayList<IButterfly> getIndividualTemplates() {
		return butterflyTemplates;
	}

	@Override
	public IAllele[] getDefaultTemplate() {
		return ButterflyTemplates.getDefaultTemplate();
	}

	@Override
	public void registerTemplate(String identifier, IAllele[] template) {
		butterflyTemplates.add(PluginLepidopterology.butterflyInterface.templateAsIndividual(template));
		speciesTemplates.put(identifier, template);
	}

	/* MUTATIONS */
	private static final ArrayList<IButterflyMutation> butterflyMutations = new ArrayList<IButterflyMutation>();

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
	@Override
	public ILepidopteristTracker getBreedingTracker(World world, GameProfile player) {
		String filename = "LepidopteristTracker." + (player == null ? "common" : player.getId());
		LepidopteristTracker tracker = (LepidopteristTracker) world.loadItemData(LepidopteristTracker.class, filename);

		// Create a tracker if there is none yet.
		if (tracker == null) {
			tracker = new LepidopteristTracker(filename, player);
			world.setItemData(filename, tracker);
		}

		return tracker;
	}

	@Override
	public IChromosomeType[] getKaryotype() {
		return EnumButterflyChromosome.values();
	}

	@Override
	public IChromosomeType getKaryotypeKey() {
		return EnumButterflyChromosome.SPECIES;
	}

}
