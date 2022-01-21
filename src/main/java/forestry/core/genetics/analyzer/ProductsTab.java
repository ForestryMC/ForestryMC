package forestry.core.genetics.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;

import forestry.api.apiculture.genetics.IBee;
import forestry.api.arboriculture.genetics.ITree;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DatabaseElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.gui.elements.layouts.LayoutHelper;

import genetics.api.individual.IIndividual;

public class ProductsTab extends DatabaseTab {
	public ProductsTab(Supplier<ItemStack> stackSupplier) {
		super("products", stackSupplier);
	}

	@Override
	public void createElements(DatabaseElement container, IIndividual individual, ItemStack itemStack) {
		LayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.horizontal(18, 2, FlexLayout.LEFT_MARGIN), 90, 0);
		Collection<ItemStack> products = getProducts(individual);
		if (!products.isEmpty()) {
			container.translated("for.gui.beealyzer.produce").setAlign(Alignment.TOP_CENTER);
			products.forEach(product -> groupHelper.add(new ItemElement(0, 0, product)));
			groupHelper.finish();
		}

		Collection<ItemStack> specialties = getSpecialties(individual);
		if (specialties.isEmpty()) {
			return;
		}

		container.translated("for.gui.beealyzer.specialty").setAlign(Alignment.TOP_CENTER);
		specialties.forEach(specialty -> groupHelper.add(new ItemElement(0, 0, specialty)));
		groupHelper.finish();
	}

	private Collection<ItemStack> getSpecialties(IIndividual individual) {
		if (individual instanceof IBee) {
			IBee bee = (IBee) individual;
			return bee.getSpecialtyList();
		} else if (individual instanceof ITree) {
			ITree tree = (ITree) individual;
			return tree.getSpecialties().getPossibleStacks();
		}
		return Collections.emptyList();
	}

	private Collection<ItemStack> getProducts(IIndividual individual) {
		if (individual instanceof IBee) {
			IBee bee = (IBee) individual;
			return bee.getProduceList();
		} else if (individual instanceof ITree) {
			ITree tree = (ITree) individual;
			return tree.getProducts().getPossibleStacks();
		}
		return Collections.emptyList();
	}
}
