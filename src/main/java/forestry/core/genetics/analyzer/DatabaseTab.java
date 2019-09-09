package forestry.core.genetics.analyzer;

import java.util.function.Supplier;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.genetics.IDatabaseTab;

//TODO: Rework the database and the analyser
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

	//TODO -side issues
	@Override
	public String getTooltip(IIndividual individual) {
		return I18n.format("for.gui.database.tab." + name + ".name");
	}
}
