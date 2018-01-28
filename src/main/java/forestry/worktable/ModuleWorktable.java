package forestry.worktable;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import forestry.api.book.IBookCategory;
import forestry.api.book.IForesterBook;
import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.core.recipes.RecipeUtil;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.worktable.blocks.BlockRegistryWorktable;
import forestry.worktable.network.PacketRegistryWorktable;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.WORKTABLE, name = "Worktable", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.worktable.description")
public class ModuleWorktable extends BlankForestryModule {
	@Nullable
	private static BlockRegistryWorktable blocks;

	public static BlockRegistryWorktable getBlocks() {
		Preconditions.checkState(blocks != null);
		return blocks;
	}

	@Override
	public void registerItemsAndBlocks() {
		blocks = new BlockRegistryWorktable();
	}


	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryWorktable();
	}

	@Override
	public void doInit() {
		BlockRegistryWorktable blocks = getBlocks();

		blocks.worktable.init();
	}

	@Override
	public void registerRecipes() {
		BlockRegistryWorktable blocks = getBlocks();

		RecipeUtil.addRecipe("worktable", blocks.worktable,
			"B",
			"W",
			"C",
			'B', Items.BOOK,
			'W', "craftingTableWood",
			'C', "chestWood");
	}

	@Override
	public void registerBookEntries(IForesterBook book) {
		IBookCategory category = book.addCategory("core")
			.addEntry("worktable", new ItemStack(getBlocks().worktable));
	}
}
