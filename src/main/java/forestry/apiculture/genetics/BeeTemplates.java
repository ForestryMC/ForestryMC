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

import java.util.Arrays;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IMutation;
import forestry.core.genetics.Allele;

public class BeeTemplates {

	// / MUTATIONS
	public static IMutation commonA;

	public static IMutation commonB;
	public static IMutation commonC;

	public static IMutation commonD;
	public static IMutation commonE;
	public static IMutation commonF;

	public static IMutation commonG;
	public static IMutation commonH;
	public static IMutation commonI;
	public static IMutation commonJ;

	public static IMutation commonK;
	public static IMutation commonL;
	public static IMutation commonM;
	public static IMutation commonN;
	public static IMutation commonO;

	public static IMutation cultivatedA;
	public static IMutation cultivatedB;
	public static IMutation cultivatedC;
	public static IMutation cultivatedD;
	public static IMutation cultivatedE;
	public static IMutation cultivatedF;

	public static IMutation nobleA;
	public static IMutation majesticA;
	public static IMutation imperialA;

	public static IMutation diligentA;
	public static IMutation unwearyA;
	public static IMutation industriousA;

	public static IMutation heroicA;

	public static IMutation sinisterA;
	public static IMutation sinisterB;
	public static IMutation fiendishA;
	public static IMutation fiendishB;
	public static IMutation fiendishC;
	public static IMutation demonicA;

	// Austere branch
	public static IMutation frugalA;
	public static IMutation frugalB;
	public static IMutation austereA;

	// Tropical branch
	public static IMutation exoticA;
	public static IMutation edenicA;

	// Wintry branch
	public static IMutation icyA;
	public static IMutation glacialA;

	// Festive branch
	public static IMutation leporineA;
	public static IMutation merryA;
	public static IMutation tipsyA;
	public static IMutation trickyA;

	// Agrarian branch
	public static IMutation ruralA;

	// Monastic branch
	public static IMutation secludedA;
	public static IMutation hermiticA;

	// End branch
	public static IMutation spectralA;
	public static IMutation phantasmalA;

	public static IMutation vindictiveA;

	public static IMutation vengefulA;
	public static IMutation vengefulB;
	public static IMutation avengingA;

	public static IAllele[] defaultTemplate;

	public static IAllele[] getDefaultTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

			defaultTemplate[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesForest;
			defaultTemplate[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
			defaultTemplate[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
			defaultTemplate[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityNormal;
			defaultTemplate[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceNone;
			defaultTemplate[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceNone;
			defaultTemplate[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolFalse;
			defaultTemplate[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersVanilla;
			defaultTemplate[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlowest;
			defaultTemplate[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryDefault;
			defaultTemplate[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectNone;
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}

	// / COMMON BRANCH

	public static IAllele[] getForestTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityHigh;
		return alleles;
	}

	public static IAllele[] getForestRainResistTemplate() {
		IAllele[] alleles = getForestTemplate();
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
		return alleles;
	}

	public static IAllele[] getMeadowsTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMeadows;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
		return alleles;
	}

	public static IAllele[] getCommonTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesCommon;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		return alleles;
	}

	public static IAllele[] getCultivatedTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesCultivated;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedFast;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
		return alleles;
	}

	// / NOBLE BRANCH

	public static IAllele[] getNobleTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesNoble;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlow;
		return alleles;
	}

	public static IAllele[] getMajesticTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMajestic;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortened;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
		return alleles;
	}

	public static IAllele[] getImperialTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesImperial;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectBeatific;
		return alleles;
	}

	// / INDUSTRIOUS BRANCH

	public static IAllele[] getDiligentTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesDiligent;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlow;
		return alleles;
	}

	public static IAllele[] getUnwearyTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesUnweary;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortened;
		return alleles;
	}

	public static IAllele[] getIndustriousTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesIndustrious;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFast;
		return alleles;
	}

	// / HEROIC BRANCH

	public static IAllele[] getSteadfastTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesSteadfast;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		return alleles;
	}

	public static IAllele[] getValiantTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesValiant;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		return alleles;
	}

	public static IAllele[] getHeroicTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesHeroic;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectHeroic;
		return alleles;
	}

	// / INFERNAL BRANCH

	public static IAllele[] getBranchInfernalTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown2;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersNether;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringAverage;
		return alleles;
	}

	public static IAllele[] getSinisterTemplate() {
		IAllele[] alleles = getBranchInfernalTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesSinister;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectAggressive;
		return alleles;
	}

	public static IAllele[] getFiendishTemplate() {
		IAllele[] alleles = getBranchInfernalTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesFiendish;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectAggressive;
		return alleles;
	}

	public static IAllele[] getDemonicTemplate() {
		IAllele[] alleles = getBranchInfernalTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesDemonic;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectIgnition;
		return alleles;
	}

	// / AUSTERE BRANCH

	public static IAllele[] getAustereBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersCacti;
		return alleles;
	}

	public static IAllele[] getModestTemplate() {
		IAllele[] alleles = getAustereBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesModest;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	public static IAllele[] getFrugalTemplate() {
		IAllele[] alleles = getAustereBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesFrugal;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		return alleles;
	}

	public static IAllele[] getAustereTemplate() {
		IAllele[] alleles = getAustereBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesAustere;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown2;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectCreeper;
		return alleles;
	}

	// / TROPICAL BRANCH
	public static IAllele[] getTropicalBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersJungle;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMiasmic;
		return alleles;
	}

	public static IAllele[] getTropicalTemplate() {
		IAllele[] alleles = getTropicalBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesTropical;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	public static IAllele[] getExoticTemplate() {
		IAllele[] alleles = getTropicalBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesExotic;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		return alleles;
	}

	public static IAllele[] getEdenicTemplate() {
		IAllele[] alleles = getTropicalBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesEdenic;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectExploration;
		return alleles;
	}

	// / END BRANCH
	public static IAllele[] getEndBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityLow;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersEnd;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMisanthrope;
		return alleles;
	}

	public static IAllele[] getEnderTemplate() {
		IAllele[] alleles = getEndBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesEnded;
		return alleles;
	}

	public static IAllele[] getSpectralTemplate() {
		IAllele[] alleles = getEndBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesSpectral;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectReanimation;
		return alleles;
	}

	public static IAllele[] getPhantasmalTemplate() {
		IAllele[] alleles = getEndBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesPhantasmal;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLongest;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectResurrection;
		return alleles;
	}

	// / FROZEN BRANCH
	public static IAllele[] getFrozenBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersSnow;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectGlacial;
		return alleles;
	}

	public static IAllele[] getWintryTemplate() {
		IAllele[] alleles = getFrozenBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesWintry;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
		return alleles;
	}

	public static IAllele[] getIcyTemplate() {
		IAllele[] alleles = getFrozenBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesIcy;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	public static IAllele[] getGlacialTemplate() {
		IAllele[] alleles = getFrozenBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesGlacial;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	// / VENGEFUL BRANCH
	public static IAllele[] getVengefulBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLargest;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectRadioactive;
		return alleles;
	}

	public static IAllele[] getVindictiveTemplate() {
		IAllele[] alleles = getVengefulBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesVindictive;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		return alleles;
	}

	public static IAllele[] getVengefulTemplate() {
		IAllele[] alleles = getVengefulBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesVengeful;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedNorm;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLonger;
		return alleles;
	}

	public static IAllele[] getAvengingTemplate() {
		IAllele[] alleles = getVengefulBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesAvenging;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlowest;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLongest;
		return alleles;
	}

	// / REDDENED BRANCH
	public static IAllele[] getReddenedBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		return alleles;
	}

	public static IAllele[] getDarkenedTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesDarkened;
		return alleles;
	}

	public static IAllele[] getReddenedTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesReddened;
		return alleles;
	}

	public static IAllele[] getOmegaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesOmega;
		return alleles;
	}

	// / FESTIVE BRANCH
	public static IAllele[] getFestiveBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		return alleles;
	}

	public static IAllele[] getLeporineTemplate() {
		IAllele[] alleles = getFestiveBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesLeporine;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectFestiveEaster;
		return alleles;
	}

	public static IAllele[] getMerryTemplate() {
		IAllele[] alleles = getFestiveBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMerry;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectSnowing;
		return alleles;
	}

	public static IAllele[] getTipsyTemplate() {
		IAllele[] alleles = getFestiveBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesTipsy;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectDrunkard;
		return alleles;
	}
	
	public static IAllele[] getTrickyTemplate() {
		IAllele[] alleles = getFestiveBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesTricky;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersGourd;
		return alleles;
	}

	// / AGRARIAN BRANCH
	public static IAllele[] getAgrarianBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersWheat;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFaster;
		return alleles;
	}

	public static IAllele[] getRuralTemplate() {
		IAllele[] alleles = getAgrarianBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesRural;
		return alleles;
	}
	
	public static IAllele[] getFarmerlyTemplate() {
		IAllele[] alleles = getAgrarianBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesFarmerly;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
		return alleles;
	}
	
	public static IAllele[] getAgrarianTemplate() {
		IAllele[] alleles = getAgrarianBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesArgrarian;
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth2;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectFertile;
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarge;
		return alleles;
	}

	// / BOGGY BRANCH
	public static IAllele[] getBoggyBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersMushrooms;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringSlower;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		return alleles;
	}

	public static IAllele[] getMarshyTemplate() {
		IAllele[] alleles = getBoggyBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMarshy;
		return alleles;
	}
	
	public static IAllele[] getMiryTemplate() {
		IAllele[] alleles = getBoggyBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMiry;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityMaximum;
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		return alleles;
	}
	
	public static IAllele[] getBoggyTemplate() {
		IAllele[] alleles = getBoggyBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesBoggy;
		alleles[EnumBeeChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.NOCTURNAL.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectMycophilic;
		alleles[EnumBeeChromosome.TERRITORY.ordinal()] = Allele.territoryLarger;
		return alleles;
	}

	/* MONASTIC BRANCH */
	public static IAllele[] getMonasticBranchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumBeeChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumBeeChromosome.LIFESPAN.ordinal()] = Allele.lifespanLong;
		alleles[EnumBeeChromosome.FERTILITY.ordinal()] = Allele.fertilityLow;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFaster;
		alleles[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		alleles[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		alleles[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = Allele.boolTrue;
		alleles[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersWheat;
		return alleles;
	}

	public static IAllele[] getMonasticTemplate() {
		IAllele[] alleles = getMonasticBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesMonastic;
		return alleles;
	}

	public static IAllele[] getSecludedTemplate() {
		IAllele[] alleles = getMonasticBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesSecluded;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFastest;
		return alleles;
	}

	public static IAllele[] getHermiticTemplate() {
		IAllele[] alleles = getMonasticBranchTemplate();
		alleles[EnumBeeChromosome.SPECIES.ordinal()] = Allele.speciesHermitic;
		alleles[EnumBeeChromosome.FLOWERING.ordinal()] = Allele.floweringFastest;
		alleles[EnumBeeChromosome.EFFECT.ordinal()] = Allele.effectRepulsion;
		return alleles;
	}

}
