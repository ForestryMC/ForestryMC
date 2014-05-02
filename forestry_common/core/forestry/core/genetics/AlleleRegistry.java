/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleHandler;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ILegacyHandler;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.ItemResearchNote.EnumNoteType;
import forestry.core.utils.IDAllocator;

public class AlleleRegistry implements IAlleleRegistry, ILegacyHandler {

	public static final int ALLELE_ARRAY_SIZE = 2048;

	/* SPECIES ROOT */
	private LinkedHashMap<String, ISpeciesRoot> rootMap = new LinkedHashMap<String, ISpeciesRoot>(16);

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
		if(rootMap.containsKey(uid))
			return rootMap.get(uid);
		return null;
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(ItemStack stack) {
		for(ISpeciesRoot root : rootMap.values())
			if(root.isMember(stack))
				return root;
		return null;
	}

	@Override
	public ISpeciesRoot getSpeciesRoot(Class<? extends IIndividual> clz) {
		for(ISpeciesRoot root : rootMap.values())
			if(root.getMemberClass().isAssignableFrom(clz))
				return root;
		return null;
	}

	/* INDIVIDUALS */
	public boolean isIndividual(ItemStack stack) {
		return getSpeciesRoot(stack) != null;
	}

	public IIndividual getIndividual(ItemStack stack) {
		ISpeciesRoot root = getSpeciesRoot(stack);
		if(root == null)
			return null;

		return root.getMember(stack);
	}

	/* ALLELES */
	private LinkedHashMap<String, IAllele> alleleMap = new LinkedHashMap<String, IAllele>(ALLELE_ARRAY_SIZE);
	private LinkedHashMap<String, IAllele> deprecatedAlleleMap = new LinkedHashMap<String, IAllele>(32);
	private LinkedHashMap<String, IClassification> classificationMap = new LinkedHashMap<String, IClassification>(128);
	private LinkedHashMap<String, IFruitFamily> fruitMap = new LinkedHashMap<String, IFruitFamily>(64);

	private HashMap<Integer, String> metaMapToUID = new HashMap<Integer, String>();
	private HashMap<String, Integer> uidMapToMeta = new HashMap<String, Integer>();

	private HashMap<Integer, String> legacyMap = new HashMap<Integer, String>();

	/*
	 * Internal HashSet of all alleleHandlers, which trigger when an allele or branch is registered
	 */
	private HashSet<IAlleleHandler> alleleHandlers = new HashSet<IAlleleHandler>();

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
			if (branch != null)
				branch.addMemberSpecies((IAlleleSpecies) allele);
		}
		for (IAlleleHandler handler : this.alleleHandlers)
			handler.onRegisterAllele(allele);
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
	public void reloadMetaMap(World world) {
		metaMapToUID.clear();
		uidMapToMeta.clear();

		Iterator<Entry<String, IAllele>> it = alleleMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, IAllele> entry = it.next();
			if (!(entry.getValue() instanceof IAlleleSpecies))
				continue;

			int meta = IDAllocator.getIDAllocator(world, "speciesMetaMap").getId(entry.getKey());
			metaMapToUID.put(meta, entry.getKey());
			uidMapToMeta.put(entry.getKey(), meta);

		}
	}

	@Override
	public IAllele getFromMetaMap(int meta) {
		if (!metaMapToUID.containsKey(meta))
			return null;

		return getAllele(metaMapToUID.get(meta));
	}

	@Override
	public int getFromUIDMap(String uid) {
		if (!uidMapToMeta.containsKey(uid))
			return 0;

		return uidMapToMeta.get(uid);
	}

	/* CLASSIFICATIONS */
	@Override
	public void registerClassification(IClassification branch) {

		if (classificationMap.containsKey(branch.getUID()))
			throw new RuntimeException(String.format("Could not add new classification '%s', because the key is already taken by %s.", branch.getUID(),
					classificationMap.get(branch.getUID())));

		classificationMap.put(branch.getUID(), branch);
		for (IAlleleHandler handler : this.alleleHandlers)
			handler.onRegisterClassification(branch);
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
	public IClassification getClassification(String uid) {
		return classificationMap.get(uid);
	}

	/* FRUIT FAMILIES */
	@Override
	public void registerFruitFamily(IFruitFamily family) {
		fruitMap.put(family.getUID(), family);
		for (IAlleleHandler handler : this.alleleHandlers)
			handler.onRegisterFruitFamily(family);
	}

	@Override
	public Map<String, IFruitFamily> getRegisteredFruitFamilies() {
		return Collections.unmodifiableMap(fruitMap);
	}

	@Override
	public IFruitFamily getFruitFamily(String uid) {
		return fruitMap.get(uid);
	}

	/* LEGACY MAPPINGS */
	@Override
	public void registerLegacyMapping(int id, String uid) {
		this.legacyMap.put(id, uid);
	}

	@Override
	public IAllele getFromLegacyMap(int id) {
		if (!legacyMap.containsKey(id))
			return null;

		return getAllele(legacyMap.get(id));
	}

	/* ALLELE HANDLERS */
	@Override
	public void registerAlleleHandler(IAlleleHandler handler) {
		this.alleleHandlers.add(handler);
	}

	/* BLACKLIST */
	private ArrayList<String> blacklist = new ArrayList<String>();

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
	public ItemStack getSpeciesNoteStack(String researcher, IAlleleSpecies species) {
		return EnumNoteType.createSpeciesNoteStack(ForestryItem.researchNote.item(), researcher, species);
	}
	
	@Override
	public ItemStack getMutationNoteStack(String researcher, IMutation mutation) {
		return EnumNoteType.createMutationNoteStack(ForestryItem.researchNote.item(), researcher, mutation);
	}

}
