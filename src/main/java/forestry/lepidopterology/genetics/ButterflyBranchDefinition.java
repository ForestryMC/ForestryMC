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

import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.classification.IBranchDefinition;
import genetics.api.classification.IClassification;
import genetics.api.classification.IClassificationRegistry;

public enum ButterflyBranchDefinition implements IBranchDefinition {
	ANTHOCHARIS,
	ATTACUS,
	BASSARONA,
	BATESIA,
	BOMBYX,
	CELASTRINA,
	CETHOSIA,
	CHIASMIA,
	COLIAS,
	DANAUS,
	GONEPTERYX,
	GRETA,
	HELICONIUS,
	MORPHO,
	MYSCELIA,
	OPISTHOGRAPTIS,
	PAPILIO,
	PARANTICA,
	PARARGE,
	PIERIS,
	POLYGONIA,
	PONTIA,
	PROTOGRAPHIUM,
	SIPROETA,
	SPEYERIA;

	private final IClassification branch;

	ButterflyBranchDefinition() {
		branch = new BranchButterflies(name());
	}

	@Override
	public IClassification getBranch() {
		return branch;
	}

	@Override
	public final IAlleleTemplate getTemplate() {
		return getTemplateBuilder().build();
	}

	@Override
	public final IAlleleTemplateBuilder getTemplateBuilder() {
		return ButterflyHelper.createTemplate();
	}

	public static void createClassifications(IClassificationRegistry registry) {
		registry.getClassification("class.insecta").addMemberGroup(
			registry.createAndRegisterClassification(IClassification.EnumClassLevel.ORDER, "lepidoptera", "Lepidoptera",
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "geometridae", "Geometridae",
					OPISTHOGRAPTIS.getBranch(),
					CHIASMIA.getBranch()
				),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "saturniidae", "Saturniidae",
					ATTACUS.getBranch()
				),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "pieridae", "Pieridae",
					PIERIS.getBranch(),
					GONEPTERYX.getBranch(),
					ANTHOCHARIS.getBranch(),
					COLIAS.getBranch(),
					PONTIA.getBranch(),
					CELASTRINA.getBranch()
				),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "nymphalidae", "Nymphalidae",
					PARARGE.getBranch(),
					POLYGONIA.getBranch(),
					MORPHO.getBranch(),
					GRETA.getBranch(),
					BATESIA.getBranch(),
					MYSCELIA.getBranch(),
					DANAUS.getBranch(),
					BASSARONA.getBranch(),
					PARANTICA.getBranch(),
					HELICONIUS.getBranch(),
					SIPROETA.getBranch(),
					CETHOSIA.getBranch(),
					SPEYERIA.getBranch()
				),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "lycaenidae", "Lycaenidae"),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "papilionidae", "Papilionidae",
					PAPILIO.getBranch(),
					PROTOGRAPHIUM.getBranch()
				),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "notchidae", "Notchidae"),
				registry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "bombycidae", "Bombycidae",
					BOMBYX.getBranch()
				)
			)
		);
	}
}
