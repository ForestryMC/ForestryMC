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
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
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
import net.minecraft.item.ItemStack;

public enum ButterflyDefinition implements IButterflyDefinition {
	CabbageWhite(ButterflyBranchDefinition.Pieris, "cabbageWhite", "rapae", new Color(0xccffee), true, 1.0f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Brimstone(ButterflyBranchDefinition.Gonepteryx, "brimstone", "rhamni", new Color(0xf0ee38), true, 1.0f),
	Aurora(ButterflyBranchDefinition.Anthocharis, "orangeTip", "cardamines", new Color(0xe34f05), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		}
	},
	Postillion(ButterflyBranchDefinition.Colias, "postillion", "croceus", new Color(0xd77e04), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOW);
		}
	},
	PalaenoSulphur(ButterflyBranchDefinition.Colias, "palaenoSulphur", "palaeno", new Color(0xf8fba3), true, 0.4f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	Reseda(ButterflyBranchDefinition.Pontia, "reseda", "edusa", new Color(0x747d48), true, 0.3f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	SpringAzure(ButterflyBranchDefinition.Celastrina, "springAzure", "argiolus", new Color(0xb8cae2), true, 0.3f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}
	},
	GozoraAzure(ButterflyBranchDefinition.Celastrina, "gozoraAzure", "gozora", new Color(0x6870e7), true, 0.2f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}
	},
	CitrusSwallow(ButterflyBranchDefinition.Papilio, "swallowtailC", "demodocus", new Color(0xeae389), false, 1.0f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 10);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.METABOLISM, 8);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	EmeraldPeacock(ButterflyBranchDefinition.Papilio, "emeraldPeacock", "palinurus", new Color(0x7cfe80), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 5);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	ThoasSwallow(ButterflyBranchDefinition.Papilio, "swallowtailT", "thoas", new Color(0xeac783), false, 0.2f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		}
	},
	Spicebush(ButterflyBranchDefinition.Papilio, "swallowtailS", "troilus", new Color(0xeefeff), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	BlackSwallow(ButterflyBranchDefinition.Papilio, "swallowtailB", "polyxenes", new Color(0xeac783), true, 1.0f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOW);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	ZebraSwallow(ButterflyBranchDefinition.Protographium, "swallowtailZ", "marcellus", new Color(0xeafeff), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Glasswing(ButterflyBranchDefinition.Greta, "glasswing", "oto", new Color(0x583732), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORT);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 5);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	SpeckledWood(ButterflyBranchDefinition.Pararge, "speckledWood", "aegeria", new Color(0x947245), true, 1.0f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	MSpeckledWood(ButterflyBranchDefinition.Pararge, "speckledWoodM", "xiphia", new Color(0x402919), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	CSpeckledWood(ButterflyBranchDefinition.Pararge, "speckledWoodC", "xiphioides", new Color(0x51372a), true, 0.5f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	MBlueMorpho(ButterflyBranchDefinition.Morpho, "blueMorphoM", "menelaus", new Color(0x72e1fd), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	PBlueMorpho(ButterflyBranchDefinition.Morpho, "blueMorphoP", "peleides", new Color(0x6ecce8), true, 0.25f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	RBlueMorpho(ButterflyBranchDefinition.Morpho, "blueMorphoR", "rhetenor", new Color(0x00bef8), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.FERTILITY, 2);
		}
	},
	Comma(ButterflyBranchDefinition.Polygonia, "comma", "c-album", new Color(0xf89505), true, 0.3f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	Batesia(ButterflyBranchDefinition.Batesia, "paintedBeauty", "hypochlora", new Color(0xfe7763), true, 0.3f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.LARGE);
		}
	},
	BlueWing(ButterflyBranchDefinition.Myscelia, "blueWing", "ethusa", new Color(0x3a93cc), true, 0.3f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.METABOLISM, 5);
		}
	},
	Monarch(ButterflyBranchDefinition.Danaus, "monarch", "plexippus", new Color(0xffa722), true, 0.2f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	BlueDuke(ButterflyBranchDefinition.Bassarona, "blueDuke", "durga", new Color(0x304240), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	GlassyTiger(ButterflyBranchDefinition.Parantica, "glassyTiger", "aglea", new Color(0x5b3935), true, 0.3f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Postman(ButterflyBranchDefinition.Heliconius, "postman", "melpomene", new Color(0xf7302d), true, 0.3f),
	Malachite(ButterflyBranchDefinition.Siproeta, "malachite", "stelenes", new Color(0xbdff53), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
					.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.AVERAGE);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	LLacewing(ButterflyBranchDefinition.Cethosia, "leopardLacewing", "cyane", new Color(0xfb8a06), true, 0.7f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
		}
	},
	DianaFrit(ButterflyBranchDefinition.Speyeria, "dianaFritillary", "diana", new Color(0xffac05), true, 0.6f) {
		@Override
		protected void setAlleles(IAllele[] alleles) {
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SPEED, EnumAllele.Speed.SLOWER);
			AlleleHelper.getInstance().set(alleles, EnumButterflyChromosome.SIZE, EnumAllele.Size.SMALLER);
		}
	};

	private final IAlleleButterflySpecies species;
	private final ButterflyBranchDefinition branch;
	private IAllele[] template;
	private IButterflyGenome genome;

	ButterflyDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity) {
		this.branch = branchDefinition;

		String uid = "lepi" + name();
		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring(parent.getLevel().name().toLowerCase(Locale.ENGLISH).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = StringUtil.camelCaseToUnderscores("butterflies/" + uid);

		IAlleleButterflySpeciesBuilder speciesBuilder = ButterflyManager.butterflyFactory.createSpecies("forestry." + uid, unlocalizedName, "Sengir", unlocalizedDescription, Constants.MOD_ID, texture, dominant, branchDefinition.getBranch(), binomial, serumColor);
		speciesBuilder.setRarity(rarity);
		setSpeciesProperties(speciesBuilder);
		this.species = speciesBuilder.build();
	}

	public static void initButterflies() {
		for (ButterflyDefinition butterfly : values()) {
			butterfly.init();
		}
		for (ButterflyDefinition butterfly : values()) {
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

		if (parent1 instanceof ButterflyDefinition) {
			species1 = ((ButterflyDefinition) parent1).species;
		} else if (parent1 instanceof MothDefinition) {
			species1 = ((MothDefinition) parent1).getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent type " + parent1);
		}

		if (parent2 instanceof ButterflyDefinition) {
			species2 = ((ButterflyDefinition) parent2).species;
		} else if (parent2 instanceof MothDefinition) {
			species2 = ((MothDefinition) parent2).getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent type " + parent2);
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
