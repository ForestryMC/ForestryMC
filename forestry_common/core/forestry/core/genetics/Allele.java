/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ILegacyHandler;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class Allele implements IAllele {

	protected String uid;
	boolean isDominant;
	protected String name;

	public Allele(String uid, boolean isDominant) {
		this(uid, isDominant, false);
	}

	protected Allele(String uid, boolean isDominant, boolean skipRegister) {
		this.uid = uid;
		this.isDominant = isDominant;
		this.name = "allele."+uid;
		
		if (!skipRegister)
			AlleleManager.alleleRegistry.registerAllele(this);
	}

	@Override
	public String getUID() {
		return "forestry." + this.uid;
	}

	@Override
	public boolean isDominant() {
		return isDominant;
	}

	// / BEES // SPECIES
	// Common Branch
	public static AlleleSpecies speciesForest;
	public static AlleleSpecies speciesMeadows;
	public static AlleleSpecies speciesCommon;
	public static AlleleSpecies speciesCultivated;

	// Noble Branch
	public static AlleleSpecies speciesNoble;
	public static AlleleSpecies speciesMajestic;
	public static AlleleSpecies speciesImperial;

	// Industrious Branch
	public static AlleleSpecies speciesDiligent;
	public static AlleleSpecies speciesUnweary;
	public static AlleleSpecies speciesIndustrious;

	// Heroic Branch
	public static AlleleSpecies speciesSteadfast;
	public static AlleleSpecies speciesValiant;
	public static AlleleSpecies speciesHeroic;

	// Infernal Branch
	public static AlleleSpecies speciesSinister;
	public static AlleleSpecies speciesFiendish;
	public static AlleleSpecies speciesDemonic;

	// Austere Branch
	public static AlleleSpecies speciesModest;
	public static AlleleSpecies speciesFrugal;
	public static AlleleSpecies speciesAustere;

	// / Tropical Branch
	public static AlleleSpecies speciesTropical;
	public static AlleleSpecies speciesExotic;
	public static AlleleSpecies speciesEdenic;

	// End Branch
	public static AlleleSpecies speciesEnded;
	public static AlleleSpecies speciesSpectral;
	public static AlleleSpecies speciesPhantasmal;

	// Frozen Branch
	public static AlleleSpecies speciesWintry;
	public static AlleleSpecies speciesIcy;
	public static AlleleSpecies speciesGlacial;

	// Vengeful Branch
	public static AlleleSpecies speciesVindictive;
	public static AlleleSpecies speciesVengeful;
	public static AlleleSpecies speciesAvenging;

	// Reddened Branch (EE)
	public static AlleleSpecies speciesDarkened;
	public static AlleleSpecies speciesReddened;
	public static AlleleSpecies speciesOmega;

	// Festive branch
	public static AlleleSpecies speciesLeporine; // Easter
	public static AlleleSpecies speciesMerry; // Christmas
	public static AlleleSpecies speciesTipsy; // New Year
	// 35 Solstice
	public static AlleleSpecies speciesTricky; // Halloween
	// 37 Thanksgiving
	// 39 New Year

	// Agrarian branch
	public static AlleleSpecies speciesRural;
	// 41 Farmerly
	// 42 Agrarian

	// Boggy branch
	public static AlleleSpecies speciesMarshy;
	// 44 speciesMiry
	// 45 speciesBoggy

	// Monastic branch
	public static AlleleSpecies speciesMonastic;
	public static AlleleSpecies speciesSecluded;
	public static AlleleSpecies speciesHermitic;

	// / TREES // SPECIES 512 - 1023
	public static AlleleSpecies treeOak;
	public static AlleleSpecies treeBirch;
	public static AlleleSpecies treeSpruce;
	public static AlleleSpecies treePine;
	public static AlleleSpecies treeJungle;
	public static AlleleSpecies treeAcacia;
	public static AlleleSpecies treeDarkOak;
	
	public static AlleleSpecies treeLarch;
	public static AlleleSpecies treeTeak;
	public static AlleleSpecies treeDesertAcacia;
	public static AlleleSpecies treeLime;
	public static AlleleSpecies treeChestnut;
	public static AlleleSpecies treeWenge;
	public static AlleleSpecies treeBaobab;
	public static AlleleSpecies treeSequioa;
	public static AlleleSpecies treeGiganteum;

	public static AlleleSpecies treeKapok;
	public static AlleleSpecies treeEbony;
	public static AlleleSpecies treeMahogany;
	public static AlleleSpecies treeBalsa;
	public static AlleleSpecies treePapaya;
	public static AlleleSpecies treeWalnut;
	public static AlleleSpecies treeDate;
	public static AlleleSpecies treeCherry;

	public static AlleleSpecies treeWillow;
	public static AlleleSpecies treeSipiri;

	public static AlleleSpecies treeMahoe;
	public static AlleleSpecies treePoplar;

	public static AlleleSpecies treeLemon;
	public static AlleleSpecies treePlum;

	public static AlleleSpecies treeMaple;

	/// BUTTERFLIES // SPECIES
	// Moths
	public static AlleleSpecies mothBrimstone;
	public static AlleleSpecies mothLatticedHeath;
	public static AlleleSpecies mothAtlas;
	
	// Butterflies
	public static AlleleSpecies lepiCabbageWhite;
	public static AlleleSpecies lepiGlasswing;
	public static AlleleSpecies lepiEmeraldPeacock;
	public static AlleleSpecies lepiThoasSwallow;
	public static AlleleSpecies lepiCitrusSwallow;
	public static AlleleSpecies lepiZebraSwallow;
	public static AlleleSpecies lepiBlackSwallow;
	public static AlleleSpecies lepiDianaFrit;
	
	public static AlleleSpecies lepiSpeckledWood;
	public static AlleleSpecies lepiMadeiranSpeckledWood;
	public static AlleleSpecies lepiCanarySpeckledWood;
	
	public static AlleleSpecies lepiMenelausBlueMorpho;
	public static AlleleSpecies lepiPeleidesBlueMorpho;
	public static AlleleSpecies lepiRhetenorBlueMorpho;
	
	public static AlleleSpecies lepiBrimstone;
	public static AlleleSpecies lepiAurora;
	public static AlleleSpecies lepiPostillion;
	public static AlleleSpecies lepiPalaenoSulphur;
	public static AlleleSpecies lepiReseda;
	public static AlleleSpecies lepiSpringAzure;
	public static AlleleSpecies lepiGozoraAzure;
	public static AlleleSpecies lepiComma;
	public static AlleleSpecies lepiBatesia;
	public static AlleleSpecies lepiBlueWing;
	
	public static AlleleSpecies lepiMonarch;
	public static AlleleSpecies lepiBlueDuke;
	public static AlleleSpecies lepiGlassyTiger;
	public static AlleleSpecies lepiPostman;
	public static AlleleSpecies lepiSpicebush;
	public static AlleleSpecies lepiMalachite;
	public static AlleleSpecies lepiLLacewing;
	
	// / ALL // GENERIC
	public static Allele boolFalse;
	public static Allele boolTrue;

	public static Allele int1;
	public static Allele int2;
	public static Allele int3;
	public static Allele int4;
	public static Allele int5;
	public static Allele int6;
	public static Allele int7;
	public static Allele int8;
	public static Allele int9;
	public static Allele int10;

	// / BEES // SPEED 1100 - 1199
	public static Allele speedSlowest;
	public static Allele speedSlower;
	public static Allele speedSlow;
	public static Allele speedNorm;
	public static Allele speedFast;
	public static Allele speedFaster;
	public static Allele speedFastest;

	// / BEES // LIFESPAN 1200 - 1299
	public static Allele lifespanShortest;
	public static Allele lifespanShorter;
	public static Allele lifespanShort;
	public static Allele lifespanShortened;
	public static Allele lifespanNormal;
	public static Allele lifespanElongated;
	public static Allele lifespanLong;
	public static Allele lifespanLonger;
	public static Allele lifespanLongest;

	// / BEES // FERTILITY 1300 - 1349
	public static Allele fertilityLow;
	public static Allele fertilityNormal;
	public static Allele fertilityHigh;
	public static Allele fertilityMaximum;

	// / TREES // GROWTH PROVIDER 1350 - 1399
	public static Allele growthLightlevel;
	public static Allele growthAcacia;
	public static Allele growthTropical;

	// TREES FRUIT PROVIDERS
	public static Allele fruitNone;
	public static Allele fruitApple;
	public static Allele fruitCocoa;
	public static Allele fruitChestnut;
	public static Allele fruitCoconut;
	public static Allele fruitWalnut;
	public static Allele fruitCherry;
	public static Allele fruitDates;
	public static Allele fruitPapaya;
	public static Allele fruitLemon;
	public static Allele fruitPlum;
	public static Allele fruitJujube;

	// / TREES // HEIGHT 1400 - 1449
	public static Allele heightSmallest;
	public static Allele heightSmaller;
	public static Allele heightSmall;
	public static Allele heightAverage;
	public static Allele heightLarge;
	public static Allele heightLarger;
	public static Allele heightLargest;
	public static Allele heightGigantic;

	/// BUTTERFLIES // SIZE
	public static Allele sizeSmallest;
	public static Allele sizeSmaller;
	public static Allele sizeSmall;
	public static Allele sizeAverage;
	public static Allele sizeLarge;
	public static Allele sizeLarger;
	public static Allele sizeLargest;
	
	// / BOTH // TOLERANCE 1450 - 1499
	public static Allele toleranceNone;
	public static Allele toleranceBoth1;
	public static Allele toleranceBoth2;
	public static Allele toleranceBoth3;
	public static Allele toleranceBoth4;
	public static Allele toleranceBoth5;
	public static Allele toleranceUp1;
	public static Allele toleranceUp2;
	public static Allele toleranceUp3;
	public static Allele toleranceUp4;
	public static Allele toleranceUp5;
	public static Allele toleranceDown1;
	public static Allele toleranceDown2;
	public static Allele toleranceDown3;
	public static Allele toleranceDown4;
	public static Allele toleranceDown5;

	// / BEES // FLOWER PROVIDERS 1500 - 1599
	public static Allele flowersVanilla;
	public static Allele flowersNether;
	public static Allele flowersCacti;
	public static Allele flowersMushrooms;
	public static Allele flowersEnd;
	public static Allele flowersJungle;
	public static Allele flowersSnow;
	public static Allele flowersWheat;
	public static Allele flowersGourd;

	// / TREES // FERTILITY 1600 - 1649
	public static Allele saplingsLowest;
	public static Allele saplingsLower;
	public static Allele saplingsLow;
	public static Allele saplingsAverage;
	public static Allele saplingsHigh;
	public static Allele saplingsHigher;
	public static Allele saplingsHighest;

	// / TREES // YIELD 1650 - 1699
	public static Allele yieldLowest;
	public static Allele yieldLower;
	public static Allele yieldLow;
	public static Allele yieldAverage;
	public static Allele yieldHigh;
	public static Allele yieldHigher;
	public static Allele yieldHighest;

	// TREES // SAPPINESS
	public static Allele sappinessLowest;
	public static Allele sappinessLower;
	public static Allele sappinessLow;
	public static Allele sappinessAverage;
	public static Allele sappinessHigh;
	public static Allele sappinessHigher;
	public static Allele sappinessHighest;

	// TREES // MATURATION TIME
	public static Allele maturationSlowest;
	public static Allele maturationSlower;
	public static Allele maturationSlow;
	public static Allele maturationAverage;
	public static Allele maturationFast;
	public static Allele maturationFaster;
	public static Allele maturationFastest;

	// / BEES // FLOWER GROWTH 1700 - 1749
	public static Allele floweringSlowest;
	public static Allele floweringSlower;
	public static Allele floweringSlow;
	public static Allele floweringAverage;
	public static Allele floweringFast;
	public static Allele floweringFaster;
	public static Allele floweringFastest;
	public static Allele floweringMaximum;

	// / BOTH // TERRITORY 1750 - 1799
	public static Allele territoryDefault;
	public static Allele territoryLarge;
	public static Allele territoryLarger;
	public static Allele territoryLargest;

	// / BEES // EFFECTS 1800 - 1899
	public static Allele effectNone;
	public static Allele effectAggressive;
	public static Allele effectHeroic;
	public static Allele effectBeatific;
	public static Allele effectMiasmic;
	public static Allele effectMisanthrope;
	public static Allele effectGlacial;
	public static Allele effectRadioactive;
	public static Allele effectCreeper;
	public static Allele effectIgnition;
	public static Allele effectExploration;
	public static Allele effectFestiveEaster;
	public static Allele effectSnowing;
	public static Allele effectDrunkard;
	public static Allele effectReanimation;
	public static Allele effectResurrection;
	public static Allele effectRepulsion;

	// / TREES // EFFECTS
	public static Allele leavesNone;

	// / BUTTERFLIES // EFFECTS 
	public static Allele butterflyNone;
	
	// These are "secondary" plant attributes, i.e. the tree can double as one.
	public static Allele plantTypeNone;
	public static Allele plantTypePlains;
	public static Allele plantTypeDesert;
	public static Allele plantTypeBeach;
	public static Allele plantTypeCave;
	public static Allele plantTypeWater;
	public static Allele plantTypeNether;
	public static Allele plantTypeCrop;

	public static void initialize() {
		// ALL // GENERIC
		boolFalse = new AlleleBoolean("boolFalse", false);
		boolTrue = new AlleleBoolean("boolTrue", true);

		int1 = new AlleleInteger("i1d", 1, true);
		int2 = new AlleleInteger("i2d", 2, true);
		int3 = new AlleleInteger("i3d", 3, true);
		int4 = new AlleleInteger("i4d", 4, true);
		int5 = new AlleleInteger("i5d", 5, true);
		int6 = new AlleleInteger("i6d", 6, true);
		int7 = new AlleleInteger("i7d", 7, true);
		int8 = new AlleleInteger("i8d", 8, true);
		int9 = new AlleleInteger("i9d", 9, true);
		int10 = new AlleleInteger("i10d", 10, true);

		// BEES // SPEED
		speedSlowest = new AlleleFloat("speedSlowest", 0.3f, true).setName("gui.slowest");
		speedSlower = new AlleleFloat("speedSlower", 0.6f, true).setName("gui.slower");
		speedSlow = new AlleleFloat("speedSlow", 0.8f, true).setName("gui.slow");
		speedNorm = new AlleleFloat("speedNorm", 1.0f).setName("gui.normal");
		speedFast = new AlleleFloat("speedFast", 1.2f, true).setName("gui.fast");
		speedFaster = new AlleleFloat("speedFaster", 1.4f).setName("gui.faster");
		speedFastest = new AlleleFloat("speedFastest", 1.7f).setName("gui.fastest");

		// BEES // LIFESPAN
		lifespanShortest = new AlleleInteger("lifespanShortest", 10, false).setName("gui.shortestlife");
		lifespanShorter = new AlleleInteger("lifespanShorter", 20, true).setName("gui.shorterlife");
		lifespanShort = new AlleleInteger("lifespanShort", 30, true).setName("gui.shortlife");
		lifespanShortened = new AlleleInteger("lifespanShortened", 35, true).setName("gui.shortenedlife");
		lifespanNormal = new AlleleInteger("lifespanNormal", 40).setName("gui.normallife");
		lifespanElongated = new AlleleInteger("lifespanElongated", 45, true).setName("gui.elongatedlife");
		lifespanLong = new AlleleInteger("lifespanLong", 50).setName("gui.longlife");
		lifespanLonger = new AlleleInteger("lifespanLonger", 60).setName("gui.longerlife");
		lifespanLongest = new AlleleInteger("lifespanLongest", 70).setName("gui.longestlife");

		// BEES // FERTILITY
		fertilityLow = new AlleleInteger("fertilityLow", 1, true);
		fertilityNormal = new AlleleInteger("fertilityNormal", 2, true);
		fertilityHigh = new AlleleInteger("fertilityHigh", 3);
		fertilityMaximum = new AlleleInteger("fertilityMaximum", 4);

		// TREES // HEIGHT
		heightSmallest = new AlleleFloat("heightSmallest", 0.25f).setName("gui.smallest");
		heightSmaller = new AlleleFloat("heightSmaller", 0.5f).setName("gui.smaller");
		heightSmall = new AlleleFloat("heightSmall", 0.75f).setName("gui.small");
		heightAverage = new AlleleFloat("heightMax10", 1.0f).setName("gui.average");
		heightLarge = new AlleleFloat("heightLarge", 1.25f).setName("gui.large");
		heightLarger = new AlleleFloat("heightLarger", 1.5f).setName("gui.larger");
		heightLargest = new AlleleFloat("heightLargest", 1.75f).setName("gui.largest");
		heightGigantic = new AlleleFloat("heightGigantic", 2.0f).setName("gui.gigantic");

		// BUTTERFLIES // SIZE
		sizeSmallest = new AlleleFloat("sizeSmallest", 0.3f).setName("gui.smallest");
		sizeSmaller = new AlleleFloat("sizeSmaller", 0.4f).setName("gui.smaller");
		sizeSmall = new AlleleFloat("sizeSmall", 0.5f).setName("gui.small");
		sizeAverage = new AlleleFloat("sizeAverage", 0.6f).setName("gui.average");
		sizeLarge = new AlleleFloat("sizeLarge", 0.75f).setName("gui.large");
		sizeLarger = new AlleleFloat("sizeLarger", 0.9f).setName("gui.larger");
		sizeLargest = new AlleleFloat("sizeLargest", 1.0f).setName("gui.largest");

		// BEES // TOLERANCE
		toleranceNone = new AlleleTolerance("toleranceNone", EnumTolerance.NONE);
		toleranceBoth1 = new AlleleTolerance("toleranceBoth1", EnumTolerance.BOTH_1, true);
		toleranceBoth2 = new AlleleTolerance("toleranceBoth2", EnumTolerance.BOTH_2);
		toleranceBoth3 = new AlleleTolerance("toleranceBoth3", EnumTolerance.BOTH_3);
		toleranceBoth4 = new AlleleTolerance("toleranceBoth4", EnumTolerance.BOTH_4);
		toleranceBoth5 = new AlleleTolerance("toleranceBoth5", EnumTolerance.BOTH_5);
		toleranceUp1 = new AlleleTolerance("toleranceUp1", EnumTolerance.UP_1, true);
		toleranceUp2 = new AlleleTolerance("toleranceUp2", EnumTolerance.UP_2);
		toleranceUp3 = new AlleleTolerance("toleranceUp3", EnumTolerance.UP_3);
		toleranceUp4 = new AlleleTolerance("toleranceUp4", EnumTolerance.UP_4);
		toleranceUp5 = new AlleleTolerance("toleranceUp5", EnumTolerance.UP_5);
		toleranceDown1 = new AlleleTolerance("toleranceDown1", EnumTolerance.DOWN_1, true);
		toleranceDown2 = new AlleleTolerance("toleranceDown2", EnumTolerance.DOWN_2);
		toleranceDown3 = new AlleleTolerance("toleranceDown3", EnumTolerance.DOWN_3);
		toleranceDown4 = new AlleleTolerance("toleranceDown4", EnumTolerance.DOWN_4);
		toleranceDown5 = new AlleleTolerance("toleranceDown5", EnumTolerance.DOWN_5);

		// TREES // FERTILITY
		saplingsLowest = new AlleleFloat("saplingsLowest", 0.01f, true).setName("gui.lowest");
		saplingsLower = new AlleleFloat("saplingsLower", 0.025f, true).setName("gui.lower");
		saplingsLow = new AlleleFloat("saplingsLow", 0.035f, true).setName("gui.low");
		saplingsAverage = new AlleleFloat("saplingsDefault", 0.05f, true).setName("gui.average");
		saplingsHigh = new AlleleFloat("saplingsDouble", 0.1f, true).setName("gui.high");
		saplingsHigher = new AlleleFloat("saplingsTriple", 0.2f, true).setName("gui.higher");
		saplingsHighest = new AlleleFloat("saplingsHighest", 0.3f, true).setName("gui.highest");

		// TREES // YIELD
		yieldLowest = new AlleleFloat("yieldLowest", 0.025f, true).setName("gui.lowest");
		yieldLower = new AlleleFloat("yieldLower", 0.05f, true).setName("gui.lower");
		yieldLow = new AlleleFloat("yieldLow", 0.1f, true).setName("gui.low");
		yieldAverage = new AlleleFloat("yieldDefault", 0.2f, true).setName("gui.average");
		yieldHigh = new AlleleFloat("yieldHigh", 0.3f, false).setName("gui.high");
		yieldHigher = new AlleleFloat("yieldHigher", 0.35f, false).setName("gui.higher");
		yieldHighest = new AlleleFloat("yieldHighest", 0.4f, false).setName("gui.highest");

		// TREES // SAPPINESS
		sappinessLowest = new AlleleFloat("sappinessLowest", 0.1f, true).setName("gui.lowest");
		sappinessLower = new AlleleFloat("sappinessLower", 0.2f, true).setName("gui.lower");
		sappinessLow = new AlleleFloat("sappinessLow", 0.3f, true).setName("gui.low");
		sappinessAverage = new AlleleFloat("sappinessAverage", 0.4f, true).setName("gui.average");
		sappinessHigh = new AlleleFloat("sappinessHigh", 0.6f, true).setName("gui.high");
		sappinessHigher = new AlleleFloat("sappinessHigher", 0.8f, false).setName("gui.higher");
		sappinessHighest = new AlleleFloat("sappinessHighest", 1.0f, false).setName("gui.highest");

		// TREES // MATURATION TIME
		maturationSlowest = new AlleleInteger("maturationSlowest", 10, true).setName("gui.slowest");
		maturationSlower = new AlleleInteger("maturationSlower", 7).setName("gui.slower");
		maturationSlow = new AlleleInteger("maturationSlow", 5, true).setName("gui.slow");
		maturationAverage = new AlleleInteger("maturationAverage", 4).setName("gui.average");
		maturationFast = new AlleleInteger("maturationFast", 3).setName("gui.fast");
		maturationFaster = new AlleleInteger("maturationFaster", 2).setName("gui.faster");
		maturationFastest = new AlleleInteger("maturationFastest", 1).setName("gui.fastest");

		// BEES // FLOWER GROWTH
		floweringSlowest = new AlleleInteger("floweringSlowest", 5, true).setName("gui.slowest");
		floweringSlower = new AlleleInteger("floweringSlower", 10).setName("gui.slower");
		floweringSlow = new AlleleInteger("floweringSlow", 15).setName("gui.slow");
		floweringAverage = new AlleleInteger("floweringAverage", 20).setName("gui.average");
		floweringFast = new AlleleInteger("floweringFast", 25).setName("gui.fast");
		floweringFaster = new AlleleInteger("floweringFaster", 30).setName("gui.faster");
		floweringFastest = new AlleleInteger("floweringFastest", 35).setName("gui.fastest");
		floweringMaximum = new AlleleInteger("floweringMaximum", 99, true).setName("gui.maximum");

		// BOTH // TERRITORY
		territoryDefault = new AlleleArea("territoryDefault", new int[] {9, 6, 9}).setName("gui.average");
		territoryLarge = new AlleleArea("territoryLarge", new int[] {11, 8, 11}).setName("gui.large");
		territoryLarger = new AlleleArea("territoryLarger", new int[] {13, 12, 13}).setName("gui.larger");
		territoryLargest = new AlleleArea("territoryLargest", new int[] {15, 13, 15}).setName("gui.largest");

		// TREES // PLANTS
		plantTypeNone = new AllelePlantType("plantTypeNone", EnumSet.noneOf(EnumPlantType.class), true);
		plantTypePlains = new AllelePlantType("plantTypePlains", EnumPlantType.Plains);
		;
		plantTypeDesert = new AllelePlantType("plantTypeDesert", EnumPlantType.Desert);
		plantTypeBeach = new AllelePlantType("plantTypeBeach", EnumPlantType.Beach);
		plantTypeCave = new AllelePlantType("plantTypeCave", EnumPlantType.Cave);
		plantTypeWater = new AllelePlantType("plantTypeWater", EnumPlantType.Water);
		plantTypeNether = new AllelePlantType("plantTypeNether", EnumPlantType.Nether);
		plantTypeCrop = new AllelePlantType("plantTypeCrop", EnumPlantType.Crop);

		// LEGACY MAPPINGS
		ILegacyHandler legacy = (ILegacyHandler) AlleleManager.alleleRegistry;

		legacy.registerLegacyMapping(0, "forestry.speciesForest");
		legacy.registerLegacyMapping(1, "forestry.speciesMeadows");
		legacy.registerLegacyMapping(2, "forestry.speciesCommon");
		legacy.registerLegacyMapping(3, "forestry.speciesCultivated");

		legacy.registerLegacyMapping(4, "forestry.speciesNoble");
		legacy.registerLegacyMapping(5, "forestry.speciesMajestic");
		legacy.registerLegacyMapping(6, "forestry.speciesImperial");

		legacy.registerLegacyMapping(7, "forestry.speciesDiligent");
		legacy.registerLegacyMapping(8, "forestry.speciesUnweary");
		legacy.registerLegacyMapping(9, "forestry.speciesIndustrious");

		legacy.registerLegacyMapping(10, "forestry.speciesSteadfast");
		legacy.registerLegacyMapping(11, "forestry.speciesValiant");
		legacy.registerLegacyMapping(12, "forestry.speciesHeroic");

		legacy.registerLegacyMapping(13, "forestry.speciesSinister");
		legacy.registerLegacyMapping(14, "forestry.speciesFiendish");
		legacy.registerLegacyMapping(15, "forestry.speciesDemonic");

		legacy.registerLegacyMapping(16, "forestry.speciesModest");
		legacy.registerLegacyMapping(17, "forestry.speciesFrugal");
		legacy.registerLegacyMapping(18, "forestry.speciesAustere");

		legacy.registerLegacyMapping(19, "forestry.speciesTropical");
		legacy.registerLegacyMapping(20, "forestry.speciesExotic");
		legacy.registerLegacyMapping(21, "forestry.speciesEdenic");

		legacy.registerLegacyMapping(22, "forestry.speciesEnded");

		legacy.registerLegacyMapping(25, "forestry.speciesWintry");

		legacy.registerLegacyMapping(28, "forestry.speciesVindictive");
		legacy.registerLegacyMapping(29, "forestry.speciesVengeful");
		legacy.registerLegacyMapping(30, "forestry.speciesAvenging");

		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_DARKENED, "forestry.speciesDarkened");
		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_REDDENED, "forestry.speciesReddened");
		legacy.registerLegacyMapping(Defaults.ID_BEE_SPECIES_OMEGA, "forestry.speciesOmega");

		legacy.registerLegacyMapping(34, "forestry.speciesLeporine");

		legacy.registerLegacyMapping(40, "forestry.speciesRural");

		legacy.registerLegacyMapping(43, "forestry.speciesMarshy");

		// Flowers
		legacy.registerLegacyMapping(1500, "forestry.flowersVanilla");
		legacy.registerLegacyMapping(1501, "forestry.flowersNether");
		legacy.registerLegacyMapping(1502, "forestry.flowersCacti");
		legacy.registerLegacyMapping(1503, "forestry.flowersMushrooms");
		legacy.registerLegacyMapping(1504, "forestry.flowersEnd");
		legacy.registerLegacyMapping(1505, "forestry.flowersJungle");
		legacy.registerLegacyMapping(1506, "forestry.flowersSnow");
		legacy.registerLegacyMapping(1507, "forestry.flowersWheat");

		// Effects
		legacy.registerLegacyMapping(1800, "forestry.effectNone");
		legacy.registerLegacyMapping(1801, "forestry.effectAggressive");
		legacy.registerLegacyMapping(1802, "forestry.effectHeroic");
		legacy.registerLegacyMapping(1803, "forestry.effectBeatific");
		legacy.registerLegacyMapping(1804, "forestry.effectMiasmic");
		legacy.registerLegacyMapping(1805, "forestry.effectMisanthrope");
		legacy.registerLegacyMapping(1806, "forestry.effectGlacial");
		legacy.registerLegacyMapping(1807, "forestry.effectRadioactive");
		legacy.registerLegacyMapping(1808, "forestry.effectCreeper");
		legacy.registerLegacyMapping(1809, "forestry.effectIgnition");
		legacy.registerLegacyMapping(1810, "forestry.effectExploration");
		legacy.registerLegacyMapping(1811, "forestry.effectFestiveEaster");

		// Generic
		legacy.registerLegacyMapping(1024, "forestry.boolFalse");
		legacy.registerLegacyMapping(1025, "forestry.boolTrue");

		// Speed
		legacy.registerLegacyMapping(1100, "forestry.speedSlowest");
		legacy.registerLegacyMapping(1101, "forestry.speedSlower");
		legacy.registerLegacyMapping(1102, "forestry.speedSlow");
		legacy.registerLegacyMapping(1103, "forestry.speedNorm");
		legacy.registerLegacyMapping(1104, "forestry.speedFast");
		legacy.registerLegacyMapping(1105, "forestry.speedFaster");
		legacy.registerLegacyMapping(1106, "forestry.speedFastest");

		// Lifespan
		legacy.registerLegacyMapping(1200, "forestry.lifespanShortest");
		legacy.registerLegacyMapping(1201, "forestry.lifespanShorter");
		legacy.registerLegacyMapping(1202, "forestry.lifespanShort");
		legacy.registerLegacyMapping(1203, "forestry.lifespanShortened");
		legacy.registerLegacyMapping(1204, "forestry.lifespanNormal");
		legacy.registerLegacyMapping(1205, "forestry.lifespanElongated");
		legacy.registerLegacyMapping(1206, "forestry.lifespanLong");
		legacy.registerLegacyMapping(1207, "forestry.lifespanLonger");
		legacy.registerLegacyMapping(1208, "forestry.lifespanLongest");

		// Fertility
		legacy.registerLegacyMapping(1300, "forestry.fertilityLow");
		legacy.registerLegacyMapping(1301, "forestry.fertilityNormal");
		legacy.registerLegacyMapping(1302, "forestry.fertilityHigh");
		legacy.registerLegacyMapping(1303, "forestry.fertilityMaximum");

		// Tolerance
		legacy.registerLegacyMapping(1450, "forestry.toleranceNone");
		legacy.registerLegacyMapping(1451, "forestry.toleranceBoth1");
		legacy.registerLegacyMapping(1452, "forestry.toleranceBoth2");
		legacy.registerLegacyMapping(1453, "forestry.toleranceBoth3");
		legacy.registerLegacyMapping(1454, "forestry.toleranceBoth4");
		legacy.registerLegacyMapping(1455, "forestry.toleranceBoth5");
		legacy.registerLegacyMapping(1456, "forestry.toleranceUp1");
		legacy.registerLegacyMapping(1457, "forestry.toleranceUp2");
		legacy.registerLegacyMapping(1458, "forestry.toleranceUp3");
		legacy.registerLegacyMapping(1459, "forestry.toleranceUp4");
		legacy.registerLegacyMapping(1460, "forestry.toleranceUp5");
		legacy.registerLegacyMapping(1461, "forestry.toleranceDown1");
		legacy.registerLegacyMapping(1462, "forestry.toleranceDown2");
		legacy.registerLegacyMapping(1463, "forestry.toleranceDown3");
		legacy.registerLegacyMapping(1464, "forestry.toleranceDown4");
		legacy.registerLegacyMapping(1465, "forestry.toleranceDown5");

		// Flower growth
		legacy.registerLegacyMapping(1700, "forestry.floweringSlowest");
		legacy.registerLegacyMapping(1701, "forestry.floweringSlower");
		legacy.registerLegacyMapping(1702, "forestry.floweringSlow");
		legacy.registerLegacyMapping(1710, "forestry.floweringMaximum");

		// Territory
		legacy.registerLegacyMapping(1750, "forestry.territoryDefault");
		legacy.registerLegacyMapping(1751, "forestry.territoryLarge");
		legacy.registerLegacyMapping(1752, "forestry.territoryLarger");
		legacy.registerLegacyMapping(1753, "forestry.territoryLargest");

	}

	@Override
	public String getName() {
		return StringUtil.localize(name);
	}
	
	public Allele setName(String string) {
		name = string;
		return this;
	}
}
