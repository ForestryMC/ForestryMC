package forestry.arboriculture;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.arboriculture.TreeManager;
import forestry.api.modules.ForestryModule;
import forestry.arboriculture.blocks.BlockRegistryCharcoal;
import forestry.arboriculture.charcoal.CharcoalPileWall;
import forestry.core.ModuleCore;
import forestry.core.config.Constants;
import forestry.core.items.ItemRegistryCore;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.CHARCOAL, name = "Charcoal", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.charcoal.description")
public class ModuleCharcoal extends BlankForestryModule {
	@Nullable
	private static BlockRegistryCharcoal blocks;

	public static BlockRegistryCharcoal getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}


	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryCharcoal();
	}

	@Override
	public void postInit() {
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.CLAY, 3));
		TreeManager.pileWalls.add(new CharcoalPileWall(getBlocks().loam, 4));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.END_STONE, 6));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.END_BRICKS, 6));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.DIRT, 2));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.GRAVEL, 1));
		TreeManager.pileWalls.add(new CharcoalPileWall(Blocks.NETHERRACK, 3));
		TreeManager.pileWalls.add(new CharcoalPileWall(ModuleCore.getBlocks().ashBrick, 5));
	}

	@Override
	public void registerRecipes() {
		BlockRegistryCharcoal blocks = getBlocks();
		ItemRegistryCore coreItems = ModuleCore.getItems();
		//Wood Pile
		RecipeUtil.addShapelessRecipe("wood_pile", new ItemStack(blocks.woodPile), OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD, OreDictUtil.LOG_WOOD);
		RecipeUtil.addShapelessRecipe("wood_pile_decorative", new ItemStack(blocks.woodPile), blocks.woodPileDecorative);
		RecipeUtil.addShapelessRecipe("decorative_wood_pile", new ItemStack(blocks.woodPileDecorative), blocks.woodPile);

		//Charcoal
		RecipeUtil.addRecipe("charcoal_block", blocks.charcoal,
				"###",
				"###",
				"###",
				'#', new ItemStack(Items.COAL, 1, 1));
		RecipeUtil.addShapelessRecipe("charcoal", new ItemStack(Items.COAL, 9, 1), blocks.charcoal);

		//Dirt Pile Block
		RecipeUtil.addShapelessRecipe("loam", new ItemStack(blocks.loam, 4), Items.CLAY_BALL, coreItems.compost, Items.CLAY_BALL, OreDictUtil.SAND, Items.CLAY_BALL, OreDictUtil.SAND, Items.CLAY_BALL, coreItems.compost, Items.CLAY_BALL);
	}
}
