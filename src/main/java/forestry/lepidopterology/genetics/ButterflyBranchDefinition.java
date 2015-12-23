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

import java.util.Arrays;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleRegistry;
import forestry.api.genetics.IClassification;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.core.genetics.IBranchDefinition;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum ButterflyBranchDefinition implements IBranchDefinition {
	Anthocharis,
	Attacus,
	Bassarona,
	Batesia,
	Celastrina,
	Cethosia,
	Chiasmia,
	Colias,
	Danaus,
	Gonepteryx,
	Greta,
	Heliconius,
	Morpho,
	Myscelia,
	Opisthograptis,
	Papilio,
	Parantica,
	Pararge,
	Pieris,
	Polygonia,
	Pontia,
	Protographium,
	Siproeta,
	Speyeria;

	private static IAllele[] defaultTemplate;

	private final IClassification branch;

	ButterflyBranchDefinition() {
		branch = new BranchButterflies(name());
	}

	@Override
	public IAllele[] getTemplate() {
		if (defaultTemplate == null) {
			defaultTemplate = new IAllele[EnumButterflyChromosome.values().length];

			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALL);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWEST);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.METABOLISM, 3);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.FERTILITY, 3);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.NOCTURNAL, false);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.TOLERANT_FLYER, false);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.FIRE_RESIST, false);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
			AlleleHelper.instance.set(defaultTemplate, EnumButterflyChromosome.EFFECT, AlleleButterflyEffect.butterflyNone);
		}
		return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
	}

	@Override
	public IClassification getBranch() {
		return branch;
	}

	public static void createAlleles() {
		IAlleleRegistry alleleRegistry = AlleleManager.alleleRegistry;

		alleleRegistry.getClassification("class.insecta").addMemberGroup(
				alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.ORDER, "lepidoptera", "Lepidoptera",
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "geometridae", "Geometridae",
								Opisthograptis.getBranch(),
								Chiasmia.getBranch()
						),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "saturniidae", "Saturniidae",
								Attacus.getBranch()
						),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "pieridae", "Pieridae",
								Pieris.getBranch(),
								Gonepteryx.getBranch(),
								Anthocharis.getBranch(),
								Colias.getBranch(),
								Pontia.getBranch(),
								Celastrina.getBranch()
						),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "nymphalidae", "Nymphalidae",
								Pararge.getBranch(),
								Polygonia.getBranch(),
								Morpho.getBranch(),
								Greta.getBranch(),
								Batesia.getBranch(),
								Myscelia.getBranch(),
								Danaus.getBranch(),
								Bassarona.getBranch(),
								Parantica.getBranch(),
								Heliconius.getBranch(),
								Siproeta.getBranch(),
								Cethosia.getBranch(),
								Speyeria.getBranch()
						),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "lycaenidae", "Lycaenidae"),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "papilionidae", "Papilionidae",
								Papilio.getBranch(),
								Protographium.getBranch()
						),
						alleleRegistry.createAndRegisterClassification(IClassification.EnumClassLevel.FAMILY, "notchidae", "Notchidae")
				)
		);
	}
}
