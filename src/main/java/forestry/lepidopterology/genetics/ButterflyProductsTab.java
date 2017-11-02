package forestry.lepidopterology.genetics;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.GuiElementAlignment;
import forestry.api.core.IGuiElementHelper;
import forestry.api.core.IGuiElementLayoutHelper;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.gui.elements.GuiElementItemStack;
import forestry.core.utils.Translator;

@SideOnly(Side.CLIENT)
public class ButterflyProductsTab implements IDatabaseTab<IButterfly> {
	ButterflyProductsTab() {
	}

	@Override
	public void createElements(IGuiElementHelper layoutHelper, IButterfly individual, ItemStack itemStack) {
		IGuiElementLayoutHelper groupHelper = layoutHelper.layoutHelper((x, y) -> layoutHelper.factory().createHorizontal(x + 4, y, 18).setDistance(2), 90, 0);
		Collection<ItemStack> butterflyLoot = individual.getGenome().getPrimary().getButterflyLoot().keySet();
		if(!butterflyLoot.isEmpty()) {
			layoutHelper.addText(Translator.translateToLocal("for.gui.loot.butterfly"), GuiElementAlignment.CENTER);
			butterflyLoot.forEach(stack -> groupHelper.add(new GuiElementItemStack(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> caterpillarLoot = individual.getGenome().getPrimary().getCaterpillarLoot().keySet();
		if(!caterpillarLoot.isEmpty()){
			layoutHelper.addText(Translator.translateToLocal("for.gui.loot.caterpillar"), GuiElementAlignment.CENTER);
			caterpillarLoot.forEach(stack -> groupHelper.add(new GuiElementItemStack(0, 0, stack)));
			groupHelper.finish();
		}

		Collection<ItemStack> cocoonLoot = individual.getGenome().getCocoon().getCocoonLoot().keySet();
		if(!cocoonLoot.isEmpty()){
			layoutHelper.addText(Translator.translateToLocal("for.gui.loot.cocoon"), GuiElementAlignment.CENTER);
			cocoonLoot.forEach(stack -> groupHelper.add(new GuiElementItemStack(0, 0, stack)));
			groupHelper.finish();
		}
	}

	@Override
	public EnumDatabaseTab getTab() {
		return EnumDatabaseTab.PRODUCTS;
	}
}
