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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;

import com.mojang.authlib.GameProfile;

import genetics.api.alleles.AlleleCategorizedValue;
import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleValue;
import genetics.api.classification.IClassification;
import genetics.api.classification.IClassification.EnumClassLevel;
import genetics.api.classification.IClassificationRegistry;
import genetics.api.mutation.IMutation;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IAlleleForestrySpecies;
import forestry.api.genetics.IAlleleHandler;
import forestry.api.genetics.IFruitFamily;
import forestry.api.genetics.IGeneticRegistry;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.genetics.ItemResearchNote.EnumNoteType;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;

public class GeneticRegistry implements IGeneticRegistry {

	/* ALLELES */
	private final LinkedHashMap<String, IFruitFamily> fruitMap = new LinkedHashMap<>(64);

	/*
	 * Internal Set of all alleleHandlers, which trigger when an allele or branch is registered
	 */
	private final Set<IAlleleHandler> alleleHandlers = new HashSet<>();

	public void registerClassifications(IClassificationRegistry registry) {
		registry.createAndRegisterClassification(IClassification.EnumClassLevel.DOMAIN, "archaea", "Archaea");
		registry.createAndRegisterClassification(EnumClassLevel.DOMAIN, "bacteria", "Bacteria");
		IClassification eukarya = registry.createAndRegisterClassification(EnumClassLevel.DOMAIN, "eukarya", "Eukarya");

		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "animalia", "Animalia"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "plantae", "Plantae"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "fungi", "Fungi"));
		eukarya.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.KINGDOM, "protista", "Protista"));

		registry.getClassification("kingdom.animalia")
			.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.PHYLUM, "arthropoda", "Arthropoda"));

		// Animalia
		registry.getClassification("phylum.arthropoda")
			.addMemberGroup(registry.createAndRegisterClassification(EnumClassLevel.CLASS, "insecta", "Insecta"));

	}

	public void registerAlleles(IAlleleRegistry registry) {
		if (ModuleHelper.anyEnabled(ForestryModuleUids.APICULTURE, ForestryModuleUids.LEPIDOPTEROLOGY)) {
			registry.registerAlleles(EnumAllele.Speed.values(),
				BeeChromosomes.SPEED,
				ButterflyChromosomes.SPEED
			);
			registry.registerAlleles(EnumAllele.Lifespan.values(),
				BeeChromosomes.LIFESPAN,
				ButterflyChromosomes.LIFESPAN
			);
			registry.registerAlleles(EnumAllele.Tolerance.values(),
				BeeChromosomes.TEMPERATURE_TOLERANCE,
				BeeChromosomes.HUMIDITY_TOLERANCE,
				ButterflyChromosomes.TEMPERATURE_TOLERANCE,
				ButterflyChromosomes.HUMIDITY_TOLERANCE
			);
			registry.registerAlleles(EnumAllele.Flowers.values(),
				BeeChromosomes.FLOWER_PROVIDER,
				ButterflyChromosomes.FLOWER_PROVIDER);
		}

		//TODO: Move to LEPIDOPTEROLOGY module
		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			registry.registerAlleles(EnumAllele.Size.values(), ButterflyChromosomes.SIZE);
		}

		for (int i = 1; i <= 10; i++) {
			registry.registerAllele("i", i + "d", i, true,
				TreeChromosomes.GIRTH,
				ButterflyChromosomes.METABOLISM,
				ButterflyChromosomes.FERTILITY
			);
		}

		Map<Boolean, IAlleleValue<Boolean>> booleans = new HashMap<>();
		booleans.put(true, new AlleleCategorizedValue<>(Constants.MOD_ID, "bool", "true", true, false));
		booleans.put(false, new AlleleCategorizedValue<>(Constants.MOD_ID, "bool", "false", false, false));
		for (IAlleleValue<Boolean> alleleBoolean : booleans.values()) {
			registry.registerAllele(alleleBoolean,
				BeeChromosomes.NEVER_SLEEPS,
				BeeChromosomes.TOLERATES_RAIN,
				BeeChromosomes.CAVE_DWELLING,
				ButterflyChromosomes.NOCTURNAL,
				ButterflyChromosomes.TOLERANT_FLYER,
				ButterflyChromosomes.FIRE_RESIST
			);
		}
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

	/* RESEARCH */
	@Override
	public ItemStack getSpeciesNoteStack(GameProfile researcher, IAlleleForestrySpecies species) {
		return EnumNoteType.createSpeciesNoteStack(ModuleCore.getItems().researchNote, researcher, species);
	}

	@Override
	public ItemStack getMutationNoteStack(GameProfile researcher, IMutation mutation) {
		return EnumNoteType.createMutationNoteStack(ModuleCore.getItems().researchNote, researcher, mutation);
	}
}
