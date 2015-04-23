package forestry.apiculture.genetics;

import java.awt.Color;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.BiomeDictionary;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.config.ForestryItem;
import forestry.core.genetics.Allele;
import forestry.plugins.PluginApiculture;

public enum BeeDefinition implements IBeeDefinition {
	/* HONEY BRANCH */
	FOREST(BeeBranchDefinition.HONEY, "nigrocincta", true, new Color(0x19d0ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 30);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
			template[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityHigh;
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	MEADOWS(BeeBranchDefinition.HONEY, "florea", true, new Color(0xef131e), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 30);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	COMMON(BeeBranchDefinition.HONEY, "cerana", true, new Color(0xb2b2b2), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 35);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		}

		@Override
		protected void registerMutations() {
			for (BeeDefinition hiveBee0 : overworldHiveBees) {
				for (BeeDefinition hiveBee1 : overworldHiveBees) {
					if (hiveBee0 != hiveBee1) {
						registerMutation(hiveBee0, hiveBee1, 15);
					}
				}
			}
		}
	},
	CULTIVATED(BeeBranchDefinition.HONEY, "mellifera", true, new Color(0x5734ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 40);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedFast;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
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
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 5), 20);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlow;
		}

		@Override
		protected void registerMutations() {
			registerMutation(COMMON, CULTIVATED, 10);
		}
	},
	MAJESTIC(BeeBranchDefinition.NOBLE, "regalis", true, new Color(0x7f0000), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 5), 30);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortened;
			template[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
		}

		@Override
		protected void registerMutations() {
			registerMutation(NOBLE, CULTIVATED, 8);
		}
	},
	IMPERIAL(BeeBranchDefinition.NOBLE, "imperatorius", false, new Color(0xa3e02f), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 5), 20)
					.addProduct(ForestryItem.royalJelly.getItemStack(), 15)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectBeatific;
		}

		@Override
		protected void registerMutations() {
			registerMutation(NOBLE, MAJESTIC, 8);
		}
	},

	/* INDUSTRIOUS BRANCH */
	DILIGENT(BeeBranchDefinition.INDUSTRIOUS, "sedulus", false, new Color(0xc219ec), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 3), 20);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlow;
		}

		@Override
		protected void registerMutations() {
			registerMutation(COMMON, CULTIVATED, 10);
		}
	},
	UNWEARY(BeeBranchDefinition.INDUSTRIOUS, "assiduus", true, new Color(0x19ec5a), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 3), 30);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortened;
		}

		@Override
		protected void registerMutations() {
			registerMutation(DILIGENT, CULTIVATED, 8);
		}
	},
	INDUSTRIOUS(BeeBranchDefinition.INDUSTRIOUS, "industria", false, new Color(0xffffff), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 3), 20)
					.addProduct(ForestryItem.pollenCluster.getItemStack(), 15)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFast;
		}

		@Override
		protected void registerMutations() {
			registerMutation(DILIGENT, UNWEARY, 8);
		}
	},

	/* HEROIC BRANCH */
	STEADFAST(BeeBranchDefinition.HEROIC, "legio", false, new Color(0x4d2b15), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 20)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		}

		@Override
		protected void registerMutations() {
			// only found in dungeons chests
		}
	},
	VALIANT(BeeBranchDefinition.HEROIC, "centurio", true, new Color(0x626bdd), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 30)
					.addSpecialty(new ItemStack(Items.sugar), 15);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		}

		@Override
		protected void registerMutations() {
			// found rarely at random in other bee's hives
		}
	},
	HEROIC(BeeBranchDefinition.HEROIC, "kraphti", false, new Color(0xb3d5e4), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 1), 40)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectHeroic;
		}

		@Override
		protected void registerMutations() {
			registerMutation(STEADFAST, VALIANT, 6)
					.restrictBiomeType(BiomeDictionary.Type.FOREST)
					.enableStrictBiomeCheck();
		}
	},

	/* INFERNAL BRANCH */
	SINISTER(BeeBranchDefinition.INFERNAL, "caecus", false, new Color(0xb3d5e4), new Color(0x9a2323)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 45)
					.setEntityTexture("sinisterBee")
					.setTemperature(EnumTemperature.HELLISH)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectAggressive;
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
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 55)
					.addProduct(ForestryItem.ash.getItemStack(), 15)
					.setEntityTexture("sinisterBee")
					.setTemperature(EnumTemperature.HELLISH)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectAggressive;
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
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 2), 45)
					.addProduct(new ItemStack(Items.glowstone_dust), 15)
					.setEntityTexture("sinisterBee")
					.setHasEffect()
					.setTemperature(EnumTemperature.HELLISH)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectIgnition;
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
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 20)
					.setTemperature(EnumTemperature.HOT)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	FRUGAL(BeeBranchDefinition.AUSTERE, "permodestus", true, new Color(0xe8dcb1), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 30)
					.setTemperature(EnumTemperature.HOT)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		}

		@Override
		protected void registerMutations() {
			registerMutation(MODEST, SINISTER, 16)
					.setTemperatureRainfall(1.9f, 2.0f, 0.0f, 0.1f);
			registerMutation(MODEST, FIENDISH, 10)
					.setTemperatureRainfall(1.9f, 2.0f, 0.0f, 0.1f);
		}
	},
	AUSTERE(BeeBranchDefinition.AUSTERE, "correpere", false, new Color(0xfffac2), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 7), 20)
					.addSpecialty(ForestryItem.beeComb.getItemStack(1, 10), 50)
					.setHasEffect()
					.setTemperature(EnumTemperature.HOT)
					.setHumidity(EnumHumidity.ARID);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
			template[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown2;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectCreeper;
		}

		@Override
		protected void registerMutations() {
			registerMutation(MODEST, FRUGAL, 8)
					.setTemperatureRainfall(1.9f, 2.0f, 0.0f, 0.1f);
		}
	},

	/* TROPICAL BRANCH */
	TROPICAL(BeeBranchDefinition.TROPICAL, "mendelia", false, new Color(0x378020), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 20)
					.setEntityTexture("tropicalBee")
					.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	EXOTIC(BeeBranchDefinition.TROPICAL, "darwini", true, new Color(0x304903), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 30)
					.setEntityTexture("tropicalBee")
					.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		}

		@Override
		protected void registerMutations() {
			registerMutation(AUSTERE, TROPICAL, 12);
		}
	},
	EDENIC(BeeBranchDefinition.TROPICAL, "humboldti", false, new Color(0x393d0d), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 20)
					.setEntityTexture("tropicalBee")
					.setHasEffect()
					.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
			template[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectExploration;
		}

		@Override
		protected void registerMutations() {
			registerMutation(EXOTIC, TROPICAL, 8);
		}
	},

	/* END BRANCH */
	ENDED(BeeBranchDefinition.END, "notchi", false, new Color(0xe079fa), new Color(0xd9de9e)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 30)
					.setEntityTexture("endBee")
					.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	SPECTRAL(BeeBranchDefinition.END, "idolum", true, new Color(0xa98bed), new Color(0xd9de9e)) {

		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 50)
					.setEntityTexture("endBee")
					.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectReanimation;
		}
		
		@Override
		protected void registerMutations() {
			registerMutation(HERMITIC, ENDED, 4);
		}
	},
	PHANTASMAL(BeeBranchDefinition.END, "lemur", false, new Color(0xcc00fa), new Color(0xd9de9e)) {

		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 8), 40)
					.setEntityTexture("endBee")
					.setHasEffect()
					.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLongest;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectResurrection;
		}
		
		@Override
		protected void registerMutations() {
			registerMutation(SPECTRAL, ENDED, 2);
		}
	},

	/* FROZEN BRANCH */
	WINTRY(BeeBranchDefinition.FROZEN, "brumalis", false, new Color(0xa0ffc8), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 30)
					.setEntityTexture("icyBee")
					.setTemperature(EnumTemperature.ICY);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
			template[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	ICY(BeeBranchDefinition.FROZEN, "coagulis", true, new Color(0xa0ffff), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 20)
					.addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
					.setEntityTexture("icyBee")
					.setTemperature(EnumTemperature.ICY);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		}

		@Override
		protected void registerMutations() {
			registerMutation(INDUSTRIOUS, WINTRY, 12)
					.setTemperature(0f, 0.15f);
		}
	},
	GLACIAL(BeeBranchDefinition.FROZEN, "glacialis", false, new Color(0xefffff), new Color(0xdaf5f3)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 20)
					.addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 40)
					.setEntityTexture("icyBee")
					.setTemperature(EnumTemperature.ICY)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		}

		@Override
		protected void registerMutations() {
			registerMutation(ICY, WINTRY, 8)
					.setTemperature(0f, 0.15f);
		}
	},

	/* VENGEFUL BRANCH */
	VINDICTIVE(BeeBranchDefinition.VENGEFUL, "ultio", false, new Color(0xeafff3), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 25)
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		}
		
		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, DEMONIC, 4).setIsSecret();
		}
	},
	VENGEFUL(BeeBranchDefinition.VENGEFUL, "punire", false, new Color(0xc2de00), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 40)
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		}
		
		@Override
		protected void registerMutations() {
			registerMutation(DEMONIC, VINDICTIVE, 8).setIsSecret();
			registerMutation(MONASTIC, VINDICTIVE, 8).setIsSecret();
		}
	},
	AVENGING(BeeBranchDefinition.VENGEFUL, "hostimentum", false, new Color(0xddff00), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 9), 40)
					.setHasEffect()
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			template[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLongest;
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
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 6), 30)
					.addProduct(new ItemStack(Items.egg), 10)
					.setHasEffect()
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectFestiveEaster;
		}

		@Override
		protected void registerMutations() {
			registerMutationTimeLimited(MEADOWS, FOREST, 10, 3, 29, 4, 15).setIsSecret();
		}
	},
	// Christmas
	MERRY(BeeBranchDefinition.FESTIVE, "feliciter", false, new Color(0xffffff), new Color(0xd40000)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 30)
					.addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
					.setTemperature(EnumTemperature.ICY)
					.setHasEffect()
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectSnowing;
		}

		@Override
		protected void registerMutations() {
			registerMutationTimeLimited(WINTRY, FOREST, 10, 12, 21, 12, 27).setIsSecret();
		}
	},
	// New Year
	TIPSY(BeeBranchDefinition.FESTIVE, "ebrius", false, new Color(0xffffff), new Color(0xc219ec)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 4), 30)
					.addProduct(ForestryItem.craftingMaterial.getItemStack(1, 5), 20)
					.setTemperature(EnumTemperature.ICY)
					.setHasEffect()
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectDrunkard;
		}

		@Override
		protected void registerMutations() {
			registerMutationTimeLimited(WINTRY, MEADOWS, 10, 12, 27, 1, 2).setIsSecret();
		}
	},
	// (missing) Solstice
	// Halloween
	TRICKY(BeeBranchDefinition.FESTIVE, "libita", false, new Color(0x49413B), new Color(0xFF6A00)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 0), 40)
					.addProduct(new ItemStack(Items.cookie), 15)
					.addSpecialty(new ItemStack(Items.skull, 1, 0), 2)
					.addSpecialty(new ItemStack(Items.skull, 1, 2), 2)
					.addSpecialty(new ItemStack(Items.skull, 1, 3), 2)
					.addSpecialty(new ItemStack(Items.skull, 1, 4), 2)
					.setHasEffect()
					.setIsNotCounted();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersGourd;
		}

		@Override
		protected void registerMutations() {
			registerMutationTimeLimited(SINISTER, COMMON, 10, 10, 15, 11, 3).setIsSecret();
		}
	},
	// (missing) Thanksgiving

	/* AGRARIAN BRANCH */
	RURAL(BeeBranchDefinition.AGRARIAN, "rustico", false, new Color(0xfeff8f), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 14), 20);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			registerMutation(MEADOWS, DILIGENT, 12)
					.restrictBiomeType(BiomeDictionary.Type.PLAINS)
					.enableStrictBiomeCheck();
		}
	},
	FARMERLY(BeeBranchDefinition.AGRARIAN, "arator", true, new Color(0xD39728), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 14), 27);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
			template[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
		}

		@Override
		protected void registerMutations() {
			registerMutation(RURAL, UNWEARY, 10)
					.restrictBiomeType(BiomeDictionary.Type.PLAINS)
					.enableStrictBiomeCheck();
		}
	},
	AGRARIAN(BeeBranchDefinition.AGRARIAN, "arator", true, new Color(0xFFCA75), new Color(0xFFE047)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 14), 35)
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
			template[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectFertile;
			template[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
		}

		@Override
		protected void registerMutations() {
			registerMutation(FARMERLY, INDUSTRIOUS, 6)
					.restrictBiomeType(BiomeDictionary.Type.PLAINS)
					.enableStrictBiomeCheck();
		}
	},

	/* BOGGY BRANCH */
	MARSHY(BeeBranchDefinition.BOGGY, "adorasti", true, new Color(0x546626), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 15), 30)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// found in hives
		}
	},
	MIRY(BeeBranchDefinition.BOGGY, "humidium", true, new Color(0x92AF42), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 15), 36)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
			template[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		}

		@Override
		protected void registerMutations() {
			registerMutation(MARSHY, NOBLE, 15)
					.setTemperatureRainfall(0.75f, 0.94f, 0.75f, 2.0f);
		}
	},
	BOGGY(BeeBranchDefinition.BOGGY, "paluster", true, new Color(0x698948), new Color(0xffdc16)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 15), 39)
					.addSpecialty(ForestryItem.peat.getItemStack(), 8)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMycophilic;
			template[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarger;
		}

		@Override
		protected void registerMutations() {
			registerMutation(MARSHY, MIRY, 9)
					.setTemperatureRainfall(0.75f, 0.94f, 0.75f, 2.0f);
		}
	},

	/* MONASTIC BRANCH */
	MONASTIC(BeeBranchDefinition.MONASTIC, "monachus", false, new Color(0x42371c), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addProduct(ForestryItem.beeComb.getItemStack(1, 14), 30)
					.addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 10)
					.setJubilanceProvider(new JubilanceProviderHermit());
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {

		}

		@Override
		protected void registerMutations() {
			// can only be bought from Apiarist Villagers
		}
	},
	SECLUDED(BeeBranchDefinition.MONASTIC, "contractus", true, new Color(0x7b6634), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 20)
					.setJubilanceProvider(new JubilanceProviderHermit());
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFastest;
		}

		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, AUSTERE, 12);
		}
	},
	HERMITIC(BeeBranchDefinition.MONASTIC, "anachoreta", false, new Color(0xffd46c), new Color(0xfff7b6)) {
		@Override
		protected void setSpeciesProperties(AlleleBeeSpecies beeSpecies) {
			beeSpecies.addSpecialty(ForestryItem.beeComb.getItemStack(1, 16), 20)
					.setJubilanceProvider(new JubilanceProviderHermit())
					.setHasEffect();
		}

		@Override
		protected void initializeTemplate(IAllele[] template) {
			template[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFastest;
			template[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectRepulsion;
		}
		
		@Override
		protected void registerMutations() {
			registerMutation(MONASTIC, SECLUDED, 8);
		}
	};

	public static final EnumSet<BeeDefinition> overworldHiveBees = EnumSet.of(FOREST, MARSHY, MEADOWS, MODEST, TROPICAL, WINTRY);

	private final BeeBranchDefinition branch;
	private final AlleleBeeSpecies species;
	private IAllele[] template;
	private IBeeGenome genome;
	
	BeeDefinition(BeeBranchDefinition branch, String binomial, boolean dominant, Color primary, Color secondary) {
		String lowercaseName = this.toString().toLowerCase(Locale.ENGLISH);
		String uid = "species" + WordUtils.capitalize(lowercaseName);
		String name = "bees.species." + lowercaseName;

		this.species = new AlleleBeeSpecies(uid, dominant, name, branch.getBranch(), binomial, primary.getRGB(), secondary.getRGB());
		this.branch = branch;
	}

	protected abstract void setSpeciesProperties(AlleleBeeSpecies beeSpecies);

	protected abstract void initializeTemplate(IAllele[] template);

	public static void initBees() {
		for (BeeDefinition bee : values()) {
			bee.init();
		}
		for (BeeDefinition bee : values()) {
			bee.registerMutations();
		}
	}

	private void init() {
		if (!overworldHiveBees.contains(this)) {
			species.setIsSecret();
		}
		setSpeciesProperties(species);

		template = branch.getTemplate();
		template[EnumBeeChromosome.SPECIES.ordinal()] = species;
		initializeTemplate(template);

		genome = PluginApiculture.beeInterface.templateAsGenome(template);

		AlleleManager.alleleRegistry.registerAllele(species);
		PluginApiculture.beeInterface.registerTemplate(template);
	}

	protected abstract void registerMutations();

	protected final BeeMutation registerMutation(BeeDefinition parent1, BeeDefinition parent2, int chance) {
		BeeMutation beeMutation = new BeeMutation(parent1, parent2, this, chance);
		PluginApiculture.beeInterface.registerMutation(beeMutation);
		return beeMutation;
	}

	protected final BeeMutation registerMutationTimeLimited(BeeDefinition parent1, BeeDefinition parent2, int chance, int startMonth, int startDay, int endMonth, int endDay) {
		MutationTimeLimited.DayMonth start = new MutationTimeLimited.DayMonth(startDay, startMonth);
		MutationTimeLimited.DayMonth end = new MutationTimeLimited.DayMonth(endDay, endMonth);

		MutationTimeLimited beeMutation = new MutationTimeLimited(parent1, parent2, this, chance, start, end);
		PluginApiculture.beeInterface.registerMutation(beeMutation);
		return beeMutation;
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
		return PluginApiculture.beeInterface.getMemberStack(bee, beeType.ordinal());
	}

	public final IBeeDefinition getRainResist() {
		return new BeeVariation.RainResist(this);
	}
}
