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

import java.util.EnumSet;

import net.minecraft.util.StatCollector;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.ILegacyHandler;
import forestry.api.lepidopterology.IAlleleButterflySpecies;

public abstract class Allele implements IAllele {

	protected final String uid;
	protected final boolean isDominant;
	protected final String unlocalizedName;

	protected Allele(String uid, String unlocalizedName, boolean isDominant) {
		this(uid, unlocalizedName, isDominant, true);
	}

	protected Allele(String uid, String unlocalizedName, boolean isDominant, boolean doRegister) {
		this.uid = uid;
		this.isDominant = isDominant;
		this.unlocalizedName = unlocalizedName;
		
		if (doRegister) {
			AlleleManager.alleleRegistry.registerAllele(this);
		}
	}

	@Override
	public String getUID() {
		return uid;
	}

	@Override
	public boolean isDominant() {
		return isDominant;
	}

	public static AlleleHelper helper;

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
	public static final Allele plantTypeNone = new AllelePlantType("none", EnumSet.noneOf(EnumPlantType.class), true);
	public static final Allele plantTypePlains = new AllelePlantType(EnumPlantType.Plains);
	public static final Allele plantTypeDesert = new AllelePlantType(EnumPlantType.Desert);
	public static final Allele plantTypeBeach = new AllelePlantType(EnumPlantType.Beach);
	public static final Allele plantTypeCave = new AllelePlantType(EnumPlantType.Cave);
	public static final Allele plantTypeWater = new AllelePlantType(EnumPlantType.Water);
	public static final Allele plantTypeNether = new AllelePlantType(EnumPlantType.Nether);
	public static final Allele plantTypeCrop = new AllelePlantType(EnumPlantType.Crop);

	public static void setupAPI() {
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
		return StatCollector.translateToLocal(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	@Override
	public String toString() {
		return uid;
	}
}
