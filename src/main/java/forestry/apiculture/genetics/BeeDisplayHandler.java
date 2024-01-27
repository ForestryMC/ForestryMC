package forestry.apiculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.item.Rarity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.alleles.AlleleManager;
import forestry.api.genetics.alleles.IAlleleFlowers;
import forestry.api.genetics.alyzer.IAlleleDisplayHandler;
import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.core.genetics.GenericRatings;
import forestry.core.utils.Translator;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IGenome;

public enum BeeDisplayHandler implements IAlleleDisplayHandler<IBee> {
	GENERATIONS(-1) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			int generation = individual.getGeneration();
			if (generation > 0) {
				Rarity rarity;
				if (generation >= 1000) {
					rarity = Rarity.EPIC;
				} else if (generation >= 100) {
					rarity = Rarity.RARE;
				} else if (generation >= 10) {
					rarity = Rarity.UNCOMMON;
				} else {
					rarity = Rarity.COMMON;
				}
				toolTip.translated("for.gui.beealyzer.generations", generation).style(rarity.color);
			}
		}
	}, SPECIES(BeeChromosomes.SPECIES, 0) {
	},
	SPEED(BeeChromosomes.SPEED, 2, 1) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleValue<Integer> speedAllele = getActive(genome);
			Component customSpeed = Component.translatable("for.tooltip.worker." +
					speedAllele.getLocalisationKey().replaceAll("(.*)\\.", ""));
			if (Translator.canTranslate(customSpeed)) {
				toolTip.singleLine()
						.add(customSpeed)
						.style(ChatFormatting.GRAY)
						.create();
			} else {
				toolTip.singleLine()
						.add(speedAllele.getDisplayName())
						.text(" ")
						.translated("for.gui.worker")
						.style(ChatFormatting.GRAY)
						.create();
			}
		}
	},
	LIFESPAN(BeeChromosomes.LIFESPAN, 1, 0) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			toolTip.singleLine()
					.add(genome.getActiveAllele(BeeChromosomes.LIFESPAN).getDisplayName())
					.text(" ")
					.translated("for.gui.life")
					.style(ChatFormatting.GRAY)
					.create();
		}
	},
	FERTILITY(BeeChromosomes.FERTILITY, 5, "fertility") {
	},
	TEMPERATURE_TOLERANCE(BeeChromosomes.TEMPERATURE_TOLERANCE, -1, 2) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
			IAlleleValue<EnumTolerance> tempToleranceAllele = getActive(genome);
			Component caption = AlleleManager.climateHelper.toDisplay(primary.getTemperature());
			toolTip.singleLine()
					.text("T: ")
					.add(caption)
					.text(" / ")
					.add(tempToleranceAllele.getDisplayName())
					.style(ChatFormatting.GREEN)
					.create();
		}
	},
	HUMIDITY_TOLERANCE(BeeChromosomes.HUMIDITY_TOLERANCE, -1, 3) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleBeeSpecies primary = genome.getActiveAllele(BeeChromosomes.SPECIES);
			IAlleleValue<EnumTolerance> humidToleranceAllele = getActive(genome);
			Component caption = AlleleManager.climateHelper.toDisplay(primary.getHumidity());
			toolTip.singleLine()
					.text("H: ")
					.add(caption)
					.text(" / ")
					.add(humidToleranceAllele.getDisplayName())
					.style(ChatFormatting.GREEN)
					.create();
		}
	},
	FLOWER_PROVIDER(BeeChromosomes.FLOWER_PROVIDER, 4, 4, "flowers") {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			IAlleleFlowers flowers = getActiveAllele(genome);
			toolTip.add(flowers.getProvider().getDescription(), ChatFormatting.GRAY);
		}
	},
	FLOWERING(BeeChromosomes.FLOWERING, 3, -1, "pollination"),
	NEVER_SLEEPS(BeeChromosomes.NEVER_SLEEPS, -1, 5) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			Boolean value = getActiveValue(genome);
			if (value) {
				toolTip.text(GenericRatings.rateActivityTime(true, false)).style(ChatFormatting.RED);
			}
		}
	},
	TOLERATES_RAIN(BeeChromosomes.TOLERATES_RAIN, -1, 6) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
			Boolean value = getActiveValue(genome);
			if (value) {
				toolTip.translated("for.gui.flyer.tooltip").style(ChatFormatting.WHITE);
			}
		}
	},
	TERRITORY(BeeChromosomes.TERRITORY, 6, "area"),
	EFFECT(BeeChromosomes.EFFECT, 7, "effect");

	final IChromosomeType type;
	@Nullable
	final String alyzerCaption;
	final int alyzerIndex;
	final int tooltipIndex;

	BeeDisplayHandler(IChromosomeType type, int alyzerIndex) {
		this(type, alyzerIndex, -1, null);
	}

	BeeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex) {
		this(type, alyzerIndex, tooltipIndex, null);
	}

	BeeDisplayHandler(IChromosomeType type, int alyzerIndex, @Nullable String alyzerCaption) {
		this(type, alyzerIndex, -1, alyzerCaption);
	}

	BeeDisplayHandler(int tooltipIndex) {
		this.type = null;
		this.alyzerCaption = "";
		this.alyzerIndex = -1;
		this.tooltipIndex = tooltipIndex;
	}

	BeeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex, @Nullable String alyzerCaption) {
		this.type = type;
		this.alyzerCaption = alyzerCaption;
		this.alyzerIndex = alyzerIndex;
		this.tooltipIndex = tooltipIndex;
	}

	public static void init(IAlleleDisplayHelper helper) {
		for (BeeDisplayHandler handler : values()) {
			int tooltipIndex = handler.tooltipIndex;
			if (tooltipIndex >= 0) {
				helper.addTooltip(handler, BeeRoot.UID, tooltipIndex * 10);
			}
			int alyzerIndex = handler.alyzerIndex;
			if (alyzerIndex >= 0) {
				helper.addAlyzer(handler, BeeRoot.UID, alyzerIndex * 10);
			}
		}
	}

	@Override
	public void addTooltip(ToolTip toolTip, IGenome genome, IBee individual) {
		//Default Implementation
	}

	<V> IAlleleValue<V> getActive(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveAllele((IChromosomeValue<V>) type);
	}

	<V> IAlleleValue<V> getInactive(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveAllele((IChromosomeValue<V>) type);
	}

	<A extends IAllele> A getActiveAllele(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveAllele((IChromosomeAllele<A>) type);
	}

	<A extends IAllele> A getInactiveAllele(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveAllele((IChromosomeAllele<A>) type);
	}

	<V> V getActiveValue(IGenome genome) {
		//noinspection unchecked
		return genome.getActiveValue((IChromosomeValue<V>) type);
	}

	<V> V getInactiveValue(IGenome genome) {
		//noinspection unchecked
		return genome.getInactiveValue((IChromosomeValue<V>) type);
	}
}
