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
package forestry.core.genetics;

import java.util.EnumSet;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ILegacyHandler;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.core.config.Defaults;
import forestry.core.utils.StringUtil;

public class Allele implements IAllele {

	protected final String uid;
	protected final boolean isDominant;
	protected String name;

	public Allele(String uid, boolean isDominant) {
		this(uid, isDominant, false);
	}

	protected Allele(String uid, boolean isDominant, boolean skipRegister) {
		this.uid = uid;
		this.isDominant = isDominant;
		this.name = "allele." + uid;
		
		if (!skipRegister) {
			AlleleManager.alleleRegistry.registerAllele(this);
		}
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
	public static IAlleleBeeSpecies speciesForest;
	public static IAlleleBeeSpecies speciesMeadows;
	public static IAlleleBeeSpecies speciesCommon;
	public static IAlleleBeeSpecies speciesCultivated;

	// Noble Branch
	public static IAlleleBeeSpecies speciesNoble;
	public static IAlleleBeeSpecies speciesMajestic;
	public static IAlleleBeeSpecies speciesImperial;

	// Industrious Branch
	public static IAlleleBeeSpecies speciesDiligent;
	public static IAlleleBeeSpecies speciesUnweary;
	public static IAlleleBeeSpecies speciesIndustrious;

	// Heroic Branch
	public static IAlleleBeeSpecies speciesSteadfast;
	public static IAlleleBeeSpecies speciesValiant;
	public static IAlleleBeeSpecies speciesHeroic;

	// Infernal Branch
	public static IAlleleBeeSpecies speciesSinister;
	public static IAlleleBeeSpecies speciesFiendish;
	public static IAlleleBeeSpecies speciesDemonic;

	// Austere Branch
	public static IAlleleBeeSpecies speciesModest;
	public static IAlleleBeeSpecies speciesFrugal;
	public static IAlleleBeeSpecies speciesAustere;

	// / Tropical Branch
	public static IAlleleBeeSpecies speciesTropical;
	public static IAlleleBeeSpecies speciesExotic;
	public static IAlleleBeeSpecies speciesEdenic;

	// End Branch
	public static IAlleleBeeSpecies speciesEnded;
	public static IAlleleBeeSpecies speciesSpectral;
	public static IAlleleBeeSpecies speciesPhantasmal;

	// Frozen Branch
	public static IAlleleBeeSpecies speciesWintry;
	public static IAlleleBeeSpecies speciesIcy;
	public static IAlleleBeeSpecies speciesGlacial;

	// Vengeful Branch
	public static IAlleleBeeSpecies speciesVindictive;
	public static IAlleleBeeSpecies speciesVengeful;
	public static IAlleleBeeSpecies speciesAvenging;

	// Reddened Branch (EE)
	public static IAlleleBeeSpecies speciesDarkened;
	public static IAlleleBeeSpecies speciesReddened;
	public static IAlleleBeeSpecies speciesOmega;

	// Festive branch
	public static IAlleleBeeSpecies speciesLeporine; // Easter
	public static IAlleleBeeSpecies speciesMerry; // Christmas
	public static IAlleleBeeSpecies speciesTipsy; // New Year
	// 35 Solstice
	public static IAlleleBeeSpecies speciesTricky; // Halloween
	// 37 Thanksgiving
	// 39 New Year

	// Agrarian branch
	public static IAlleleBeeSpecies speciesRural;
	public static IAlleleBeeSpecies speciesFarmerly;
	public static IAlleleBeeSpecies speciesArgrarian;

	// Boggy branch
	public static IAlleleBeeSpecies speciesMarshy;
	public static IAlleleBeeSpecies speciesMiry;
	public static IAlleleBeeSpecies speciesBoggy;

	// Monastic branch
	public static IAlleleBeeSpecies speciesMonastic;
	public static IAlleleBeeSpecies speciesSecluded;
	public static IAlleleBeeSpecies speciesHermitic;

	// / TREES // SPECIES 512 - 1023
	public static IAlleleTreeSpecies treeOak;
	public static IAlleleTreeSpecies treeBirch;
	public static IAlleleTreeSpecies treeSpruce;
	public static IAlleleTreeSpecies treePine;
	public static IAlleleTreeSpecies treeJungle;
	public static IAlleleTreeSpecies treeAcacia;
	public static IAlleleTreeSpecies treeDarkOak;
	
	public static IAlleleTreeSpecies treeLarch;
	public static IAlleleTreeSpecies treeTeak;
	public static IAlleleTreeSpecies treeDesertAcacia;
	public static IAlleleTreeSpecies treeLime;
	public static IAlleleTreeSpecies treeChestnut;
	public static IAlleleTreeSpecies treeWenge;
	public static IAlleleTreeSpecies treeBaobab;
	public static IAlleleTreeSpecies treeSequioa;
	public static IAlleleTreeSpecies treeGiganteum;

	public static IAlleleTreeSpecies treeKapok;
	public static IAlleleTreeSpecies treeEbony;
	public static IAlleleTreeSpecies treeMahogany;
	public static IAlleleTreeSpecies treeBalsa;
	public static IAlleleTreeSpecies treePapaya;
	public static IAlleleTreeSpecies treeWalnut;
	public static IAlleleTreeSpecies treeDate;
	public static IAlleleTreeSpecies treeCherry;

	public static IAlleleTreeSpecies treeWillow;
	public static IAlleleTreeSpecies treeSipiri;

	public static IAlleleTreeSpecies treeMahoe;
	public static IAlleleTreeSpecies treePoplar;

	public static IAlleleTreeSpecies treeLemon;
	public static IAlleleTreeSpecies treePlum;

	public static IAlleleTreeSpecies treeMaple;

	public static IAlleleTreeSpecies treeIpe;
	public static IAlleleTreeSpecies treePadauk;
	public static IAlleleTreeSpecies treeCocobolo;
	public static IAlleleTreeSpecies treeZebrawood;
	
	/// BUTTERFLIES // SPECIES
	// Moths
	public static IAlleleButterflySpecies mothBrimstone;
	public static IAlleleButterflySpecies mothLatticedHeath;
	public static IAlleleButterflySpecies mothAtlas;
	
	// Butterflies
	public static IAlleleButterflySpecies lepiCabbageWhite;
	public static IAlleleButterflySpecies lepiGlasswing;
	public static IAlleleButterflySpecies lepiEmeraldPeacock;
	public static IAlleleButterflySpecies lepiThoasSwallow;
	public static IAlleleButterflySpecies lepiCitrusSwallow;
	public static IAlleleButterflySpecies lepiZebraSwallow;
	public static IAlleleButterflySpecies lepiBlackSwallow;
	public static IAlleleButterflySpecies lepiDianaFrit;
	
	public static IAlleleButterflySpecies lepiSpeckledWood;
	public static IAlleleButterflySpecies lepiMadeiranSpeckledWood;
	public static IAlleleButterflySpecies lepiCanarySpeckledWood;
	
	public static IAlleleButterflySpecies lepiMenelausBlueMorpho;
	public static IAlleleButterflySpecies lepiPeleidesBlueMorpho;
	public static IAlleleButterflySpecies lepiRhetenorBlueMorpho;
	
	public static IAlleleButterflySpecies lepiBrimstone;
	public static IAlleleButterflySpecies lepiAurora;
	public static IAlleleButterflySpecies lepiPostillion;
	public static IAlleleButterflySpecies lepiPalaenoSulphur;
	public static IAlleleButterflySpecies lepiReseda;
	public static IAlleleButterflySpecies lepiSpringAzure;
	public static IAlleleButterflySpecies lepiGozoraAzure;
	public static IAlleleButterflySpecies lepiComma;
	public static IAlleleButterflySpecies lepiBatesia;
	public static IAlleleButterflySpecies lepiBlueWing;
	
	public static IAlleleButterflySpecies lepiMonarch;
	public static IAlleleButterflySpecies lepiBlueDuke;
	public static IAlleleButterflySpecies lepiGlassyTiger;
	public static IAlleleButterflySpecies lepiPostman;
	public static IAlleleButterflySpecies lepiSpicebush;
	public static IAlleleButterflySpecies lepiMalachite;
	public static IAlleleButterflySpecies lepiLLacewing;
	
	// / ALL // GENERIC
	public static final Allele boolFalse = new AlleleBoolean("boolFalse", false);
	public static final Allele boolTrue = new AlleleBoolean("boolTrue", true);

	public static final Allele int1 = new AlleleInteger("i1d", 1, true);
	public static final Allele int2 = new AlleleInteger("i2d", 2, true);
	public static final Allele int3 = new AlleleInteger("i3d", 3, true);
	public static final Allele int4 = new AlleleInteger("i4d", 4, true);
	public static final Allele int5 = new AlleleInteger("i5d", 5, true);
	public static final Allele int6 = new AlleleInteger("i6d", 6, true);
	public static final Allele int7 = new AlleleInteger("i7d", 7, true);
	public static final Allele int8 = new AlleleInteger("i8d", 8, true);
	public static final Allele int9 = new AlleleInteger("i9d", 9, true);
	public static final Allele int10 = new AlleleInteger("i10d", 10, true);

	// / BEES // SPEED 1100 - 1199
	public static final Allele speedSlowest = new AlleleFloat("speedSlowest", 0.3f, true).setName("speed", "slowest");
	public static final Allele speedSlower = new AlleleFloat("speedSlower", 0.6f, true).setName("speed", "slower");
	public static final Allele speedSlow = new AlleleFloat("speedSlow", 0.8f, true).setName("speed", "slow");
	public static final Allele speedNorm = new AlleleFloat("speedNorm", 1.0f).setName("speed", "normal");
	public static final Allele speedFast = new AlleleFloat("speedFast", 1.2f, true).setName("speed", "fast");
	public static final Allele speedFaster = new AlleleFloat("speedFaster", 1.4f).setName("speed", "faster");
	public static final Allele speedFastest = new AlleleFloat("speedFastest", 1.7f).setName("speed", "fastest");

	// / BEES // LIFESPAN 1200 - 1299
	public static final Allele lifespanShortest = new AlleleInteger("lifespanShortest", 10, false).setName("gui.shortestlife");
	public static final Allele lifespanShorter = new AlleleInteger("lifespanShorter", 20, true).setName("gui.shorterlife");
	public static final Allele lifespanShort = new AlleleInteger("lifespanShort", 30, true).setName("gui.shortlife");
	public static final Allele lifespanShortened = new AlleleInteger("lifespanShortened", 35, true).setName("gui.shortenedlife");
	public static final Allele lifespanNormal = new AlleleInteger("lifespanNormal", 40).setName("gui.normallife");
	public static final Allele lifespanElongated = new AlleleInteger("lifespanElongated", 45, true).setName("gui.elongatedlife");
	public static final Allele lifespanLong = new AlleleInteger("lifespanLong", 50).setName("gui.longlife");
	public static final Allele lifespanLonger = new AlleleInteger("lifespanLonger", 60).setName("gui.longerlife");
	public static final Allele lifespanLongest = new AlleleInteger("lifespanLongest", 70).setName("gui.longestlife");

	// / BEES // FERTILITY 1300 - 1349
	public static final Allele fertilityLow = new AlleleInteger("fertilityLow", 1, true);
	public static final Allele fertilityNormal = new AlleleInteger("fertilityNormal", 2, true);
	public static final Allele fertilityHigh = new AlleleInteger("fertilityHigh", 3);
	public static final Allele fertilityMaximum = new AlleleInteger("fertilityMaximum", 4);

	// / TREES // GROWTH PROVIDER 1350 - 1399
	public static IAlleleGrowth growthLightlevel;
	public static IAlleleGrowth growthAcacia;
	public static IAlleleGrowth growthTropical;

	// TREES FRUIT PROVIDERS
	public static IAlleleFruit fruitNone;
	public static IAlleleFruit fruitApple;
	public static IAlleleFruit fruitCocoa;
	public static IAlleleFruit fruitChestnut;
	public static IAlleleFruit fruitCoconut;
	public static IAlleleFruit fruitWalnut;
	public static IAlleleFruit fruitCherry;
	public static IAlleleFruit fruitDates;
	public static IAlleleFruit fruitPapaya;
	public static IAlleleFruit fruitLemon;
	public static IAlleleFruit fruitPlum;
	public static IAlleleFruit fruitJujube;

	// / TREES // HEIGHT 1400 - 1449
	public static final Allele heightSmallest = new AlleleFloat("heightSmallest", 0.25f).setName("height", "smallest");
	public static final Allele heightSmaller = new AlleleFloat("heightSmaller", 0.5f).setName("height", "smaller");
	public static final Allele heightSmall = new AlleleFloat("heightSmall", 0.75f).setName("height", "small");
	public static final Allele heightAverage = new AlleleFloat("heightMax10", 1.0f).setName("height", "average");
	public static final Allele heightLarge = new AlleleFloat("heightLarge", 1.25f).setName("height", "large");
	public static final Allele heightLarger = new AlleleFloat("heightLarger", 1.5f).setName("height", "larger");
	public static final Allele heightLargest = new AlleleFloat("heightLargest", 1.75f).setName("height", "largest");
	public static final Allele heightGigantic = new AlleleFloat("heightGigantic", 2.0f).setName("height", "gigantic");

	/// BUTTERFLIES // SIZE
	public static final Allele sizeSmallest = new AlleleFloat("sizeSmallest", 0.3f).setName("size", "smallest");
	public static final Allele sizeSmaller = new AlleleFloat("sizeSmaller", 0.4f).setName("size", "smaller");
	public static final Allele sizeSmall = new AlleleFloat("sizeSmall", 0.5f).setName("size", "small");
	public static final Allele sizeAverage = new AlleleFloat("sizeAverage", 0.6f).setName("size", "average");
	public static final Allele sizeLarge = new AlleleFloat("sizeLarge", 0.75f).setName("size", "large");
	public static final Allele sizeLarger = new AlleleFloat("sizeLarger", 0.9f).setName("size", "larger");
	public static final Allele sizeLargest = new AlleleFloat("sizeLargest", 1.0f).setName("size", "largest");
	
	// / BOTH // TOLERANCE 1450 - 1499
	public static final Allele toleranceNone = new AlleleTolerance("toleranceNone", EnumTolerance.NONE);
	public static final Allele toleranceBoth1 = new AlleleTolerance("toleranceBoth1", EnumTolerance.BOTH_1, true);
	public static final Allele toleranceBoth2 = new AlleleTolerance("toleranceBoth2", EnumTolerance.BOTH_2);
	public static final Allele toleranceBoth3 = new AlleleTolerance("toleranceBoth3", EnumTolerance.BOTH_3);
	public static final Allele toleranceBoth4 = new AlleleTolerance("toleranceBoth4", EnumTolerance.BOTH_4);
	public static final Allele toleranceBoth5 = new AlleleTolerance("toleranceBoth5", EnumTolerance.BOTH_5);
	public static final Allele toleranceUp1 = new AlleleTolerance("toleranceUp1", EnumTolerance.UP_1, true);
	public static final Allele toleranceUp2 = new AlleleTolerance("toleranceUp2", EnumTolerance.UP_2);
	public static final Allele toleranceUp3 = new AlleleTolerance("toleranceUp3", EnumTolerance.UP_3);
	public static final Allele toleranceUp4 = new AlleleTolerance("toleranceUp4", EnumTolerance.UP_4);
	public static final Allele toleranceUp5 = new AlleleTolerance("toleranceUp5", EnumTolerance.UP_5);
	public static final Allele toleranceDown1 = new AlleleTolerance("toleranceDown1", EnumTolerance.DOWN_1, true);
	public static final Allele toleranceDown2 = new AlleleTolerance("toleranceDown2", EnumTolerance.DOWN_2);
	public static final Allele toleranceDown3 = new AlleleTolerance("toleranceDown3", EnumTolerance.DOWN_3);
	public static final Allele toleranceDown4 = new AlleleTolerance("toleranceDown4", EnumTolerance.DOWN_4);
	public static final Allele toleranceDown5 = new AlleleTolerance("toleranceDown5", EnumTolerance.DOWN_5);

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
	public static final Allele saplingsLowest = new AlleleFloat("saplingsLowest", 0.01f, true).setName("saplings", "lowest");
	public static final Allele saplingsLower = new AlleleFloat("saplingsLower", 0.025f, true).setName("saplings", "lower");
	public static final Allele saplingsLow = new AlleleFloat("saplingsLow", 0.035f, true).setName("saplings", "low");
	public static final Allele saplingsAverage = new AlleleFloat("saplingsDefault", 0.05f, true).setName("saplings", "average");
	public static final Allele saplingsHigh = new AlleleFloat("saplingsDouble", 0.1f, true).setName("saplings", "high");
	public static final Allele saplingsHigher = new AlleleFloat("saplingsTriple", 0.2f, true).setName("saplings", "higher");
	public static final Allele saplingsHighest = new AlleleFloat("saplingsHighest", 0.3f, true).setName("saplings", "highest");

	// / TREES // YIELD 1650 - 1699
	public static final Allele yieldLowest = new AlleleFloat("yieldLowest", 0.025f, true).setName("yield", "lowest");
	public static final Allele yieldLower = new AlleleFloat("yieldLower", 0.05f, true).setName("yield", "lower");
	public static final Allele yieldLow = new AlleleFloat("yieldLow", 0.1f, true).setName("yield", "low");
	public static final Allele yieldAverage = new AlleleFloat("yieldDefault", 0.2f, true).setName("yield", "average");
	public static final Allele yieldHigh = new AlleleFloat("yieldHigh", 0.3f, false).setName("yield", "high");
	public static final Allele yieldHigher = new AlleleFloat("yieldHigher", 0.35f, false).setName("yield", "higher");
	public static final Allele yieldHighest = new AlleleFloat("yieldHighest", 0.4f, false).setName("yield", "highest");

	// TREES // SAPPINESS
	public static final Allele sappinessLowest = new AlleleFloat("sappinessLowest", 0.1f, true).setName("sappiness", "lowest");
	public static final Allele sappinessLower = new AlleleFloat("sappinessLower", 0.2f, true).setName("sappiness", "lower");
	public static final Allele sappinessLow = new AlleleFloat("sappinessLow", 0.3f, true).setName("sappiness", "low");
	public static final Allele sappinessAverage = new AlleleFloat("sappinessAverage", 0.4f, true).setName("sappiness", "average");
	public static final Allele sappinessHigh = new AlleleFloat("sappinessHigh", 0.6f, true).setName("sappiness", "high");
	public static final Allele sappinessHigher = new AlleleFloat("sappinessHigher", 0.8f, false).setName("sappiness", "higher");
	public static final Allele sappinessHighest = new AlleleFloat("sappinessHighest", 1.0f, false).setName("sappiness", "highest");

	// TREES // MATURATION TIME
	public static final Allele maturationSlowest = new AlleleInteger("maturationSlowest", 10, true).setName("maturity", "slowest");
	public static final Allele maturationSlower = new AlleleInteger("maturationSlower", 7).setName("maturity", "slower");
	public static final Allele maturationSlow = new AlleleInteger("maturationSlow", 5, true).setName("maturity", "slow");
	public static final Allele maturationAverage = new AlleleInteger("maturationAverage", 4).setName("maturity", "average");
	public static final Allele maturationFast = new AlleleInteger("maturationFast", 3).setName("maturity", "fast");
	public static final Allele maturationFaster = new AlleleInteger("maturationFaster", 2).setName("maturity", "faster");
	public static final Allele maturationFastest = new AlleleInteger("maturationFastest", 1).setName("maturity", "fastest");

	// / BEES // FLOWER GROWTH 1700 - 1749
	public static final Allele floweringSlowest = new AlleleInteger("floweringSlowest", 5, true).setName("flowering", "slowest");
	public static final Allele floweringSlower = new AlleleInteger("floweringSlower", 10).setName("flowering", "slower");
	public static final Allele floweringSlow = new AlleleInteger("floweringSlow", 15).setName("flowering", "slow");
	public static final Allele floweringAverage = new AlleleInteger("floweringAverage", 20).setName("flowering", "average");
	public static final Allele floweringFast = new AlleleInteger("floweringFast", 25).setName("flowering", "fast");
	public static final Allele floweringFaster = new AlleleInteger("floweringFaster", 30).setName("flowering", "faster");
	public static final Allele floweringFastest = new AlleleInteger("floweringFastest", 35).setName("flowering", "fastest");
	public static final Allele floweringMaximum = new AlleleInteger("floweringMaximum", 99, true).setName("flowering", "maximum");

	// / BOTH // TERRITORY 1750 - 1799
	public static final Allele territoryDefault = new AlleleArea("territoryDefault", new int[]{9, 6, 9}).setName("territory", "average");
	public static final Allele territoryLarge = new AlleleArea("territoryLarge", new int[]{11, 8, 11}).setName("territory", "large");
	public static final Allele territoryLarger = new AlleleArea("territoryLarger", new int[]{13, 12, 13}).setName("territory", "larger");
	public static final Allele territoryLargest = new AlleleArea("territoryLargest", new int[]{15, 13, 15}).setName("territory", "largest");

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
	public static Allele effectFertile;
	public static Allele effectMycophilic;

	// / TREES // EFFECTS
	public static Allele leavesNone;

	// / BUTTERFLIES // EFFECTS 
	public static Allele butterflyNone;
	
	// These are "secondary" plant attributes, i.e. the tree can double as one.
	public static final Allele plantTypeNone = new AllelePlantType("plantTypeNone", EnumSet.noneOf(EnumPlantType.class), true);
	public static final Allele plantTypePlains = new AllelePlantType("plantTypePlains", EnumPlantType.Plains);
	public static final Allele plantTypeDesert = new AllelePlantType("plantTypeDesert", EnumPlantType.Desert);
	public static final Allele plantTypeBeach = new AllelePlantType("plantTypeBeach", EnumPlantType.Beach);
	public static final Allele plantTypeCave = new AllelePlantType("plantTypeCave", EnumPlantType.Cave);
	public static final Allele plantTypeWater = new AllelePlantType("plantTypeWater", EnumPlantType.Water);
	public static final Allele plantTypeNether = new AllelePlantType("plantTypeNether", EnumPlantType.Nether);
	public static final Allele plantTypeCrop = new AllelePlantType("plantTypeCrop", EnumPlantType.Crop);

	// / TREES // FIREPROOF
	public static final Allele fireproofFalse = new AlleleBoolean("fireproofFalse", false);
	public static final Allele fireproofTrue = new AlleleBoolean("fireproofTrue", true);

	public static void setupLegacyAPI() {
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
		return StringUtil.localize(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return name;
	}
	
	public Allele setName(String string) {
		name = string;
		return this;
	}

	@Override
	public String toString() {
		return uid;
	}
}
