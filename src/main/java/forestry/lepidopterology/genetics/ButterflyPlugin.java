package forestry.lepidopterology.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import genetics.api.GeneticHelper;
import genetics.api.organism.IOrganism;

import forestry.api.genetics.DatabaseMode;
import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.EnumFlutterType;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Config;
import forestry.core.genetics.analyzer.DatabasePlugin;
import forestry.core.genetics.analyzer.MutationsTab;
import forestry.lepidopterology.ModuleLepidopterology;

@OnlyIn(Dist.CLIENT)
public class ButterflyPlugin extends DatabasePlugin<IButterfly> {
	public static final ButterflyPlugin INSTANCE = new ButterflyPlugin();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private ButterflyPlugin() {
		super(new ButterflyDatabaseTab(DatabaseMode.ACTIVE),
			new ButterflyDatabaseTab(DatabaseMode.INACTIVE),
			new ButterflyProductsTab(),
			new MutationsTab(() -> ButterflyDefinition.Glasswing.getMemberStack(EnumFlutterType.COCOON)));
		NonNullList<ItemStack> butterflyList = NonNullList.create();
		ModuleLepidopterology.getItems().butterflyGE.addCreativeItems(butterflyList, false);
		for (ItemStack butterflyStack : butterflyList) {
			IOrganism<?> organism = GeneticHelper.getOrganism(butterflyStack);
			if (organism.isEmpty()) {
				continue;
			}
			IAlleleButterflySpecies species = organism.getAllele(ButterflyChromosomes.SPECIES, true);
			iconStacks.put(species.getRegistryName().toString(), butterflyStack);
		}
	}

	@Override
	public Map<String, ItemStack> getIndividualStacks() {
		return iconStacks;
	}

	@Override
	public List<String> getHints() {
		return Config.hints.get("flutterlyzer");
	}
}
