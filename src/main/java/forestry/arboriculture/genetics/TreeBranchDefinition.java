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

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IClassification.EnumClassLevel;
import forestry.arboriculture.genetics.alleles.AlleleFruit;
import forestry.arboriculture.genetics.alleles.AlleleGrowth;
import forestry.arboriculture.genetics.alleles.AlleleLeafEffect;
import forestry.core.genetics.IBranchDefinition;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.AllelePlantType;
import forestry.core.genetics.alleles.EnumAllele;

public enum TreeBranchDefinition implements IBranchDefinition {
	ACACIA,
	ACER,
	ADANSONIA,
	ASTRONIUM,
	BETULA,
	CASTANEA,
	CEIBA,
	CHLOROCARDIUM,
	CITRUS,
	DALBERGIA,
	EBONY("Diospyros"),
	JUGLANS,
	LARIX,
	MAHOGANY("Shorea"),
	MILLETTIA,
	OCHROMA,
	PHOENIX,
	PICEA,
	PINUS,
	POPULUS,
	PRUNUS,
	PTEROCARPUS,
	QUERCUS,
	SALIX,
	SEQUOIA,
	SEQUOIADENDRON,
	TABEBUIA,
	TALIPARITI,
	TECTONA,
	TILIA,

	// unclassified
	CARICA,
	TROPICAL(""),;

	private final IClassification branch;

	TreeBranchDefinition() {
		String name = this.name().toLowerCase(Locale.ENGLISH);
		String scientific = WordUtils.capitalize(name);
		branch = new BranchTrees(name, scientific);
	}

	TreeBranchDefinition(String scientific) {
		String name = this.name().toLowerCase(Locale.ENGLISH);
		branch = new BranchTrees(name, scientific);
	}

	private static IAllele[] defaultTemplate;

	@Override
	public IAllele[] getTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumTreeChromosome.values().length];

			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.FRUITS, AlleleFruit.fruitNone);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.GROWTH, AlleleGrowth.growthLightLevel);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.HEIGHT, EnumAllele.Height.SMALL);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.FERTILITY, EnumAllele.Saplings.LOWER);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.YIELD, EnumAllele.Yield.LOWEST);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.PLANT, AllelePlantType.plantTypeNone);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.SAPPINESS, EnumAllele.Sappiness.LOWEST);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.TERRITORY, EnumAllele.Territory.AVERAGE);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.EFFECT, AlleleLeafEffect.leavesNone);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.MATURATION, EnumAllele.Maturation.AVERAGE);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.GIRTH, 1);
			AlleleHelper.instance.set(defaultTemplate, EnumTreeChromosome.FIREPROOF, EnumAllele.Fireproof.FALSE);
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}

	@Override
	public IClassification getBranch() {
		return branch;
	}

	public static void createAlleles() {

		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;

		IClassification plantae = alleleRegistry.getClassification("kingdom.plantae");

		plantae.addMemberGroup(
				alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "angiosperms", "Angiosperms",
						alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "asterids", "Asterids",
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "ericales", "Ericales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "ebenaceae", "Ebenaceae",
												EBONY.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fabaceae", "Fabaceae",
												ACACIA.getBranch(),
												DALBERGIA.getBranch(),
												MILLETTIA.getBranch(),
												PTEROCARPUS.getBranch()
										)
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "lamiales", "Lamiales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "bignoniaceae", "Bignoniaceae",
												TABEBUIA.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lamiaceae", "Lamiaceae",
												TECTONA.getBranch()
										)
								)
						),
						alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "commelinids", "Commelinids",
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "arecales", "Arecales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "arecaceae", "Arecaceae",
												PHOENIX.getBranch()
										)
								)
						),
						alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "rosids", "Rosids",
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "brassicales", "Brassicales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "caricaceae", "Caricaceae")
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fabales", "Fabales"),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fagales", "Fagales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "betulaceae", "Betulaceae",
												BETULA.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fagaceae", "Fagaceae",
												CASTANEA.getBranch(),
												QUERCUS.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "juglandaceae", "Juglandaceae",
												JUGLANS.getBranch()
										)
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "rosales", "Rosales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "rosaceae", "Rosaceae",
												PRUNUS.getBranch()
										)
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malvales", "Malvales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "dipterocarpaceae", "Dipterocarpaceae",
												MAHOGANY.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "malvaceae", "Malvaceae",
												ADANSONIA.getBranch(),
												CEIBA.getBranch(),
												OCHROMA.getBranch(),
												TALIPARITI.getBranch(),
												TILIA.getBranch()
										)
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "laurales", "Laurales"),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malpighiales", "Malpighiales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "salicaceae", "Salicaceae",
												CHLOROCARDIUM.getBranch(),
												POPULUS.getBranch(),
												SALIX.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lauraceae", "Lauraceae")
								),
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "sapindales", "Sapindales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "rutaceae", "Rutaceae",
												CITRUS.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "sapindaceae", "Sapindaceae",
												ACER.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "anacardiaceae", "Anacardiaceae",
												ASTRONIUM.getBranch()
										)
								)
						)
				)
		);

		plantae.addMemberGroup(
				alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "pinophyta", "Pinophyta",
						alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "pinopsida", "Pinopsida",
								alleleRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "pinales", "Pinales",
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "pinaceae", "Pinaceae",
												PICEA.getBranch(),
												PINUS.getBranch(),
												LARIX.getBranch()
										),
										alleleRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "cupressaceae", "Cupressaceae",
												SEQUOIA.getBranch(),
												SEQUOIADENDRON.getBranch()
										)
								)
						),
						alleleRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "magnoliopsida", "Magnoliopsida")
				)
		);

		plantae.addMemberGroup(
				alleleRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "magnoliophyta", "Magnoliophyta")
		);
	}
}
