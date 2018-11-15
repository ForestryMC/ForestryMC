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

import java.awt.Color;
import java.util.Arrays;
import java.util.Locale;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMutationBuilder;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.utils.StringUtil;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;

public enum MothDefinition implements IButterflyDefinition {
	Brimstone(ButterflyBranchDefinition.Opisthograptis, "brimstone", "luteolata", new Color(0xffea40), true, 1.0f),
	LatticedHeath(ButterflyBranchDefinition.Chiasmia, "latticedHeath", "clathrata", new Color(0xf2f0be), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
		}
	},
	Atlas(ButterflyBranchDefinition.Attacus, "atlas", "atlas", new Color(0xd96e3d), false, 0.1f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGEST);
		}
	},
	BombyxMori(ButterflyBranchDefinition.Bombyx, "bombyxMori", "bombyxMori", new Color(0xDADADA), false, 0.0f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.METABOLISM, 4);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.COCOON, ButterflyAlleles.cocoonSilk);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MothDefinition.LatticedHeath, ButterflyDefinition.Brimstone, 7);
		}
	};

	private final IAlleleButterflySpecies species;
	private final ButterflyBranchDefinition branch;
	private IAllele[] template;
	private IButterflyGenome genome;

	MothDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity) {
		branch = branchDefinition;

		String uid = "moth" + name();
		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring(parent.getLevel().name().toLowerCase(Locale.ENGLISH).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = StringUtil.camelCaseToUnderscores("butterflies/" + uid);

		IAlleleButterflySpeciesBuilder speciesBuilder = ButterflyManager.butterflyFactory.createSpecies("forestry." + uid, unlocalizedName, "Sengir", unlocalizedDescription, Constants.MOD_ID, texture, dominant, branchDefinition.getBranch(), binomial, serumColor);
		speciesBuilder.setRarity(rarity);
		speciesBuilder.setNocturnal();
		setSpeciesProperties(speciesBuilder);
		species = speciesBuilder.build();
	}

	public static void preInit() {
		// just used to initialize the enums
	}

	public static void initMoths() {
		for (MothDefinition butterfly : values()) {
			butterfly.init();
		}
		for (MothDefinition butterfly : values()) {
			butterfly.registerMutations();
		}
	}

	private void init() {
		template = branch.getTemplate();
		AlleleHelper.getInstance().set(template, EnumButterflyChromosome.SPECIES, species);
		setAlleles(template);

		genome = ButterflyManager.butterflyRoot.templateAsGenome(template);

		ButterflyManager.butterflyRoot.registerTemplate(template);
	}

	protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {

	}

	protected void setAlleles(IAllele[] alleles) {

	}

	protected void registerMutations() {

	}

	protected final IButterflyMutationBuilder registerMutation(IButterflyDefinition parent1, IButterflyDefinition parent2, int chance) {
		IAlleleButterflySpecies species1;
		IAlleleButterflySpecies species2;

		if (parent1 instanceof MothDefinition) {
			species1 = ((MothDefinition) parent1).species;
		} else if (parent1 instanceof ButterflyDefinition) {
			species1 = ((ButterflyDefinition) parent1).getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent1: " + parent1);
		}

		if (parent2 instanceof MothDefinition) {
			species2 = ((MothDefinition) parent2).species;
		} else if (parent2 instanceof ButterflyDefinition) {
			species2 = ((ButterflyDefinition) parent2).getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent2: " + parent2);
		}

		return ButterflyManager.butterflyMutationFactory.createMutation(species1, species2, getTemplate(), chance);
	}

	@Override
	public final IAllele[] getTemplate() {
		return Arrays.copyOf(template, template.length);
	}

	@Override
	public final IButterflyGenome getGenome() {
		return genome;
	}

	@Override
	public final IButterfly getIndividual() {
		return new Butterfly(genome);
	}

	@Override
	public final ItemStack getMemberStack(EnumFlutterType flutterType) {
		IButterfly butterfly = getIndividual();
		return ButterflyManager.butterflyRoot.getMemberStack(butterfly, flutterType);
	}

	public IAlleleButterflySpecies getSpecies() {
		return species;
	}
}
