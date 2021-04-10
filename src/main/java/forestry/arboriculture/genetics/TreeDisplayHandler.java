package forestry.arboriculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import com.mojang.blaze3d.matrix.MatrixStack;

import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.core.tooltips.ITextInstance;
import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.alyzer.IAlleleDisplayHandler;
import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.api.genetics.alyzer.IAlyzerHelper;
import forestry.apiculture.genetics.IGeneticTooltipProvider;
import forestry.arboriculture.genetics.alleles.AlleleFruits;

import genetics.api.alleles.IAllele;
import genetics.api.alleles.IAlleleValue;
import genetics.api.individual.IChromosomeAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IChromosomeValue;
import genetics.api.individual.IGenome;

public enum TreeDisplayHandler implements IAlleleDisplayHandler<ITree> {
	SAPPINESS(TreeChromosomes.SAPPINESS, TextFormatting.GOLD, "S: %1$s"),
	MATURATION(TreeChromosomes.MATURATION, TextFormatting.RED, "M: %1$s"),
	GROUP_0(0, 0, 1),
	HEIGHT(TreeChromosomes.HEIGHT, TextFormatting.LIGHT_PURPLE, "H: %1$s"),
	GIRTH(TreeChromosomes.GIRTH, TextFormatting.AQUA, "G: %1$sx%2$s"),
	GROUP_1(1, 3, 4),
	SAPLINGS(TreeChromosomes.FERTILITY, TextFormatting.YELLOW, "S: %1$s"),
	YIELD(TreeChromosomes.YIELD, TextFormatting.WHITE, "Y: %1$s"),
	GROUP_2(2, 6, 7),
	FIREPROOF(TreeChromosomes.FIREPROOF, 3) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, ITree individual) {
			toolTip.translated("for.gui.fireresist")
					.style(TextFormatting.RED);
		}
	},
	FRUITS(TreeChromosomes.FRUITS, 4) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, ITree individual) {
			IAllele fruit = getActiveAllele(genome);
			if (fruit != AlleleFruits.fruitNone) {
				ITextInstance<?, ?, ?> instance = toolTip.singleLine().text("F: ")
						.add(genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider().getDescription()).style(TextFormatting.GREEN);
				if (!individual.canBearFruit()) {
					instance.style(TextFormatting.STRIKETHROUGH);
				}
				instance.create();
			}
		}
	};

	final IChromosomeType type;
	@Nullable
	final String alyzerCaption;
	final int alyzerIndex;
	final int tooltipIndex;
	@Nullable
	final TextFormatting color;
	final String formattingText;
	final int[] groupPair;

	TreeDisplayHandler(int tooltipIndex, int... groupPair) {
		this.type = null;
		this.alyzerCaption = "";
		this.alyzerIndex = -1;
		this.tooltipIndex = tooltipIndex;
		this.color = null;
		this.formattingText = "";
		this.groupPair = groupPair;
	}

	TreeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex) {
		this(type, alyzerIndex, tooltipIndex, null);
	}

	TreeDisplayHandler(IChromosomeType type, int alyzerIndex, @Nullable String alyzerCaption) {
		this(type, alyzerIndex, -1, alyzerCaption);
	}

	TreeDisplayHandler(IChromosomeType type, TextFormatting color, String formattingText) {
		this.type = type;
		this.alyzerCaption = "";
		this.alyzerIndex = -1;
		this.tooltipIndex = -1;
		this.color = color;
		this.formattingText = formattingText;
		this.groupPair = new int[0];
	}

	TreeDisplayHandler(IChromosomeType type, int tooltipIndex) {
		this.type = type;
		this.alyzerCaption = "";
		this.alyzerIndex = -1;
		this.tooltipIndex = tooltipIndex;
		this.color = null;
		this.formattingText = "";
		this.groupPair = new int[0];
	}

	TreeDisplayHandler(IChromosomeType type, int alyzerIndex, int tooltipIndex, @Nullable String alyzerCaption) {
		this.type = type;
		this.alyzerCaption = alyzerCaption;
		this.alyzerIndex = alyzerIndex;
		this.tooltipIndex = tooltipIndex;
		this.color = null;
		this.formattingText = "";
		this.groupPair = new int[0];
	}

	public static void init(IAlleleDisplayHelper helper) {
		for (TreeDisplayHandler handler : values()) {
			int tooltipIndex = handler.tooltipIndex;
			if (tooltipIndex >= 0) {
				helper.addTooltip(handler, TreeRoot.UID, tooltipIndex * 10);
			}
			int alyzerIndex = handler.alyzerIndex;
			if (alyzerIndex >= 0) {
				helper.addAlyzer(handler, TreeRoot.UID, alyzerIndex * 10);
			}
		}
	}

	@Override
	public void addTooltip(ToolTip toolTip, IGenome genome, ITree individual) {
		//Default Implementation
	}

	@Override
	public void drawAlyzer(IAlyzerHelper helper, IGenome genome, double mouseX, double mouseY, MatrixStack transform) {
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

	private static class AllelePair implements IGeneticTooltipProvider<ITree> {
		private final IGeneticTooltipProvider<ITree> first;
		private final IGeneticTooltipProvider<ITree> second;

		private AllelePair(IGeneticTooltipProvider<ITree> first, IGeneticTooltipProvider<ITree> second) {
			this.first = first;
			this.second = second;
		}

		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, ITree individual) {
			ToolTip cache = new ToolTip();
			first.addTooltip(cache, genome, individual);
			ITextComponent fistComp = cache.lastComponent();
			second.addTooltip(cache, genome, individual);
			ITextComponent secondComp = cache.lastComponent();
			toolTip.translated("%1$s %2$s", fistComp, secondComp);
		}
	}
}
