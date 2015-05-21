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
package forestry.arboriculture.genetics;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IMutation;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.alleles.Allele;
import forestry.core.genetics.alleles.EnumAllele;

public class TreeTemplates {

	/* MUTATIONS */
	public static IMutation larchA;
	public static IMutation larchB;
	public static IMutation pineA;
	public static IMutation sequoiaA;

	public static IMutation limeA;

	public static IMutation cherryA;
	public static IMutation cherryB;

	public static IMutation walnutA;
	public static IMutation chestnutA;
	public static IMutation chestnutB;

	public static IMutation teakA;
	public static IMutation kapokA;
	public static IMutation ebonyA;
	public static IMutation mahoganyA;

	public static IMutation balsaA;
	public static IMutation acaciaA;
	public static IMutation wengeA;
	public static IMutation baobabA;

	public static IMutation willowA;
	public static IMutation willowB;
	public static IMutation willowC;

	public static IMutation sipiriA;

	public static IMutation mahoeA;

	public static IMutation poplarA;
	public static IMutation poplarB;
	public static IMutation poplarC;

	public static IMutation lemonA;
	public static IMutation plumA;

	public static IMutation mapleA;

	public static IMutation papayaA;
	public static IMutation dateA;
	
	public static IMutation ipeA;
	public static TreeMutation padaukA;
	public static TreeMutation cocoboloA;
	public static TreeMutation zebrawoodA;
	
	/* TEMPLATES */
	public static IAllele[] getDefaultTemplate() {
		IAllele[] alleles = new IAllele[EnumTreeChromosome.values().length];

		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeOak);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitNone);
		Allele.helper.set(alleles, EnumTreeChromosome.GROWTH, Allele.growthLightlevel);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWEST);
		Allele.helper.set(alleles, EnumTreeChromosome.PLANT, Allele.plantTypeNone);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
		Allele.helper.set(alleles, EnumTreeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.EFFECT, Allele.leavesNone);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 1);
		Allele.helper.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.FALSE);

		return alleles;
	}

	public static IAllele[] getOakTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitApple);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		return alleles;
	}

	public static IAllele[] getBirchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeBirch);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		return alleles;
	}

	public static IAllele[] getSpruceTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeSpruce);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		return alleles;
	}

	public static IAllele[] getJungleTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeJungle);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitCocoa);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGER);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FAST);
		return alleles;
	}
	
	public static IAllele[] getAcaciaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeAcacia);
		return alleles;
	}
	
	public static IAllele[] getDarkOakTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeDarkOak);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}

	public static IAllele[] getBalsaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeBalsa);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.HIGH);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		return alleles;
	}

	public static IAllele[] getSequoiaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeSequioa);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
		Allele.helper.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		return alleles;
	}

	public static IAllele[] getGiganteumTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeGiganteum);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.GIGANTIC);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 4);
		Allele.helper.set(alleles, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.TRUE);
		return alleles;
	}

	public static IAllele[] getLarchTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeLarch);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		return alleles;
	}

	public static IAllele[] getPineTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treePine);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		return alleles;
	}

	public static IAllele[] getCherryTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeCherry);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitCherry);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLER);
		return alleles;
	}

	public static IAllele[] getLimeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeLime);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
		//Allele.helper.set(alleles, EnumTreeChromosome.EFFECT, Allele.leavesBrimstone);
		return alleles;
	}

	public static IAllele[] getTeakTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeTeak);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		return alleles;
	}

	public static IAllele[] getKapokTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeKapok);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		return alleles;
	}

	public static IAllele[] getEbonyTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeEbony);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
		return alleles;
	}

	public static IAllele[] getMahoganyTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeMahogany);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}

	public static IAllele[] getChestnutTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeChestnut);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitChestnut);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}

	public static IAllele[] getDesertAcaciaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeDesertAcacia);
		Allele.helper.set(alleles, EnumTreeChromosome.PLANT, Allele.plantTypeDesert);
		return alleles;
	}

	public static IAllele[] getWengeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeWenge);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWEST);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}

	public static IAllele[] getBaobabTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeBaobab);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.PLANT, Allele.plantTypeDesert);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 3);
		return alleles;
	}

	public static IAllele[] getWalnutTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeWalnut);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitWalnut);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}

	public static IAllele[] getWillowTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeWillow);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.FASTER);
		return alleles;
	}

	public static IAllele[] getSipiriTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeSipiri);
		Allele.helper.set(alleles, EnumTreeChromosome.GROWTH, Allele.growthTropical);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOW);
		return alleles;
	}

	public static IAllele[] getMahoeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeMahoe);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.HIGH);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWEST);
		return alleles;
	}

	public static IAllele[] getPoplarTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treePoplar);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.SLOWER);
		return alleles;
	}

	public static IAllele[] getLemonTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeLemon);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitLemon);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		return alleles;
	}

	public static IAllele[] getPlumTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treePlum);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitPlum);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.HIGH);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.AVERAGE);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALLEST);
		return alleles;
	}

	public static IAllele[] getMapleTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeMaple);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		return alleles;
	}

	public static IAllele[] getPapayaTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treePapaya);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitPapaya);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		return alleles;
	}

	public static IAllele[] getDateTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeDate);
		Allele.helper.set(alleles, EnumTreeChromosome.FRUITS, Allele.fruitDates);
		Allele.helper.set(alleles, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOW);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.AVERAGE);
		return alleles;
	}

	public static IAllele[] getIpeTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeIpe);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}
	
	public static IAllele[] getPadaukTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treePadauk);
		Allele.helper.set(alleles, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWER);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		return alleles;
	}
	
	public static IAllele[] getCocoboloTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeCocobolo);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGEST);
		return alleles;
	}
	
	public static IAllele[] getZebrawoodTemplate() {
		IAllele[] alleles = getDefaultTemplate();
		Allele.helper.set(alleles, EnumTreeChromosome.SPECIES, Allele.treeZebrawood);
		Allele.helper.set(alleles, EnumTreeChromosome.HEIGHT, EnumAllele.Height.LARGE);
		Allele.helper.set(alleles, EnumTreeChromosome.GIRTH, 2);
		return alleles;
	}
	
	// / HELPER FUNCTIONS

	public static Chromosome[] templateAsChromosomes(IAllele[] template) {
		Chromosome[] chromosomes = new Chromosome[template.length];
		for (int i = 0; i < template.length; i++) {
			if (template[i] != null) {
				chromosomes[i] = new Chromosome(template[i]);
			}
		}

		return chromosomes;
	}

	public static Chromosome[] templateAsChromosomes(IAllele[] templateActive, IAllele[] templateInactive) {
		Chromosome[] chromosomes = new Chromosome[templateActive.length];
		for (int i = 0; i < templateActive.length; i++) {
			if (templateActive[i] != null) {
				chromosomes[i] = new Chromosome(templateActive[i], templateInactive[i]);
			}
		}

		return chromosomes;
	}

	public static ITreeGenome templateAsGenome(IAllele[] template) {
		return new TreeGenome(templateAsChromosomes(template));
	}

	public static ITreeGenome templateAsGenome(IAllele[] templateActive, IAllele[] templateInactive) {
		return new TreeGenome(templateAsChromosomes(templateActive, templateInactive));
	}
}
