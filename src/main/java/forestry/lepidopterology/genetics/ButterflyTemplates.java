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
import forestry.core.genetics.alleles.Allele;
import forestry.core.genetics.alleles.EnumAllele;

public class ButterflyTemplates {

	public static IAllele[] getDefaultTemplate() {
		IAllele[] alleles = new IAllele[EnumButterflyChromosome.values().length];

		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiCabbageWhite);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALL);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWEST);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
		Allele.helper.set(alleles, EnumButterflyChromosome.METABOLISM, 3);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 3);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
		Allele.helper.set(alleles, EnumButterflyChromosome.NOCTURNAL, false);
		Allele.helper.set(alleles, EnumButterflyChromosome.TOLERANT_FLYER, false);
		Allele.helper.set(alleles, EnumButterflyChromosome.FIRE_RESIST, false);
		Allele.helper.set(alleles, EnumButterflyChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
		Allele.helper.set(alleles, EnumButterflyChromosome.EFFECT, Allele.butterflyNone);

		return alleles;
	}

	public static IAllele[] getCabbageWhiteTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		return alleles;
	}

	public static IAllele[] getGlasswingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiGlasswing);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 5);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		return alleles;
	}

	public static IAllele[] getEmeraldPeacockTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiEmeraldPeacock);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 5);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		return alleles;
	}

	public static IAllele[] getCitrusSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiCitrusSwallow);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 10);
		Allele.helper.set(alleles, EnumButterflyChromosome.METABOLISM, 8);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		return alleles;
	}

	public static IAllele[] getBlackSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiBlackSwallow);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOW);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		return alleles;
	}

	public static IAllele[] getBrimstoneMothTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.mothBrimstone);
		return alleles;
	}

	public static IAllele[] getSpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiSpeckledWood);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getMadeiranSpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiMadeiranSpeckledWood);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getCanarySpeckledWoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiCanarySpeckledWood);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getMenelausBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiMenelausBlueMorpho);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getRhetenorBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiRhetenorBlueMorpho);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getPeleidesBlueMorphoTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiPeleidesBlueMorpho);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		Allele.helper.set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		return alleles;
	}

	public static IAllele[] getBrimstoneTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiBrimstone);
		return alleles;
	}

	public static IAllele[] getAuroraTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiAurora);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		return alleles;
	}

	public static IAllele[] getPostillionTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiPostillion);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOW);
		return alleles;
	}

	public static IAllele[] getPalaenoSulphurTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiPalaenoSulphur);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		return alleles;
	}

	public static IAllele[] getResedaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiReseda);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		return alleles;
	}

	public static IAllele[] getSpringAzureTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiSpringAzure);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		return alleles;
	}

	public static IAllele[] getGozoraAzureTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiGozoraAzure);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		return alleles;
	}

	public static IAllele[] getCommaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiComma);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		return alleles;
	}

	public static IAllele[] getBatesiaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiBatesia);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		return alleles;
	}

	public static IAllele[] getBlueWingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiBlueWing);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.METABOLISM, 5);
		return alleles;
	}

	public static IAllele[] getBlueDukeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiBlueDuke);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		return alleles;
	}

	public static IAllele[] getGlassyTigerTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiGlassyTiger);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		return alleles;
	}

	public static IAllele[] getPostmanTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiPostman);
		return alleles;
	}

	public static IAllele[] getSpicebushTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiSpicebush);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		return alleles;
	}

	public static IAllele[] getMalachiteTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiMalachite);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		return alleles;
	}

	public static IAllele[] getLeopardLacewingTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiLLacewing);
		Allele.helper.set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
		Allele.helper.set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
		return alleles;
	}

	public static IAllele[] getMonarchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiMonarch);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		return alleles;
	}

	public static IAllele[] getThoasSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiThoasSwallow);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		Allele.helper.set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		return alleles;
	}

	public static IAllele[] getZebraSwallowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiZebraSwallow);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		return alleles;
	}

	public static IAllele[] getDianaFritTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.lepiDianaFrit);
		Allele.helper.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		return alleles;
	}

	public static IAllele[] getLatticedHeathTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.mothLatticedHeath);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
		return alleles;
	}

	public static IAllele[] getAtlasMothTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumButterflyChromosome.SPECIES, Allele.mothAtlas);
		Allele.helper.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGEST);
		return alleles;
	}

}
