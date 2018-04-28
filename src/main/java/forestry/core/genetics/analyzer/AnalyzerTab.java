package forestry.core.genetics.analyzer;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IIndividual;
import forestry.api.gui.IElementGenetic;
import forestry.core.ModuleCore;
import forestry.core.translation.Translator;

public enum AnalyzerTab implements IDatabaseTab {
	ANALYZE {
		@Override
		public void createElements(IElementGenetic container, IIndividual individual, ItemStack itemStack) {

		}

		@Override
		public ItemStack getIconStack() {
			return new ItemStack(ModuleCore.getItems().portableAlyzer);
		}
	};

	public String getTooltip(IIndividual individual) {
		return Translator.translateToLocalFormatted("for.gui.database.tab." + name().toLowerCase() + ".name");
	}
}
