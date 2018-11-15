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

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeMutationBuilder;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleSpeciesRegisterEvent;
import forestry.api.genetics.IAllele;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.genetics.alleles.AlleleEffects;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum BeeDefinition implements IBeeDefinition {
	/* HONEY BRANCH */
	FOREST(BeeBranchDefinition.HONEY, "nigrocincta", true, new Color(0x19d0ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1), 0.30f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.HIGH);
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	MEADOWS(BeeBranchDefinition.HONEY, "florea", true, new Color(0xef131e), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1), 0.30f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOWER);
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	COMMON(BeeBranchDefinition.HONEY, "cerana", true, new Color(0xb2b2b2), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1), 0.35f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
		}

		@Override
		protected void registerMutations() {
			for (BeeDefinition hiveBee0 : overworldHiveBees) {
				for (BeeDefinition hiveBee1 : overworldHiveBees) {
					if (hiveBee0.ordinal() < hiveBee1.ordinal()) {
						registerMutation(hiveBee0, hiveBee1, 15);
					}
				}
			}
		}
	},
	CULTIVATED(BeeBranchDefinition.HONEY, "mellifera", true, new Color(0x5734ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1), 0.40f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.FAST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		}

		@Override
		protected void registerMutations() {
			for (BeeDefinition hiveBee : overworldHiveBees) {
				registerMutation(COMMON, hiveBee, 12);
			}
		}
	},

	/* NOBLE BRANCH */
	NOBLE(BeeBranchDefinition.NOBLE, "nobilis", false, new Color(0xec9a19), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.DRIPPING, 1), 0.20f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(COMMON, CULTIVATED, 10);
		}
	},
	MAJESTIC(BeeBranchDefinition.NOBLE, "regalis", true, new Color(0x7f0000), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.DRIPPING, 1), 0.30f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTENED);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.MAXIMUM);
		}

		@Override
		protected void registerMutations() {
			registerMutation(NOBLE, CULTIVATED, 8);
		}
	},
	IMPERIAL(BeeBranchDefinition.NOBLE, "imperatorius", false, new Color(0xa3e02f), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.DRIPPING, 1), 0.20f)
				.addProduct(ModuleApiculture.getItems().royalJelly.getItemStack(), 0.15f)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectBeatific);
		}

		@Override
		protected void registerMutations() {
			registerMutation(NOBLE, MAJESTIC, 8);
		}
	},

	/* INDUSTRIOUS BRANCH */
	DILIGENT(BeeBranchDefinition.INDUSTRIOUS, "sedulus", false, new Color(0xc219ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.STRINGY, 1), 0.20f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.SLOW);
		}

		@Override
		protected void registerMutations() {
			registerMutation(COMMON, CULTIVATED, 10);
		}
	},
	UNWEARY(BeeBranchDefinition.INDUSTRIOUS, "assiduus", true, new Color(0x19ec5a), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.STRINGY, 1), 0.30f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTENED);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DILIGENT, CULTIVATED, 8);
		}
	},
	INDUSTRIOUS(BeeBranchDefinition.INDUSTRIOUS, "industria", false, new Color(0xffffff), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.STRINGY, 1), 0.20f)
				.addProduct(ModuleApiculture.getItems().pollenCluster.get(EnumPollenCluster.NORMAL, 1), 0.15f)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FAST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DILIGENT, UNWEARY, 8);
		}
	},

	/* HEROIC BRANCH */
	STEADFAST(BeeBranchDefinition.HEROIC, "legio", false, new Color(0x4d2b15), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.COCOA, 1), 0.20f)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.CAVE_DWELLING, true);
		}

		@Override
		protected void registerMutations() {
			// only found in dungeons chests
		}
	},
	VALIANT(BeeBranchDefinition.HEROIC, "centurio", true, new Color(0x626bdd), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.COCOA, 1), 0.30f)
				.addSpecialty(new ItemStack(Items.SUGAR), 0.15f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.CAVE_DWELLING, true);
		}

		@Override
		protected void registerMutations() {
			// found rarely at random in other bee's hives
		}
	},
	HEROIC(BeeBranchDefinition.HEROIC, "kraphti", false, new Color(0xb3d5e4), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.COCOA, 1), 0.40f)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.CAVE_DWELLING, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectHeroic);
		}

		@Override
		protected void registerMutations() {
			registerMutation(STEADFAST, VALIANT, 6)
				.restrictBiomeType(BiomeDictionary.Type.FOREST);
		}
	},

	/* INFERNAL BRANCH */
	SINISTER(BeeBranchDefinition.INFERNAL, "caecus", false, new Color(0xb3d5e4), new Color(0x9a2323)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SIMMERING, 1), 0.45f)
				.setTemperature(EnumTemperature.HELLISH)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectAggressive);
		}

		@Override
		protected void registerMutations() {
			for (BeeDefinition parent2 : EnumSet.of(MODEST, TROPICAL)) {
				registerMutation(CULTIVATED, parent2, 60)
					.restrictBiomeType(BiomeDictionary.Type.NETHER);
			}
		}
	},
	FIENDISH(BeeBranchDefinition.INFERNAL, "diabolus", true, new Color(0xd7bee5), new Color(0x9a2323)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SIMMERING, 1), 0.55f)
				.addProduct(ModuleCore.getItems().ash.getItemStack(), 0.15f)
				.setTemperature(EnumTemperature.HELLISH)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectAggressive);
		}

		@Override
		protected void registerMutations() {
			for (BeeDefinition parent2 : EnumSet.of(CULTIVATED, MODEST, TROPICAL)) {
				registerMutation(SINISTER, parent2, 40)
					.restrictBiomeType(BiomeDictionary.Type.NETHER);
			}
		}
	},
	DEMONIC(BeeBranchDefinition.INFERNAL, "draco", false, new Color(0xf4e400), new Color(0x9a2323)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SIMMERING, 1), 0.45f)
				.addProduct(new ItemStack(Items.GLOWSTONE_DUST), 0.15f)
				.setHasEffect()
				.setTemperature(EnumTemperature.HELLISH)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectIgnition);
		}

		@Override
		protected void registerMutations() {
			registerMutation(SINISTER, FIENDISH, 25)
				.restrictBiomeType(BiomeDictionary.Type.NETHER);
		}
	},

	/* AUSTERE BRANCH */
	MODEST(BeeBranchDefinition.AUSTERE, "modicus", false, new Color(0xc5be86), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.PARCHED, 1), 0.20f)
				.setTemperature(EnumTemperature.HOT)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	FRUGAL(BeeBranchDefinition.AUSTERE, "permodestus", true, new Color(0xe8dcb1), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.PARCHED, 1), 0.30f)
				.setTemperature(EnumTemperature.HOT)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MODEST, SINISTER, 16)
				.restrictTemperature(EnumTemperature.HOT, EnumTemperature.HELLISH)
				.restrictHumidity(EnumHumidity.ARID);
			registerMutation(MODEST, FIENDISH, 10)
				.restrictTemperature(EnumTemperature.HOT, EnumTemperature.HELLISH)
				.restrictHumidity(EnumHumidity.ARID);
		}
	},
	AUSTERE(BeeBranchDefinition.AUSTERE, "correpere", false, new Color(0xfffac2), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.PARCHED, 1), 0.20f)
				.addSpecialty(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.POWDERY, 1), 0.50f)
				.setHasEffect()
				.setTemperature(EnumTemperature.HOT)
				.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_2);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectCreeper);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MODEST, FRUGAL, 8)
				.restrictTemperature(EnumTemperature.HOT, EnumTemperature.HELLISH)
				.restrictHumidity(EnumHumidity.ARID);
		}
	},

	/* TROPICAL BRANCH */
	TROPICAL(BeeBranchDefinition.TROPICAL, "mendelia", false, new Color(0x378020), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SILKY, 1), 0.20f)
				.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	EXOTIC(BeeBranchDefinition.TROPICAL, "darwini", true, new Color(0x304903), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SILKY, 1), 0.30f)
				.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONG);
		}

		@Override
		protected void registerMutations() {
			registerMutation(AUSTERE, TROPICAL, 12);
		}
	},
	EDENIC(BeeBranchDefinition.TROPICAL, "humboldti", false, new Color(0x393d0d), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SILKY, 1), 0.20f)
				.setHasEffect()
				.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectExploration);
		}

		@Override
		protected void registerMutations() {
			registerMutation(EXOTIC, TROPICAL, 8);
		}
	},

	/* END BRANCH */
	ENDED(BeeBranchDefinition.END, "notchi", false, new Color(0xe079fa), new Color(0xd9de9e)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MYSTERIOUS, 1), 0.30f)
				.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	SPECTRAL(BeeBranchDefinition.END, "idolum", true, new Color(0xa98bed), new Color(0xd9de9e)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MYSTERIOUS, 1), 0.50f)
				.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectReanimation);
		}

		@Override
		protected void registerMutations() {
			registerMutation(HERMITIC, ENDED, 4);
		}
	},
	PHANTASMAL(BeeBranchDefinition.END, "lemur", false, new Color(0xcc00fa), new Color(0xd9de9e)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MYSTERIOUS, 1), 0.40f)
				.setHasEffect()
				.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectResurrection);
		}

		@Override
		protected void registerMutations() {
			registerMutation(SPECTRAL, ENDED, 2);
		}
	},

	/* FROZEN BRANCH */
	WINTRY(BeeBranchDefinition.FROZEN, "brumalis", false, new Color(0xa0ffc8), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.FROZEN, 1), 0.30f)
				.setTemperature(EnumTemperature.ICY);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.MAXIMUM);
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	ICY(BeeBranchDefinition.FROZEN, "coagulis", true, new Color(0xa0ffff), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.FROZEN, 1), 0.20f)
				.addProduct(ModuleCore.getItems().craftingMaterial.getIceShard(1), 0.20f)
				.setTemperature(EnumTemperature.ICY);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}

		@Override
		protected void registerMutations() {
			registerMutation(INDUSTRIOUS, WINTRY, 12)
				.restrictTemperature(EnumTemperature.ICY, EnumTemperature.COLD);
		}
	},
	GLACIAL(BeeBranchDefinition.FROZEN, "glacialis", false, new Color(0xefffff), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.FROZEN, 1), 0.20f)
				.addProduct(ModuleCore.getItems().craftingMaterial.getIceShard(1), 0.40f)
				.setTemperature(EnumTemperature.ICY)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}

		@Override
		protected void registerMutations() {
			registerMutation(ICY, WINTRY, 8)
				.restrictTemperature(EnumTemperature.ICY, EnumTemperature.COLD);
		}
	},

	/* VENGEFUL BRANCH */
	VINDICTIVE(BeeBranchDefinition.VENGEFUL, "ultio", false, new Color(0xeafff3), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.IRRADIATED, 1), 0.25f)
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, DEMONIC, 4).setIsSecret();
		}
	},
	VENGEFUL(BeeBranchDefinition.VENGEFUL, "punire", false, new Color(0xc2de00), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.IRRADIATED, 1), 0.40f)
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.NORMAL);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(DEMONIC, VINDICTIVE, 8).setIsSecret();
			registerMutation(MONASTIC, VINDICTIVE, 8).setIsSecret();
		}
	},
	AVENGING(BeeBranchDefinition.VENGEFUL, "hostimentum", false, new Color(0xddff00), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.IRRADIATED, 1), 0.40f)
				.setHasEffect()
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.LIFESPAN, EnumAllele.Lifespan.LONGEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(VENGEFUL, VINDICTIVE, 4);
		}
	},

	/* FESTIVE BRANCH */
	// Easter
	LEPORINE(BeeBranchDefinition.FESTIVE, "lepus", false, new Color(0xfeff8f), new Color(0x3cd757)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.SILKY, 1), 0.30f)
				.addProduct(new ItemStack(Items.EGG), 0.10f)
				.setHasEffect()
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectFestiveEaster);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MEADOWS, FOREST, 10)
				.restrictDateRange(3, 29, 4, 15)
				.setIsSecret();
		}

		@Override
		protected boolean isSecret() {
			return true;
		}
	},
	// Christmas
	MERRY(BeeBranchDefinition.FESTIVE, "feliciter", false, new Color(0xffffff), new Color(0xd40000)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.FROZEN, 1), 0.30f)
				.addProduct(ModuleCore.getItems().craftingMaterial.getIceShard(1), 0.20f)
				.setTemperature(EnumTemperature.ICY)
				.setHasEffect()
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectSnowing);
		}

		@Override
		protected void registerMutations() {
			registerMutation(WINTRY, FOREST, 10)
				.restrictDateRange(12, 21, 12, 27)
				.setIsSecret();
		}

		@Override
		protected boolean isSecret() {
			return true;
		}
	},
	// New Year
	TIPSY(BeeBranchDefinition.FESTIVE, "ebrius", false, new Color(0xffffff), new Color(0xc219ec)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.FROZEN, 1), 0.30f)
				.addProduct(ModuleCore.getItems().craftingMaterial.getIceShard(1), 0.20f)
				.setTemperature(EnumTemperature.ICY)
				.setHasEffect()
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectDrunkard);
		}

		@Override
		protected void registerMutations() {
			registerMutation(WINTRY, MEADOWS, 10)
				.restrictDateRange(12, 27, 1, 2)
				.setIsSecret();
		}

		@Override
		protected boolean isSecret() {
			return true;
		}
	},
	// (missing) Solstice
	// Halloween
	TRICKY(BeeBranchDefinition.FESTIVE, "libita", false, new Color(0x49413B), new Color(0xFF6A00)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1), 0.40f)
				.addProduct(new ItemStack(Items.COOKIE), 0.15f)
				.addSpecialty(new ItemStack(Items.SKULL, 1, 0), 0.02f)
				.addSpecialty(new ItemStack(Items.SKULL, 1, 2), 0.02f)
				.addSpecialty(new ItemStack(Items.SKULL, 1, 3), 0.02f)
				.addSpecialty(new ItemStack(Items.SKULL, 1, 4), 0.02f)
				.setHasEffect()
				.setIsNotCounted();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TOLERATES_RAIN, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.GOURD);
		}

		@Override
		protected void registerMutations() {
			registerMutation(SINISTER, COMMON, 10)
				.restrictDateRange(10, 15, 11, 3)
				.setIsSecret();
		}

		@Override
		protected boolean isSecret() {
			return true;
		}
	},
	// (missing) Thanksgiving

	/* AGRARIAN BRANCH */
	RURAL(BeeBranchDefinition.AGRARIAN, "rustico", false, new Color(0xfeff8f), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.WHEATEN, 1), 0.20f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			registerMutation(MEADOWS, DILIGENT, 12)
				.restrictBiomeType(BiomeDictionary.Type.PLAINS);
		}
	},
	FARMERLY(BeeBranchDefinition.AGRARIAN, "arator", true, new Color(0xD39728), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.WHEATEN, 1), 0.27f);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(RURAL, UNWEARY, 10)
				.restrictBiomeType(BiomeDictionary.Type.PLAINS);
		}
	},
	AGRARIAN(BeeBranchDefinition.AGRARIAN, "arator", true, new Color(0xFFCA75), new Color(0xFFE047)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.WHEATEN, 1), 0.35f)
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_2);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectFertile);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGE);
		}

		@Override
		protected void registerMutations() {
			registerMutation(FARMERLY, INDUSTRIOUS, 6)
				.restrictBiomeType(BiomeDictionary.Type.PLAINS);
		}
	},

	/* BOGGY BRANCH */
	MARSHY(BeeBranchDefinition.BOGGY, "adorasti", true, new Color(0x546626), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MOSSY, 1), 0.30f)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	MIRY(BeeBranchDefinition.BOGGY, "humidium", true, new Color(0x92AF42), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MOSSY, 1), 0.36f)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FERTILITY, EnumAllele.Fertility.MAXIMUM);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TOLERATES_RAIN, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MARSHY, NOBLE, 15)
				.restrictTemperature(EnumTemperature.WARM)
				.restrictHumidity(EnumHumidity.DAMP);
		}
	},
	BOGGY(BeeBranchDefinition.BOGGY, "paluster", true, new Color(0x698948), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MOSSY, 1), 0.39f)
				.addSpecialty(ModuleCore.getItems().peat.getItemStack(), 0.08f)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TOLERATES_RAIN, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.NEVER_SLEEPS, true);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectMycophilic);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.TERRITORY, EnumAllele.Territory.LARGER);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MARSHY, MIRY, 9)
				.restrictTemperature(EnumTemperature.WARM)
				.restrictHumidity(EnumHumidity.DAMP);
		}
	},

	/* MONASTIC BRANCH */
	MONASTIC(BeeBranchDefinition.MONASTIC, "monachus", false, new Color(0x42371c), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addProduct(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.WHEATEN, 1), 0.30f)
				.addSpecialty(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MELLOW, 1), 0.10f)
				.setJubilanceProvider(new JubilanceProviderHermit());
		}

		@Override
		protected void setAlleles(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// can only be bought from Apiarist Villagers
		}
	},
	SECLUDED(BeeBranchDefinition.MONASTIC, "contractus", true, new Color(0x7b6634), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addSpecialty(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MELLOW, 1), 0.20f)
				.setJubilanceProvider(new JubilanceProviderHermit());
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTEST);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, AUSTERE, 12);
		}
	},
	HERMITIC(BeeBranchDefinition.MONASTIC, "anachoreta", false, new Color(0xffd46c), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies) {
			beeSpecies.addSpecialty(ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.MELLOW, 1), 0.20f)
				.setJubilanceProvider(new JubilanceProviderHermit())
				.setHasEffect();
		}

		@Override
		protected void setAlleles(IAllele[] template) {
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.FLOWERING, EnumAllele.Flowering.FASTEST);
			AlleleHelper.getInstance().set(template, EnumBeeChromosome.EFFECT, AlleleEffects.effectRepulsion);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, SECLUDED, 8);
		}
	};

	private static final EnumSet<BeeDefinition> overworldHiveBees = EnumSet.of(FOREST, MARSHY, MEADOWS, MODEST, TROPICAL, WINTRY);

	private final BeeBranchDefinition branch;
	private final IAlleleBeeSpecies species;

	@Nullable
	private IAllele[] template;
	@Nullable
	private IBeeGenome genome;

	BeeDefinition(BeeBranchDefinition branch, String binomial, boolean dominant, Color primary, Color secondary) {
		String lowercaseName = this.toString().toLowerCase(Locale.ENGLISH);
		String species = "species" + WordUtils.capitalize(lowercaseName);

		String modId = Constants.MOD_ID;
		String uid = modId + '.' + species;
		String description = "for.description." + species;
		String name = "for.bees.species." + lowercaseName;

		this.branch = branch;
		IAlleleBeeSpeciesBuilder speciesBuilder = BeeManager.beeFactory.createSpecies(modId, uid, dominant, "Sengir", name, description, branch.getBranch(), binomial, primary.getRGB(), secondary.getRGB());
		if (isSecret()) {
			speciesBuilder.setIsSecret();
		}
		setSpeciesProperties(speciesBuilder);
		this.species = speciesBuilder.build();
	}

	protected abstract void setSpeciesProperties(IAlleleBeeSpeciesBuilder beeSpecies);

	protected abstract void setAlleles(IAllele[] template);

	protected abstract void registerMutations();

	protected boolean isSecret() {
		return false;
	}

	public static void initBees() {
		for (BeeDefinition bee : values()) {
			bee.init();
		}
		for (BeeDefinition bee : values()) {
			bee.registerMutations();
		}
	}

	public static void preInit() {
		MinecraftForge.EVENT_BUS.post(new AlleleSpeciesRegisterEvent<>(BeeManager.beeRoot, IAlleleBeeSpecies.class));
	}

	private void init() {
		template = branch.getTemplate();
		AlleleHelper.getInstance().set(template, EnumBeeChromosome.SPECIES, species);
		setAlleles(template);

		genome = BeeManager.beeRoot.templateAsGenome(template);

		BeeManager.beeRoot.registerTemplate(template);
	}

	protected final IBeeMutationBuilder registerMutation(BeeDefinition parent1, BeeDefinition parent2, int chance) {
		return BeeManager.beeMutationFactory.createMutation(parent1.species, parent2.species, getTemplate(), chance);
	}

	@Override
	public final IAllele[] getTemplate() {
		return Arrays.copyOf(template, template.length);
	}

	@Override
	public final IBeeGenome getGenome() {
		return genome;
	}

	@Override
	public final IBee getIndividual() {
		return new Bee(genome);
	}

	@Override
	public final ItemStack getMemberStack(EnumBeeType beeType) {
		IBee bee = getIndividual();
		return BeeManager.beeRoot.getMemberStack(bee, beeType);
	}

	public final IBeeDefinition getRainResist() {
		return new BeeVariation.RainResist(this);
	}
}
