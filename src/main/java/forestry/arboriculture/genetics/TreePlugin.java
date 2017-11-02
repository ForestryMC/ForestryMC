package forestry.arboriculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.ISpeciesPlugin;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.config.Config;
import forestry.core.items.ItemFruit;

//TODO: Add support for the alyzer
@SideOnly(Side.CLIENT)
public class TreePlugin implements ISpeciesPlugin<ITree>  {
	public static final TreePlugin INSTANCE = new TreePlugin();
	private static final TreeDatabaseTab ACTIVE = new TreeDatabaseTab(EnumDatabaseTab.ACTIVE_SPECIES);
	private static final TreeDatabaseTab INACTIVE = new TreeDatabaseTab(EnumDatabaseTab.INACTIVE_SPECIES);

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private TreePlugin() {
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

	@Override
	public IDatabaseTab getSpeciesTab(boolean active) {
		if(active){
			return ACTIVE;
		}
		return INACTIVE;
	}

	@Override
	public ItemStack getTabDatabaseIconItem(EnumDatabaseTab tab) {
		switch (tab){
			case ACTIVE_SPECIES:
				return TreeDefinition.Cherry.getMemberStack(EnumGermlingType.SAPLING);
			case PRODUCTS:
				return ItemFruit.EnumFruit.CHERRY.getStack();
			case MUTATIONS:
				return ModuleArboriculture.getItems().grafter.getItemStack();
		}
		return TreeDefinition.Cherry.getMemberStack(EnumGermlingType.POLLEN);
	}
}
