package forestry.core.genetics.analyzer;

import java.util.Locale;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

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
	public Component getTooltip(IIndividual individual) {
		return Component.translatable("for.gui.database.tab." + name().toLowerCase(Locale.ENGLISH) + ".name");
	}
}
