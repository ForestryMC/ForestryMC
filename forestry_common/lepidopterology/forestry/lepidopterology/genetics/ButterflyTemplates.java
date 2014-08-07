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
package forestry.lepidopterology.genetics;

import forestry.api.genetics.IAllele;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.core.genetics.Allele;

public class ButterflyTemplates {

	public static IAllele[] getDefaultTemplate() {
		IAllele[] alleles = new IAllele[EnumButterflyChromosome.values().length];
		
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiCabbageWhite;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmall;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlowest;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
		alleles[EnumButterflyChromosome.METABOLISM.ordinal()] = Allele.int3;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int3;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceNone;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceNone;
		alleles[EnumButterflyChromosome.NOCTURNAL.ordinal()] = Allele.boolFalse;
		alleles[EnumButterflyChromosome.TOLERANT_FLYER.ordinal()] = Allele.boolFalse;
		alleles[EnumButterflyChromosome.FIRE_RESIST.ordinal()] = Allele.boolFalse;
		alleles[EnumButterflyChromosome.FLOWER_PROVIDER.ordinal()] = Allele.flowersVanilla;
		alleles[EnumButterflyChromosome.EFFECT.ordinal()] = Allele.butterflyNone;
		
		return alleles;
	}
	
	public static IAllele[] getCabbageWhiteTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		return alleles;
	}
	
	public static IAllele[] getGlasswingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiGlasswing;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmaller;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int5;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		return alleles;
	}
	
	public static IAllele[] getEmeraldPeacockTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiEmeraldPeacock;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarge;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanNormal;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int5;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		return alleles;
	}
	
	public static IAllele[] getCitrusSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiCitrusSwallow;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarge;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int10;
		alleles[EnumButterflyChromosome.METABOLISM.ordinal()] = Allele.int8;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		return alleles;
	}

	public static IAllele[] getBlackSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiBlackSwallow;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlow;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarge;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShorter;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		return alleles;
	}

	public static IAllele[] getBrimstoneMothTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.mothBrimstone;
		return alleles;
	}
	
	public static IAllele[] getSpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiSpeckledWood;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getMadeiranSpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiMadeiranSpeckledWood;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getCanarySpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiCanarySpeckledWood;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getMenelausBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiMenelausBlueMorpho;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarger;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getRhetenorBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiRhetenorBlueMorpho;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarger;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getPeleidesBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiPeleidesBlueMorpho;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarger;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
		alleles[EnumButterflyChromosome.FERTILITY.ordinal()] = Allele.int2;
		return alleles;
	}

	public static IAllele[] getBrimstoneTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiBrimstone;
		return alleles;
	}

	public static IAllele[] getAuroraTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiAurora;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmaller;
		return alleles;
	}

	public static IAllele[] getPostillionTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiPostillion;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlow;
		return alleles;
	}

	public static IAllele[] getPalaenoSulphurTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiPalaenoSulphur;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		return alleles;
	}

	public static IAllele[] getResedaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiReseda;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		return alleles;
	}

	public static IAllele[] getSpringAzureTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiSpringAzure;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmaller;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	public static IAllele[] getGozoraAzureTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiGozoraAzure;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmaller;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShort;
		return alleles;
	}

	public static IAllele[] getCommaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiComma;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		return alleles;
	}

	public static IAllele[] getBatesiaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiBatesia;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarge;
		return alleles;
	}

	public static IAllele[] getBlueWingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiBlueWing;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		alleles[EnumButterflyChromosome.METABOLISM.ordinal()] = Allele.int5;
		return alleles;
	}

	public static IAllele[] getBlueDukeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiBlueDuke;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceBoth1;
		return alleles;
	}

	public static IAllele[] getGlassyTigerTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiGlassyTiger;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		return alleles;
	}

	public static IAllele[] getPostmanTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiPostman;
		return alleles;
	}

	public static IAllele[] getSpicebushTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiSpicebush;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		return alleles;
	}

	public static IAllele[] getMalachiteTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiMalachite;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceDown1;
		return alleles;
	}

	public static IAllele[] getLeopardLacewingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiLLacewing;
		alleles[EnumButterflyChromosome.TEMPERATURE_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		alleles[EnumButterflyChromosome.HUMIDITY_TOLERANCE.ordinal()] = Allele.toleranceUp1;
		return alleles;
	}

	public static IAllele[] getMonarchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiMonarch;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		return alleles;
	}

	public static IAllele[] getThoasSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiThoasSwallow;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLarge;
		alleles[EnumButterflyChromosome.LIFESPAN.ordinal()] = Allele.lifespanShortest;
		return alleles;
	}

	public static IAllele[] getZebraSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiZebraSwallow;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeAverage;
		return alleles;
	}

	public static IAllele[] getDianaFritTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.lepiDianaFrit;
		alleles[EnumButterflyChromosome.SPEED.ordinal()] = Allele.speedSlower;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmaller;
		return alleles;
	}

	public static IAllele[] getLatticedHeathTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.mothLatticedHeath;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeSmallest;
		return alleles;
	}

	public static IAllele[] getAtlasMothTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		alleles[EnumButterflyChromosome.SPECIES.ordinal()] = Allele.mothAtlas;
		alleles[EnumButterflyChromosome.SIZE.ordinal()] = Allele.sizeLargest;
		return alleles;
	}


}
