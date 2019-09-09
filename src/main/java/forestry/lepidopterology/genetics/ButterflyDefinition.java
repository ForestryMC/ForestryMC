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

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpeciesBuilder;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.api.lepidopterology.genetics.IButterflyMutationBuilder;
import forestry.core.config.Constants;
import forestry.core.genetics.alleles.EnumAllele;

public enum ButterflyDefinition implements IButterflyDefinition {
	CabbageWhite(ButterflyBranchDefinition.PIERIS, "cabbageWhite", "rapae", new Color(0xccffee), true, 1.0f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Brimstone(ButterflyBranchDefinition.GONEPTERYX, "brimstone", "rhamni", new Color(0xf0ee38), true, 1.0f),
	Aurora(ButterflyBranchDefinition.ANTHOCHARIS, "orangeTip", "cardamines", new Color(0xe34f05), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLER);
		}
	},
	Postillion(ButterflyBranchDefinition.COLIAS, "postillion", "croceus", new Color(0xd77e04), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOW);
		}
	},
	PalaenoSulphur(ButterflyBranchDefinition.COLIAS, "palaenoSulphur", "palaeno", new Color(0xf8fba3), true, 0.4f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	Reseda(ButterflyBranchDefinition.PONTIA, "reseda", "edusa", new Color(0x747d48), true, 0.3f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	SpringAzure(ButterflyBranchDefinition.CELASTRINA, "springAzure", "argiolus", new Color(0xb8cae2), true, 0.3f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}
	},
	GozoraAzure(ButterflyBranchDefinition.CELASTRINA, "gozoraAzure", "gozora", new Color(0x6870e7), true, 0.2f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORT);
		}
	},
	CitrusSwallow(ButterflyBranchDefinition.PAPILIO, "swallowtailC", "demodocus", new Color(0xeae389), false, 1.0f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGE);
			template.set(ButterflyChromosomes.FERTILITY, 10);
			template.set(ButterflyChromosomes.METABOLISM, 8);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			template.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	EmeraldPeacock(ButterflyBranchDefinition.PAPILIO, "emeraldPeacock", "palinurus", new Color(0x7cfe80), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGE);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.NORMAL);
			template.set(ButterflyChromosomes.FERTILITY, 5);
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			template.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	ThoasSwallow(ButterflyBranchDefinition.PAPILIO, "swallowtailT", "thoas", new Color(0xeac783), false, 0.2f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGE);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
		}
	},
	Spicebush(ButterflyBranchDefinition.PAPILIO, "swallowtailS", "troilus", new Color(0xeefeff), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	BlackSwallow(ButterflyBranchDefinition.PAPILIO, "swallowtailB", "polyxenes", new Color(0xeac783), true, 1.0f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOW);
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGE);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTER);
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			template.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	ZebraSwallow(ButterflyBranchDefinition.PROTOGRAPHIUM, "swallowtailZ", "marcellus", new Color(0xeafeff), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Glasswing(ButterflyBranchDefinition.GRETA, "glasswing", "oto", new Color(0x583732), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORT);
			template.set(ButterflyChromosomes.FERTILITY, 5);
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	SpeckledWood(ButterflyBranchDefinition.PARARGE, "speckledWood", "aegeria", new Color(0x947245), true, 1.0f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	MSpeckledWood(ButterflyBranchDefinition.PARARGE, "speckledWoodM", "xiphia", new Color(0x402919), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	CSpeckledWood(ButterflyBranchDefinition.PARARGE, "speckledWoodC", "xiphioides", new Color(0x51372a), true, 0.5f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	MBlueMorpho(ButterflyBranchDefinition.MORPHO, "blueMorphoM", "menelaus", new Color(0x72e1fd), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	PBlueMorpho(ButterflyBranchDefinition.MORPHO, "blueMorphoP", "peleides", new Color(0x6ecce8), true, 0.25f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	RBlueMorpho(ButterflyBranchDefinition.MORPHO, "blueMorphoR", "rhetenor", new Color(0x00bef8), true, 0.1f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGER);
			template.set(ButterflyChromosomes.LIFESPAN, EnumAllele.Lifespan.SHORTEST);
			template.set(ButterflyChromosomes.FERTILITY, 2);
		}
	},
	Comma(ButterflyBranchDefinition.POLYGONIA, "comma", "c-album", new Color(0xf89505), true, 0.3f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
		}
	},
	Batesia(ButterflyBranchDefinition.BATESIA, "paintedBeauty", "hypochlora", new Color(0xfe7763), true, 0.3f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.LARGE);
		}
	},
	BlueWing(ButterflyBranchDefinition.MYSCELIA, "blueWing", "ethusa", new Color(0x3a93cc), true, 0.3f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
			template.set(ButterflyChromosomes.METABOLISM, 5);
		}
	},
	Monarch(ButterflyBranchDefinition.DANAUS, "monarch", "plexippus", new Color(0xffa722), true, 0.2f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	BlueDuke(ButterflyBranchDefinition.BASSARONA, "blueDuke", "durga", new Color(0x304240), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.COLD);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
		}
	},
	GlassyTiger(ButterflyBranchDefinition.PARANTICA, "glassyTiger", "aglea", new Color(0x5b3935), true, 0.3f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
		}
	},
	Postman(ButterflyBranchDefinition.HELICONIUS, "postman", "melpomene", new Color(0xf7302d), true, 0.3f),
	Malachite(ButterflyBranchDefinition.SIPROETA, "malachite", "stelenes", new Color(0xbdff53), true, 0.5f) {
		@Override
		protected void setSpeciesProperties(IAlleleButterflySpeciesBuilder species) {
			species.setTemperature(EnumTemperature.WARM)
				.setHumidity(EnumHumidity.DAMP);
		}

		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.AVERAGE);
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
			template.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.DOWN_1);
		}
	},
	LLacewing(ButterflyBranchDefinition.CETHOSIA, "leopardLacewing", "cyane", new Color(0xfb8a06), true, 0.7f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
			template.set(ButterflyChromosomes.HUMIDITY_TOLERANCE, EnumAllele.Tolerance.UP_1);
		}
	},
	DianaFrit(ButterflyBranchDefinition.SPEYERIA, "dianaFritillary", "diana", new Color(0xffac05), true, 0.6f) {
		@Override
		protected void setAlleles(IAlleleTemplateBuilder template) {
			template.set(ButterflyChromosomes.SPEED, EnumAllele.Speed.SLOWER);
			template.set(ButterflyChromosomes.SIZE, EnumAllele.Size.SMALLER);
		}
	};

	private final IAlleleButterflySpecies species;
	private final ButterflyBranchDefinition branch;
	@Nullable
	private IAlleleTemplate template;
	@Nullable
	private IGenome genome;

	ButterflyDefinition(ButterflyBranchDefinition branchDefinition, String speciesName, String binomial, Color serumColor, boolean dominant, float rarity) {
		this.branch = branchDefinition;

		String uid = "lepi_" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
		IClassification parent = branch.getBranch().getParent();
		String unlocalizedName = "for.butterflies.species." + parent.getUID().substring(parent.getLevel().name().toLowerCase(Locale.ENGLISH).length() + 1) + '.' + speciesName;
		String unlocalizedDescription = "for.description." + uid;

		String texture = "butterflies/" + uid;

		IAlleleButterflySpeciesBuilder speciesBuilder = ButterflyManager.butterflyFactory.createSpecies(uid, unlocalizedName, "Sengir", unlocalizedDescription, Constants.MOD_ID, texture, dominant, branchDefinition.getBranch(), binomial, serumColor);
		speciesBuilder.setRarity(rarity);
		setSpeciesProperties(speciesBuilder);
		this.species = speciesBuilder.build();
	}

	public static void preInit() {
		// just used to initialize the enums
	}

	public static void initButterflies() {
		for (ButterflyDefinition butterfly : values()) {
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

		if (parent1 instanceof ButterflyDefinition) {
			species1 = ((ButterflyDefinition) parent1).species;
		} else if (parent1 instanceof MothDefinition) {
			species1 = parent1.getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent type " + parent1);
		}

		if (parent2 instanceof ButterflyDefinition) {
			species2 = ((ButterflyDefinition) parent2).species;
		} else if (parent2 instanceof MothDefinition) {
			species2 = parent2.getSpecies();
		} else {
			throw new IllegalArgumentException("Unknown parent type " + parent2);
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
