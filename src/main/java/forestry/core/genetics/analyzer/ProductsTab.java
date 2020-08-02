package forestry.core.genetics.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import genetics.api.individual.IIndividual;

import forestry.api.apiculture.genetics.IBee;
import forestry.api.arboriculture.genetics.ITree;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.gui.elements.lib.IElementLayoutHelper;

public class ProductsTab extends DatabaseTab {
    public ProductsTab(Supplier<ItemStack> stackSupplier) {
        super("products", stackSupplier);
    }

    @Override
    public void createElements(IDatabaseElement container, IIndividual individual, ItemStack itemStack) {
        IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
        Collection<ItemStack> products = getProducts(individual);
        if (!products.isEmpty()) {
            container.translated("for.gui.beealyzer.produce").setAlign(GuiElementAlignment.TOP_CENTER);
            products.forEach(product -> groupHelper.add(new ItemElement(0, 0, product)));
            groupHelper.finish();
        }

        Collection<ItemStack> specialties = getSpecialties(individual);
        if (specialties.isEmpty()) {
            return;
        }

        container.translated("for.gui.beealyzer.specialty").setAlign(GuiElementAlignment.TOP_CENTER);
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
