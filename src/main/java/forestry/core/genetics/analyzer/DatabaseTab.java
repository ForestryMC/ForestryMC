package forestry.core.genetics.analyzer;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.IIndividual;
import forestry.api.core.Translator;

public abstract class DatabaseTab<I extends IIndividual> implements IDatabaseTab<I> {
	private final Supplier<ItemStack> stackSupplier;
	private final String name;

	public DatabaseTab(String name, Supplier<ItemStack> stackSupplier) {
		this.name = name;
		this.stackSupplier = stackSupplier;
	}

	@Override
	public ItemStack getIconStack() {
		return stackSupplier.get();
	}

	@Override
	public String getTooltip(IIndividual individual) {
		return Translator.translateToLocal("for.gui.database.tab." + name + ".name");
	}
}
