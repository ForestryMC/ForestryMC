package forestry.core.genetics;

import net.minecraft.util.text.TextFormatting;

import forestry.api.core.tooltips.ToolTip;
import forestry.api.genetics.alyzer.IAlleleDisplayHelper;
import forestry.apiculture.genetics.BeeRoot;
import forestry.apiculture.genetics.IGeneticTooltipProvider;
import forestry.arboriculture.genetics.TreeRoot;
import forestry.lepidopterology.genetics.ButterflyRoot;

import genetics.api.alleles.IAllele;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;

public enum DefaultDisplayHandler implements IGeneticTooltipProvider<IIndividual> {
	UNKNOWN(-3) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IIndividual individual) {
			toolTip.singleLine()
					.text("<")
					.translated("for.gui.unknown")
					.text(">")
					.style(TextFormatting.GRAY)
					.create();
		}
	}, HYBRID(-2) {
		@Override
		public void addTooltip(ToolTip toolTip, IGenome genome, IIndividual individual) {
			IChromosomeType speciesType = individual.getRoot().getKaryotype().getSpeciesType();
			IAllele primary = genome.getActiveAllele(speciesType);
			IAllele secondary = genome.getInactiveAllele(speciesType);
			if (!individual.isPureBred(speciesType)) {
				toolTip.translated("for.bees.hybrid", primary.getDisplayName(), secondary.getDisplayName()).style(TextFormatting.BLUE);
			}
		}
	};

	final int tooltipIndex;

	DefaultDisplayHandler(int tooltipIndex) {
		this.tooltipIndex = tooltipIndex;
	}

	public static void init(IAlleleDisplayHelper helper) {
		for (DefaultDisplayHandler handler : values()) {
			int tooltipIndex = handler.tooltipIndex;
			if (tooltipIndex >= 0) {
				helper.addTooltip(handler, BeeRoot.UID, tooltipIndex * 10);
				helper.addTooltip(handler, TreeRoot.UID, tooltipIndex * 10);
				helper.addTooltip(handler, ButterflyRoot.UID, tooltipIndex * 10);
			}
		}
	}

}
