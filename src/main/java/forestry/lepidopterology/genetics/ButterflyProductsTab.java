package forestry.lepidopterology.genetics;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.genetics.gatgets.IDatabaseTab;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.gui.elements.GuiElementFactory;
import forestry.core.gui.elements.ItemElement;
import forestry.core.gui.elements.lib.GuiElementAlignment;
import forestry.core.gui.elements.lib.IDatabaseElement;
import forestry.core.gui.elements.lib.IElementLayoutHelper;

@OnlyIn(Dist.CLIENT)
public class ButterflyProductsTab implements IDatabaseTab<IButterfly> {
    ButterflyProductsTab() {
    }

    @Override
    public void createElements(IDatabaseElement container, IButterfly individual, ItemStack itemStack) {
        IElementLayoutHelper groupHelper = container.layoutHelper((x, y) -> GuiElementFactory.INSTANCE.createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
        Collection<ItemStack> butterflyLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getButterflyLoot().getPossibleStacks();
        if (!butterflyLoot.isEmpty()) {
            container.translated("for.gui.loot.butterfly").setAlign(GuiElementAlignment.TOP_CENTER);
            butterflyLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
            groupHelper.finish();
        }

        Collection<ItemStack> caterpillarLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.SPECIES).getCaterpillarLoot().getPossibleStacks();
        if (!caterpillarLoot.isEmpty()) {
            container.translated("for.gui.loot.caterpillar").setAlign(GuiElementAlignment.TOP_CENTER);
            caterpillarLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
            groupHelper.finish();
        }

        Collection<ItemStack> cocoonLoot = individual.getGenome().getActiveAllele(ButterflyChromosomes.COCOON).getCocoonLoot().getPossibleStacks();
        if (!cocoonLoot.isEmpty()) {
            container.translated("for.gui.loot.cocoon").setAlign(GuiElementAlignment.TOP_CENTER);
            cocoonLoot.forEach(stack -> groupHelper.add(new ItemElement(0, 0, stack)));
            groupHelper.finish();
        }
    }

    @Override
    public ItemStack getIconStack() {
        return ButterflyDefinition.Aurora.getMemberStack(EnumFlutterType.SERUM);
    }
}
