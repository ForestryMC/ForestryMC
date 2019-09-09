package forestry.core.genetics.analyzer;

import java.util.Locale;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.IDatabaseElement;
import forestry.core.ModuleCore;

public enum AnalyzerTab implements IDatabaseTab {
	ANALYZE {
		@Override
		public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {

		}

		@Override
		public ItemStack getIconStack() {
			return new ItemStack(ModuleCore.getItems().portableAlyzer);
		}
	};

	//TODO - side issues
	public String getTooltip(IIndividual individual) {
		return I18n.format("for.gui.database.tab." + name().toLowerCase(Locale.ENGLISH) + ".name");
	}
}
