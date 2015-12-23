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
import forestry.api.lepidopterology.IAlleleButterflySpeciesCustom;
import forestry.api.lepidopterology.IButterfly;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

public enum MothDefinition implements IButterflyDefinition {
	Brimstone(ButterflyBranchDefinition.Opisthograptis, "brimstone", "luteolata", new Color(0xffea40), true, 1.0f),
	LatticedHeath(ButterflyBranchDefinition.Chiasmia, "latticeHeath", "clathrata", new Color(0xf2f0be), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLEST);
		}
	},
	Atlas(ButterflyBranchDefinition.Attacus, "atlas", "atlas", new Color(0xd96e3d), false, 0.1f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.instance.set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGEST);
		}
	};

	private final IAlleleButterflySpeciesCustom species;
	private final ButterflyBranchDefinition branch;
	private IAllele[] template;
	private IButterflyGenome genome;

	MothDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity) {
		branch = branchDefinition;

		String uid = "moth" + name();
		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring((parent.getLevel().name().toLowerCase(Locale.ENGLISH)).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = "forestry:" + Constants.TEXTURE_PATH_ENTITIES + "/butterflies/" + uid + ".png";

		species = ButterflyManager.butterflyFactory.createSpecies("forestry." + uid, unlocalizedName, "Sengir", unlocalizedDescription, texture, dominant, branchDefinition.getBranch(), binomial, serumColor);
		species.setRarity(rarity);
		species.setNocturnal();
	}

	public static void initMoths() {
		for (MothDefinition butterfly : values()) {
			butterfly.init();
		}
	}

	private void init() {
		setSpeciesProperties(species);

		template = branch.getTemplate();
		AlleleHelper.instance.set(template, EnumButterflyChromosome.SPECIES, species);
		setAlleles(template);

		genome = ButterflyManager.butterflyRoot.templateAsGenome(template);

		ButterflyManager.butterflyRoot.registerTemplate(template);
	}

	protected void setSpeciesProperties(IAlleleButterflySpeciesCustom species) {

	}

	protected void setAlleles(IAllele[] alleles) {

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
		return ButterflyManager.butterflyRoot.getMemberStack(butterfly, flutterType.ordinal());
	}
}
