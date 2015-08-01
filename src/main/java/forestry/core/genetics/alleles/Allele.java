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
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
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
