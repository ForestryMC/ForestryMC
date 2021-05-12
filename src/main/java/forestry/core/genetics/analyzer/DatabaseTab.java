package forestry.core.genetics.analyzer;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import forestry.api.genetics.gatgets.IDatabaseTab;

import genetics.api.individual.IIndividual;

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

	@Override
	public ITextComponent getTooltip(IIndividual individual) {
		return new TranslationTextComponent("for.gui.database.tab." + name + ".name");
	}
}
