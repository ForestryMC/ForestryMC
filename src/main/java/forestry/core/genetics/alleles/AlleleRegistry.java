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
package forestry.core.genetics.alleles;

import com.google.common.collect.HashMultimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleHandler;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Classification;
import forestry.core.genetics.ItemResearchNote.EnumNoteType;
import forestry.plugins.PluginCore;

public class AlleleRegistry implements IAlleleRegistry {

	private static final int ALLELE_ARRAY_SIZE = 2048;

	/* SPECIES ROOT */
	private final LinkedHashMap<String, ISpeciesRoot> rootMap = new LinkedHashMap<>(16);

	@Override
	public void registerSpeciesRoot(ISpeciesRoot root) {
		rootMap.put(root.getUID(), root);
	}

	@Override
	public Map<String, ISpeciesRoot> getSpeciesRoot() {
		return Collections.unmodifiableMap(rootMap);
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(String uid) {
		if (rootMap.containsKey(uid)) {
			return rootMap.get(uid);
		}
		return null;
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(ItemStack stack) {
		if (stack == null) {
			return null;
		}

		for (ISpeciesRoot root : rootMap.values()) {
			if (root.isMember(stack)) {
				return root;
			}
		}
		return null;
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(Class<? extends IIndividual> clz) {
		for (ISpeciesRoot root : rootMap.values()) {
			if (root.getMemberClass().isAssignableFrom(clz)) {
				return root;
			}
		}
		return null;
	}

	/* INDIVIDUALS */
	@Override
	public boolean isIndividual(ItemStack stack) {
		if (stack == null) {
			return false;
		}
		return getSpeciesRoot(stack) != null;
	}

	@Override
	public IIndividual getIndividual(ItemStack stack) {
		ISpeciesRoot root = getSpeciesRoot(stack);
		if (root == null) {
			return null;
		}

		return root.getMember(stack);
	}

	/* ALLELES */
	private final LinkedHashMap<String, IAllele> alleleMap = new LinkedHashMap<>(ALLELE_ARRAY_SIZE);
	private final HashMultimap<IChromosomeType, IAllele> allelesByType = HashMultimap.create();
	private final HashMultimap<IAllele, IChromosomeType> typesByAllele = HashMultimap.create();
	private final LinkedHashMap<String, IAllele> deprecatedAlleleMap = new LinkedHashMap<>(32);
	private final LinkedHashMap<String, IClassification> classificationMap = new LinkedHashMap<>(128);
	private final LinkedHashMap<String, IFruitFamily> fruitMap = new LinkedHashMap<>(64);

	/*
	 * Internal HashSet of all alleleHandlers, which trigger when an allele or branch is registered
	 */
	private final HashSet<IAlleleHandler> alleleHandlers = new HashSet<>();

	public void initialize() {

		createAndRegisterClassification(EnumClassLevel.DOMAIN, "archaea", "Archaea");
		createAndRegisterClassification(EnumClassLevel.DOMAIN, "bacteria", "Bacteria");
		IClassification eukarya = createAndRegisterClassification(EnumClassLevel.DOMAIN, "eukarya", "Eukarya");

		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "animalia", "Animalia"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "plantae", "Plantae"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "fungi", "Fungi"));
		eukarya.addMemberGroup(createAndRegisterClassification(EnumClassLevel.KINGDOM, "protista", "Protista"));

		getClassification("kingdom.animalia").addMemberGroup(createAndRegisterClassification(EnumClassLevel.PHYLUM, "arthropoda", "Arthropoda"));

		// Animalia
		getClassification("phylum.arthropoda").addMemberGroup(createAndRegisterClassification(EnumClassLevel.CLASS, "insecta", "Insecta"));

	}

	@Override
	public Map<String, IAllele> getRegisteredAlleles() {
		return Collections.unmodifiableMap(alleleMap);
	}

	@Override
	public Map<String, IAllele> getDeprecatedAlleleReplacements() {
		return Collections.unmodifiableMap(deprecatedAlleleMap);
	}

	@Override
	public void registerAllele(IAllele allele) {
		alleleMap.put(allele.getUID(), allele);
		if (allele instanceof IAlleleSpecies) {
			IClassification branch = ((IAlleleSpecies) allele).getBranch();
			if (branch != null) {
				branch.addMemberSpecies((IAlleleSpecies) allele);
			}
		}
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterAllele(allele);
		}
	}

	@Override
	public void registerAllele(IAllele allele, IChromosomeType... chromosomeTypes) {
		for (IChromosomeType chromosomeType : chromosomeTypes) {
			if (!chromosomeType.getAlleleClass().isAssignableFrom(allele.getClass())) {
				throw new IllegalArgumentException("Allele class (" + allele.getClass() + ") does not match chromosome type (" + chromosomeType.getAlleleClass() + ").");
			}
			allelesByType.put(chromosomeType, allele);
			typesByAllele.put(allele, chromosomeType);
		}
		registerAllele(allele);
	}

	@Override
	public void registerDeprecatedAlleleReplacement(String deprecatedUID, IAllele replacementAllele) {
		if (deprecatedAlleleMap.containsKey(deprecatedUID)) {
			return;
		}

		deprecatedAlleleMap.put(deprecatedUID, replacementAllele);
	}

	@Override
	public IAllele getAllele(String uid) {
		IAllele allele = alleleMap.get(uid);

		if (allele == null) {
			allele = deprecatedAlleleMap.get(uid);
		}

		return allele;
	}

	@Override
	public Collection<IAllele> getRegisteredAlleles(IChromosomeType type) {
		return Collections.unmodifiableSet(allelesByType.get(type));
	}

	// This method is not useful until all mod addon alleles are registered with their valid IChromosomeTypes
	public Collection<IChromosomeType> getChromosomeTypes(IAllele allele) {
		return Collections.unmodifiableSet(typesByAllele.get(allele));
	}

	/* CLASSIFICATIONS */
	@Override
	public void registerClassification(IClassification branch) {

		if (classificationMap.containsKey(branch.getUID())) {
			throw new RuntimeException(String.format("Could not add new classification '%s', because the key is already taken by %s.", branch.getUID(),
					classificationMap.get(branch.getUID())));
		}

		classificationMap.put(branch.getUID(), branch);
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterClassification(branch);
		}
	}

	@Override
	public Map<String, IClassification> getRegisteredClassifications() {
		return Collections.unmodifiableMap(classificationMap);
	}

	@Override
	public IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific) {
		return new Classification(level, uid, scientific);
	}

	@Override
	public IClassification createAndRegisterClassification(EnumClassLevel level, String uid, String scientific, IClassification... members) {
		IClassification classification = new Classification(level, uid, scientific);
		for (IClassification member : members) {
			classification.addMemberGroup(member);
		}
		return classification;
	}

	@Override
	public IClassification getClassification(String uid) {
		return classificationMap.get(uid);
	}

	/* FRUIT FAMILIES */
	@Override
	public void registerFruitFamily(IFruitFamily family) {
		fruitMap.put(family.getUID(), family);
		for (IAlleleHandler handler : this.alleleHandlers) {
			handler.onRegisterFruitFamily(family);
		}
	}

	@Override
	public Map<String, IFruitFamily> getRegisteredFruitFamilies() {
		return Collections.unmodifiableMap(fruitMap);
	}

	@Override
	public IFruitFamily getFruitFamily(String uid) {
		return fruitMap.get(uid);
	}

	/* ALLELE HANDLERS */
	@Override
	public void registerAlleleHandler(IAlleleHandler handler) {
		this.alleleHandlers.add(handler);
	}

	/* BLACKLIST */
	private final ArrayList<String> blacklist = new ArrayList<>();

	@Override
	public void blacklistAllele(String uid) {
		blacklist.add(uid);
	}

	@Override
	public Collection<String> getAlleleBlacklist() {
		return Collections.unmodifiableCollection(blacklist);
	}

	@Override
	public boolean isBlacklisted(String uid) {
		return blacklist.contains(uid);
	}

	/* RESEARCH */
	@Override
	public ItemStack getSpeciesNoteStack(GameProfile researcher, IAlleleSpecies species) {
		return EnumNoteType.createSpeciesNoteStack(PluginCore.items.researchNote, researcher, species);
	}

	@Override
	public ItemStack getMutationNoteStack(GameProfile researcher, IMutation mutation) {
		return EnumNoteType.createMutationNoteStack(PluginCore.items.researchNote, researcher, mutation);
	}
}
