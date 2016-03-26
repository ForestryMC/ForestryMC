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
import forestry.api.lepidopterology.EnumCocoonType;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyCocoonProvider;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.api.lepidopterology.IButterflyMutationCustom;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum MothDefinition implements IButterflyDefinition {
	Brimstone(ButterflyBranchDefinition.Opisthograptis, "brimstone", "luteolata", new Color(0xffea40), true, 1.0f, EnumCocoonType.DEFAULT),
	LatticedHeath(ButterflyBranchDefinition.Chiasmia, "latticeHeath", "clathrata", new Color(0xf2f0be), true, 0.5f, EnumCocoonType.DEFAULT) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
		}
	},
	Atlas(ButterflyBranchDefinition.Attacus, "atlas", "atlas", new Color(0xd96e3d), false, 0.1f, EnumCocoonType.DEFAULT) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGEST);
		}
	},
	BombyxMori(ButterflyBranchDefinition.Bombyx, "bombyxMori", "bombyxMori", new Color(0xDADADA), false, 0.05f, EnumCocoonType.SILK){
		
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
		}
		
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.METABOLISM, 4);
		}
	};

	private final IAlleleButterflySpecies species;
	private final ButterflyBranchDefinition branch;
	private IAllele[] template;
	private IButterflyGenome genome;

	MothDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity, EnumCocoonType cocoonType) {
		branch = branchDefinition;

		String uid = "moth" + name();
		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring(parent.getLevel().name().toLowerCase(Locale.ENGLISH).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = "butterflies/" + uid;
		
		IButterflyCocoonProvider cocoonProvider = new DefaultCocoonProvider(cocoonType.name().toLowerCase(Locale.ENGLISH));

		IAlleleButterflySpeciesBuilder speciesBuilder = ButterflyManager.butterflyFactory.createSpecies("forestry." + uid, unlocalizedName, "Sengir", unlocalizedDescription, Constants.RESOURCE_ID, texture, dominant, branchDefinition.getBranch(), binomial, serumColor, cocoonProvider);
		speciesBuilder.setRarity(rarity);
		speciesBuilder.setNocturnal();
		setSpeciesProperties(speciesBuilder);
		species = speciesBuilder.build();
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
		AlleleHelper.instance.set(template, EnumButterflyChromosome.SPECIES, species);
		setAlleles(template);

		genome = ButterflyManager.butterflyRoot.templateAsGenome(template);

		ButterflyManager.butterflyRoot.registerTemplate(template);
	}

	protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {

	}

	protected void setAlleles(IAllele[] alleles) {

	}
	
	protected void registerMutations(){
		
	}
	
	protected final IButterflyMutationCustom registerMutation(MothDefinition parent1, MothDefinition parent2, int chance) {
		return ButterflyManager.butterflyMutationFactory.createMutation(parent1.species, parent2.species, getTemplate(), chance);
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
}
