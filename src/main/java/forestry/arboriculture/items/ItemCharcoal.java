package forestry.arboriculture.items;

import java.util.List;

import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.Tabs;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.items.ItemForestry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCharcoal extends ItemForestry {
	
	public ItemCharcoal() {
		setCreativeTab(Tabs.tabArboriculture);
		setHasSubtypes(true);
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
		for(int i = 1;i < 25;i++){
			subItems.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(int i = 1;i < 25;i++){
			manager.registerItemModel(item, i);
		}
	}
	
	public static int getBurnTime(ItemStack fuel) {
		return  fuel.getMetadata() * 100;
	}

}
