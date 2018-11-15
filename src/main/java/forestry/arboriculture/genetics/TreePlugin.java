package forestry.arboriculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.DatabaseMode;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.config.Config;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.core.genetics.analyzer.ProductsTab;
import forestry.core.items.ItemFruit;

//TODO: Add support for the alyzer
@SideOnly(Side.CLIENT)
public class TreePlugin extends DatabasePlugin<ITree> {
	public static final TreePlugin INSTANCE = new TreePlugin();
	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private TreePlugin() {
		super(new TreeDatabaseTab(DatabaseMode.ACTIVE),
			new TreeDatabaseTab(DatabaseMode.INACTIVE),
			new ProductsTab(ItemFruit.EnumFruit.CHERRY::getStack),
			new MutationsTab(() -> ModuleArboriculture.getItems().grafter.getItemStack()));
		NonNullList<ItemStack> treeList = NonNullList.create();
		ModuleArboriculture.getItems().sapling.addCreativeItems(treeList, false);
		for (ItemStack treeStack : treeList) {
			IAlleleTreeSpecies species = TreeGenome.getSpecies(treeStack);
			iconStacks.put(species.getUID(), treeStack);
		}
	}

	@Override
	public Map<String, ItemStack> getIndividualStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("treealyzer");
	}
}
