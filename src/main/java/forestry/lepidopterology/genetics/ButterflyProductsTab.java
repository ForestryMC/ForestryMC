package forestry.lepidopterology.genetics;

import java.util.Collection;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.gui.elements.Alignment;
import forestry.core.gui.elements.DatabaseElement;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.layouts.FlexLayout;
import forestry.core.gui.elements.layouts.LayoutHelper;

@OnlyIn(Dist.CLIENT)
public class ButterflyProductsTab implements IDatabaseTab<IButterfly> {
	ButterflyProductsTab() {
	}

	@Override
	public void createElements(DatabaseElement container, IButterfly individual, ItemStack itemStack) {
		LayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.horizontal(18, 2, FlexLayout.LEFT_MARGIN), 90, 0);
		Collection<ItemStack> butterflyLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getButterflyLoot().getPossibleStacks();
		if (!butterflyLoot.isEmpty()) {
			container.translated("for.gui.loot.butterfly").setAlign(Alignment.TOP_CENTER);
			butterflyLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> caterpillarLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getCaterpillarLoot().getPossibleStacks();
		if (!caterpillarLoot.isEmpty()) {
			container.translated("for.gui.loot.caterpillar").setAlign(Alignment.TOP_CENTER);
			caterpillarLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> cocoonLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.COCOON).getCocoonLoot().getPossibleStacks();
		if (!cocoonLoot.isEmpty()) {
			container.translated("for.gui.loot.cocoon").setAlign(Alignment.TOP_CENTER);
			cocoonLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}
	}

	@Override
	public ItemStack getIconStack() {
		return ButterflyDefinition.Aurora.getMemberStack(EnumFlutterType.SERUM);
	}
}
