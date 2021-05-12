package forestry.core.genetics.analyzer;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.core.features.CoreItems;
import forestry.core.gui.elements.DatabaseElement;

import genetics.api.individual.IIndividual;

public enum AnalyzerTab implements IDatabaseTab<IIndividual> {
	ANALYZE {
		@Override
		public void createElements(DatabaseElement container, IIndividual individual, ItemStack itemStack) {

		}

		@Override
		public ItemStack getIconStack() {
			return CoreItems.PORTABLE_ALYZER.stack();
		}
	};

	@Override
	public ITextComponent getTooltip(IIndividual individual) {
		return new TranslationTextComponent("for.gui.database.tab." + name().toLowerCase(Locale.ENGLISH) + ".name");
	}
}
