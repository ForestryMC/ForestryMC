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

import com.google.common.base.CaseFormat;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Locale;

import net.minecraft.item.ItemStack;

import genetics.api.alleles.IAlleleRegistry;
import genetics.api.alleles.IAlleleTemplate;
import genetics.api.alleles.IAlleleTemplateBuilder;
import genetics.api.classification.IClassification;
import genetics.api.individual.IGenome;
import genetics.api.root.ITemplateContainer;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.components.IRootComponent;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.api.lepidopterology.genetics.IButterflyMutationBuilder;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.lepidopterology.genetics.alleles.ButterflyAlleles;

public enum MothDefinition implements IButterflyDefinition {
	Brimstone(ButterflyBranchDefinition.OPISTHOGRAPTIS, "brimstone", "luteolata", new Color(0xffea40), true, 1.0f),
	LatticedHeath(ButterflyBranchDefinition.CHIASMIA, "latticedHeath", "clathrata", new Color(0xf2f0be), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLEST);
		}
	},
	Atlas(ButterflyBranchDefinition.ATTACUS, "atlas", "atlas", new Color(0xd96e3d), false, 0.1f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGEST);
		}
	},
	BombyxMori(ButterflyBranchDefinition.BOMBYX, "bombyxMori", "bombyxMori", new Color(0xDADADA), false, 0.0f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLEST);
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
			template.set(ButterflyChromosomes.METABOLISM, 4);
			template.set(ButterflyChromosomes.COCOON, ButterflyAlleles.cocoonSilk);
		}

		@Override
		protected void registerMutations() {
			registerMutation(MothDefinition.LatticedHeath, ButterflyDefinition.Brimstone, 7);
		}
	};

	private final IAlleleButterflySpecies species;
	private final ButterflyBranchDefinition branch;

	@Nullable
	private IAlleleTemplate template;
	@Nullable
	private IGenome genome;

	MothDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity) {
		branch = branchDefinition;

		String uid = "moth_" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());

		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring(parent.getLevel().name().toLowerCase(Locale.ENGLISH).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = "butterflies/" + uid;

		IAlleleButterflySpeciesBuilder speciesBuilder = ButterflyManager.butterflyFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, Constants.MOD_ID, texture, dominant, branchDefinition.getBranch(), binomial, serumColor);
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
			butterfly.registerMutations();
		}
	}

	@Override
	public <C extends IRootComponent<IButterfly>> void onComponentSetup(C component) {
		ComponentKey key = component.getKey();
		if (key == ComponentKeys.TEMPLATES) {
			ITemplateContainer registry = (ITemplateContainer) component;
			IAlleleTemplateBuilder templateBuilder = branch.getTemplateBuilder();
			templateBuilder.set(ButterflyChromosomes.SPECIES, species);
			setAlleles(templateBuilder);

			this.template = templateBuilder.build();
			this.genome = template.toGenome();
			registry.registerTemplate(this.template);
		}
	}

	public void registerAlleles(IAlleleRegistry registry) {
		registry.registerAllele(species, ButterflyChromosomes.SPECIES);
	}

	protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {

	}

	protected void setAlleles(IAlleleTemplateBuilder template) {

	}

	protected void registerMutations() {

	}

	protected final IButterflyMutationBuilder registerMutation(IButterflyDefinition parent1, IButterflyDefinition parent2, int chance) {
		IAlleleButterflySpecies species1;
		IAlleleButterflySpecies species2;

		if (parent1 instanceof MothDefinition) {
			species1 = ((MothDefinition) parent1).species;
		} else if (parent1 instanceof ButterflyDefinition) {
			species1 = parent1.getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent1: " + parent1);
		}

		if (parent2 instanceof MothDefinition) {
			species2 = ((MothDefinition) parent2).species;
		} else if (parent2 instanceof ButterflyDefinition) {
			species2 = parent2.getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent2: " + parent2);
		}

		return ButterflyManager.butterflyMutationFactory.createMutation(species1, species2, getTemplate().alleles(), chance);
	}

	@Override
	public IButterfly createIndividual() {
		return getTemplate().toIndividual(ButterflyHelper.getRoot());
	}

	@Override
	public final IAlleleTemplate getTemplate() {
		return template;
	}

	@Override
	public final IGenome getGenome() {
		return genome;
	}

	@Override
	public final ItemStack getMemberStack(EnumFlutterType flutterType) {
		IButterfly butterfly = createIndividual();
		return ButterflyHelper.getRoot().getTypes().createStack(butterfly, flutterType);
	}

	@Override
	public IAlleleButterflySpecies getSpecies() {
		return species;
	}
}
