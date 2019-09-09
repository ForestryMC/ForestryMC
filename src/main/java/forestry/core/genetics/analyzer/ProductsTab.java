package forestry.core.genetics.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.genetics.IBee;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.IElementLayoutHelper;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.utils.Translator;

public class ProductsTab extends DatabaseTab {
	public ProductsTab(Supplier<ItemStack> stackSupplier) {
		super("products", stackSupplier);
	}

	@Override
	public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {
		IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
		Collection<ItemStack> products = getProducts(individual);
		if (!products.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.beealyzer.produce"), GuiElementAlignment.TOP_CENTER);
			products.forEach(product -> groupHelper.add(new ItemElement(0, 0, product)));
			groupHelper.finish();
		}

		Collection<ItemStack> specialties = getSpecialties(individual);
		if (specialties.isEmpty()) {
			return;
		}

		container.label(Translator.translateToLocal("for.gui.beealyzer.specialty"), GuiElementAlignment.TOP_CENTER);
		specialties.forEach(specialty -> groupHelper.add(new ItemElement(0, 0, specialty)));
		groupHelper.finish();
	}

	private Collection<ItemStack> getSpecialties(IIndividual individual) {
		if (individual instanceof IBee) {
			IBee bee = (IBee) individual;
			return bee.getSpecialtyList();
		} else if (individual instanceof ITree) {
			ITree tree = (ITree) individual;
			return tree.getSpecialties().keySet();
		}
		return Collections.emptyList();
	}

	private Collection<ItemStack> getProducts(IIndividual individual) {
		if (individual instanceof IBee) {
			IBee bee = (IBee) individual;
			return bee.getProduceList();
		} else if (individual instanceof ITree) {
			ITree tree = (ITree) individual;
			return tree.getProducts().keySet();
		}
		return Collections.emptyList();
	}
}
