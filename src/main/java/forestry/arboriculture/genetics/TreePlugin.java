package forestry.arboriculture.genetics;

import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.core.config.Config;
import forestry.core.features.CoreItems;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.core.genetics.analyzer.ProductsTab;
import forestry.core.items.ItemFruit;
import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: Add support for the alyzer
@OnlyIn(Dist.CLIENT)
public class TreePlugin extends DatabasePlugin<ITree> {
    public static final TreePlugin INSTANCE = new TreePlugin();
    protected final Map<String, ItemStack> iconStacks = new HashMap<>();

    private TreePlugin() {
        super(
                new TreeDatabaseTab(DatabaseMode.ACTIVE),
                new TreeDatabaseTab(DatabaseMode.INACTIVE),
                new ProductsTab(() -> CoreItems.FRUITS.stack(ItemFruit.EnumFruit.CHERRY, 1)),
                new MutationsTab(ArboricultureItems.GRAFTER::stack)
        );
        NonNullList<ItemStack> treeList = NonNullList.create();
        ArboricultureItems.SAPLING.item().addCreativeItems(treeList, false);
        for (ItemStack treeStack : treeList) {
            IOrganism<?> organism = GeneticHelper.getOrganism(treeStack);
            if (organism.isEmpty()) {
                continue;
            }
            IAlleleTreeSpecies species = organism.getAllele(TreeChromosomes.SPECIES, true);
            iconStacks.put(species.getRegistryName().toString(), treeStack);
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
