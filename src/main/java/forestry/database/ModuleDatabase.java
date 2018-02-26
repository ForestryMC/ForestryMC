package forestry.database;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

import forestry.api.modules.ForestryModule;
import forestry.apiculture.ModuleApiculture;
import forestry.arboriculture.ModuleArboriculture;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.items.ItemFruit;
import forestry.core.items.ItemRegistryCore;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.database.blocks.BlockRegistryDatabase;
import forestry.database.network.PacketRegistryDatabase;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleManager;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.DATABASE, name = "Database", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.database.description")
public class ModuleDatabase extends BlankForestryModule {
	@Nullable
	private static BlockRegistryDatabase blocks;

	public static BlockRegistryDatabase getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryDatabase();
	}

	@Override
	public void doInit() {
		BlockRegistryDatabase blocks = getBlocks();

		blocks.database.init();
	}

	@Override
	public void registerRecipes() {
		List<Object> possibleChests = new LinkedList<>();
		List<Object> possibleSpecial = new LinkedList<>();
		if (ModuleManager.getInstance().isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.APICULTURE)) {
			possibleChests.add(new ItemStack(ModuleApiculture.getBlocks().beeChest));
			possibleSpecial.add(new ItemStack(ModuleApiculture.getItems().royalJelly));
		}
		if (ModuleManager.getInstance().isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.ARBORICULTURE)) {
			possibleChests.add(new ItemStack(ModuleArboriculture.getBlocks().treeChest));
			possibleSpecial.add(ItemFruit.EnumFruit.PLUM.getStack());
		}
		if (ModuleManager.getInstance().isModuleEnabled(Constants.MOD_ID, ForestryModuleUids.LEPIDOPTEROLOGY)) {
			possibleChests.add(new ItemStack(ModuleLepidopterology.getBlocks().butterflyChest));
		}
		if (possibleChests.size() == 1) {
			addRecipe(possibleSpecial, "chestWood", possibleChests.get(0));
		}
		if (possibleSpecial.isEmpty()) {
			possibleSpecial.add("chestWood");
		}
		if (possibleChests.isEmpty()) {
			addRecipe(possibleSpecial, "chestWood", "chestWood");
		} else {
			for (int firstChest = 0; firstChest < possibleChests.size(); firstChest++) {
				for (int secondChest = 0; secondChest < possibleChests.size(); secondChest++) {
					if (secondChest != firstChest) {
						addRecipe(possibleSpecial, possibleChests.get(firstChest), possibleChests.get(secondChest));
					}
				}
			}
		}
	}

	private void addRecipe(List<Object> possibleSpecial, Object firstChest, Object secondChest) {
		for (Object special : possibleSpecial) {
			ItemRegistryCore coreItems = ModuleCore.getItems();
			RecipeUtil.addRecipe("database_" + getIngredientName(firstChest) + "_" + getIngredientName(secondChest) + "_" + getIngredientName(special), getBlocks().database,
					"I#I",
					"FYS",
					"WCW",
					'#', coreItems.portableAlyzer,
					'I', "ingotBronze",
					'W', "plankWood",
					'C', special,
					'Y', coreItems.sturdyCasing,
					'F', firstChest,
					'S', secondChest);
		}
	}

	private String getIngredientName(Object o) {
		return o instanceof ItemStack ? ((ItemStack) o).getItem().getRegistryName().getResourcePath() : o.toString();
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryDatabase();
	}
}
