package forestry.core.genetics.analyzer;

import java.util.Locale;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.core.features.CoreItems;
import forestry.core.gui.elements.lib.IDatabaseElement;

import genetics.api.individual.IIndividual;

public enum AnalyzerTab implements IDatabaseTab {
	ANALYZE {
		@Override
		public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {

		}

		@Override
		public ItemStack getIconStack() {
			return CoreItems.PORTABLE_ALYZER.stack();
		}
	};

	//TODO - side issues
	public String getTooltip(IIndividual individual) {
		return I18n.get("for.gui.database.tab." + name().toLowerCase(Locale.ENGLISH) + ".name");
	}
}
