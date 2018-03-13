package forestry.apiculture.genetics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.EnumDatabaseTab;
import forestry.api.genetics.IDatabaseTab;
import forestry.api.genetics.ISpeciesPlugin;
import forestry.apiculture.ModuleApiculture;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.config.Config;

//TODO: Add support for the alyzer
@SideOnly(Side.CLIENT)
public class BeePlugin implements ISpeciesPlugin<IBee> {
	public static final BeePlugin INSTANCE = new BeePlugin();
	private static final BeeDatabaseTab ACTIVE = new BeeDatabaseTab(EnumDatabaseTab.ACTIVE_SPECIES);
	private static final BeeDatabaseTab INACTIVE = new BeeDatabaseTab(EnumDatabaseTab.INACTIVE_SPECIES);

	protected final Map<String, ItemStack> iconStacks = new HashMap<>();

	private BeePlugin() {
		NonNullList<ItemStack> beeList = NonNullList.create();
		ModuleApiculture.getItems().beeDroneGE.addCreativeItems(beeList, false);
		for (ItemStack beeStack : beeList) {
			IAlleleBeeSpecies species = BeeGenome.getSpecies(beeStack);
			iconStacks.put(species.getUID(), beeStack);
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

	@Override
	public IDatabaseTab getSpeciesTab(boolean active) {
		if (active) {
			return ACTIVE;
		}
		return INACTIVE;
	}

	@Override
	public ItemStack getTabDatabaseIconItem(EnumDatabaseTab tab) {
		switch (tab) {
			case ACTIVE_SPECIES:
				return BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.QUEEN);
			case PRODUCTS:
				return ModuleApiculture.getItems().beeComb.get(EnumHoneyComb.HONEY, 1);
			case MUTATIONS:
				return ModuleApiculture.getItems().frameImpregnated.getItemStack();
		}
		return BeeDefinition.MEADOWS.getMemberStack(EnumBeeType.DRONE);
	}
}
