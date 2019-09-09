package forestry.lepidopterology.genetics;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.IDatabaseTab;
import forestry.api.gui.GuiElementAlignment;
import forestry.api.gui.IDatabaseElement;
import forestry.api.gui.IElementLayoutHelper;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.utils.Translator;

@OnlyIn(Dist.CLIENT)
public class ButterflyProductsTab implements IDatabaseTab<IButterfly> {
	ButterflyProductsTab() {
	}

	@Override
	public void createElements(IDatabaseElement container, IButterfly individual, ItemStack itemStack) {
		IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
		Collection<ItemStack> butterflyLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getButterflyLoot().keySet();
		if (!butterflyLoot.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.loot.butterfly"), GuiElementAlignment.TOP_CENTER);
			butterflyLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> caterpillarLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getCaterpillarLoot().keySet();
		if (!caterpillarLoot.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.loot.caterpillar"), GuiElementAlignment.TOP_CENTER);
			caterpillarLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> cocoonLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.COCOON).getCocoonLoot().keySet();
		if (!cocoonLoot.isEmpty()) {
			container.label(Translator.translateToLocal("for.gui.loot.cocoon"), GuiElementAlignment.TOP_CENTER);
			cocoonLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
			groupHelper.finish();
		}
	}

	@Override
	public ItemStack getIconStack() {
		return ButterflyDefinition.Aurora.getMemberStack(EnumFlutterType.SERUM);
	}
}
