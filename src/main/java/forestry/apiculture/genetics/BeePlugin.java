package forestry.apiculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;

import forestry.api.apiculture.genetics.BeeChromosomes;
import forestry.api.apiculture.genetics.IAlleleBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.genetics.DatabaseMode;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.config.Config;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.core.genetics.analyzer.ProductsTab;

@OnlyIn(Dist.CLIENT)
public class BeePlugin extends DatabasePlugin<IBee> {
	public static final BeePlugin INSTANCE = new BeePlugin();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private BeePlugin() {
		super(new BeeDatabaseTab(DatabaseMode.ACTIVE),
			new BeeDatabaseTab(DatabaseMode.INACTIVE),
			new ProductsTab(() -> ModuleApiculture.getItems().getComb(EnumHoneyComb.HONEY, 1)),
			new MutationsTab(() -> ModuleApiculture.getItems().frameImpregnated.getItemStack()));
		NonNullList<ItemStack> beeList = NonNullList.create();
		ModuleApiculture.getItems().beeDroneGE.addCreativeItems(beeList, false);
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
