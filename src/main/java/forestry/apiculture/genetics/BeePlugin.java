package forestry.apiculture.genetics;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.gatgets.DatabaseMode;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.config.Config;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.core.genetics.analyzer.ProductsTab;
import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BeePlugin extends DatabasePlugin<IBee> {
    public static final BeePlugin INSTANCE = new BeePlugin();

    protected final Map<String, ItemStack> iconStacks = new HashMap<>();

    private BeePlugin() {
        super(
                new BeeDatabaseTab(DatabaseMode.ACTIVE),
                new BeeDatabaseTab(DatabaseMode.INACTIVE),
                new ProductsTab(() -> ApicultureItems.BEE_COMBS.stack(EnumHoneyComb.HONEY)),
                new MutationsTab(ApicultureItems.FRAME_IMPREGNATED::stack)
        );
        NonNullList<ItemStack> beeList = NonNullList.create();
        ApicultureItems.BEE_DRONE.item().addCreativeItems(beeList, false);
        for (ItemStack beeStack : beeList) {
            IOrganism<?> organism = GeneticHelper.getOrganism(beeStack);
            if (organism.isEmpty()) {
                continue;
            }
            IAlleleBeeSpecies species = organism.getAllele(BeeChromosomes.SPECIES, true);
            iconStacks.put(species.getRegistryName().toString(), beeStack);
        }
    }

    @Override
    public Map<String, ItemStack> getIndividualStacks() {
        return iconStacks;
    }

    @Override
    public List<String> getHints() {
        return Config.hints.get("beealyzer");
    }
}
