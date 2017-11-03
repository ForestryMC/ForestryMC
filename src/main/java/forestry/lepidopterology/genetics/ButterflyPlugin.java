package forestry.lepidopterology.genetics;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.ISpeciesPlugin;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Config;
import forestry.lepidopterology.ModuleLepidopterology;

@SideOnly(Side.CLIENT)
public class ButterflyPlugin implements ISpeciesPlugin<IButterfly> {
	public static final ButterflyPlugin INSTANCE = new ButterflyPlugin();
	private static final ButterflyDatabaseTab ACTIVE = new ButterflyDatabaseTab(EnumDatabaseTab.ACTIVE_SPECIES);
	private static final ButterflyDatabaseTab INACTIVE = new ButterflyDatabaseTab(EnumDatabaseTab.INACTIVE_SPECIES);
	private static final ButterflyProductsTab PRODUCTS = new ButterflyProductsTab();

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private ButterflyPlugin() {
		NonNullList<ItemStack> butterflyList = NonNullList.create();
		ModuleLepidopterology.getItems().butterflyGE.addCreativeItems(butterflyList, false);
		for (ItemStack butterflyStack : butterflyList) {
			IAlleleButterflySpecies species = ButterflyGenome.getSpecies(butterflyStack);
			iconStacks.put(species.getUID(), butterflyStack);
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

	@Override
	public IDatabaseTab getSpeciesTab(boolean active) {
		if(active){
			return ACTIVE;
		}
		return INACTIVE;
	}

	@Nullable
	@Override
	public IDatabaseTab<IButterfly> getProductsTab() {
		return PRODUCTS;
	}

	@Override
	public ItemStack getTabDatabaseIconItem(EnumDatabaseTab tab) {
		switch (tab){
			case ACTIVE_SPECIES:
				return ButterflyDefinition.BlueWing.getMemberStack(EnumFlutterType.BUTTERFLY);
			case PRODUCTS:
				return ButterflyDefinition.Aurora.getMemberStack(EnumFlutterType.SERUM);
			case MUTATIONS:
				return ButterflyDefinition.Glasswing.getMemberStack(EnumFlutterType.COCOON);
		}
		return ButterflyDefinition.GlassyTiger.getMemberStack(EnumFlutterType.CATERPILLAR);
	}
}
