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

import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.classification.IBranchDefinition;
import genetics.api.classification.IClassification;
import genetics.api.classification.IClassification.EnumClassLevel;
import genetics.api.classification.IClassificationRegistry;

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
	TROPICAL(""),
	;

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

	@Override
	public IAlleleTemplate getTemplate() {
		return TreeHelper.getKaryotype().getDefaultTemplate();
	}

	@Override
	public IClassification getBranch() {
		return branch;
	}

	public static void registerBranches(IClassificationRegistry classRegistry) {
		IClassification plantae = classRegistry.getClassification("kingdom.plantae");

		plantae.addMemberGroup(
			classRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "angiosperms", "Angiosperms",
				classRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "asterids", "Asterids",
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "ericales", "Ericales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "ebenaceae", "Ebenaceae",
							EBONY.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fabaceae", "Fabaceae",
							ACACIA.getBranch(),
							DALBERGIA.getBranch(),
							MILLETTIA.getBranch(),
							PTEROCARPUS.getBranch()
						)
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "lamiales", "Lamiales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "bignoniaceae", "Bignoniaceae",
							TABEBUIA.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lamiaceae", "Lamiaceae",
							TECTONA.getBranch()
						)
					)
				),
				classRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "commelinids", "Commelinids",
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "arecales", "Arecales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "arecaceae", "Arecaceae",
							PHOENIX.getBranch()
						)
					)
				),
				classRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "rosids", "Rosids",
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "brassicales", "Brassicales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "caricaceae", "Caricaceae")
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fabales", "Fabales"),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "fagales", "Fagales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "betulaceae", "Betulaceae",
							BETULA.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "fagaceae", "Fagaceae",
							CASTANEA.getBranch(),
							QUERCUS.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "juglandaceae", "Juglandaceae",
							JUGLANS.getBranch()
						)
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "rosales", "Rosales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "rosaceae", "Rosaceae",
							PRUNUS.getBranch()
						)
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malvales", "Malvales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "dipterocarpaceae", "Dipterocarpaceae",
							MAHOGANY.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "malvaceae", "Malvaceae",
							ADANSONIA.getBranch(),
							CEIBA.getBranch(),
							OCHROMA.getBranch(),
							TALIPARITI.getBranch(),
							TILIA.getBranch()
						)
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "laurales", "Laurales"),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "malpighiales", "Malpighiales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "salicaceae", "Salicaceae",
							CHLOROCARDIUM.getBranch(),
							POPULUS.getBranch(),
							SALIX.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "lauraceae", "Lauraceae")
					),
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "sapindales", "Sapindales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "rutaceae", "Rutaceae",
							CITRUS.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "sapindaceae", "Sapindaceae",
							ACER.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "anacardiaceae", "Anacardiaceae",
							ASTRONIUM.getBranch()
						)
					)
				)
			)
		);

		plantae.addMemberGroup(
			classRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "pinophyta", "Pinophyta",
				classRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "pinopsida", "Pinopsida",
					classRegistry.createAndRegisterClassification(EnumClassLevel.ORDER, "pinales", "Pinales",
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "pinaceae", "Pinaceae",
							PICEA.getBranch(),
							PINUS.getBranch(),
							LARIX.getBranch()
						),
						classRegistry.createAndRegisterClassification(EnumClassLevel.FAMILY, "cupressaceae", "Cupressaceae",
							SEQUOIA.getBranch(),
							SEQUOIADENDRON.getBranch()
						)
					)
				),
				classRegistry.createAndRegisterClassification(EnumClassLevel.CLASS, "magnoliopsida", "Magnoliopsida")
			)
		);

		plantae.addMemberGroup(
			classRegistry.createAndRegisterClassification(EnumClassLevel.DIVISION, "magnoliophyta", "Magnoliophyta")
		);
	}
}
