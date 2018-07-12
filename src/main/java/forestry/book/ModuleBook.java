package forestry.book;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.SidedProxy;

import forestry.api.modules.ForestryModule;
import forestry.book.items.ItemRegistryBook;
import forestry.book.proxy.ProxyBook;
import forestry.core.config.Constants;
import forestry.core.recipes.RecipeUtil;
import forestry.core.utils.OreDictUtil;
import forestry.lepidopterology.ModuleLepidopterology;
import forestry.lepidopterology.items.ItemRegistryLepidopterology;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleHelper;
import forestry.modules.ModuleManager;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.BOOK, name = "Book", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.book.description")
public class ModuleBook extends BlankForestryModule {
	@SuppressWarnings("NullableProblems")
	@SidedProxy(clientSide = "forestry.book.proxy.ProxyBookClient", serverSide = "forestry.book.proxy.ProxyBook")
	public static ProxyBook proxy;

	@Nullable
	private static ItemRegistryBook items;

	public static ItemRegistryBook getItems() {
		Preconditions.checkNotNull(items);
		return items;
	}

	@Override
	public void setupAPI() {
		proxy.setupAPI();
	}

	@Override
	public void registerItemsAndBlocks() {
		items = new ItemRegistryBook();
	}

	@Override
	public void preInit() {
		proxy.preInit();
		MinecraftForge.EVENT_BUS.register(new EventHandlerBook());
	}

	@Override
	public void registerRecipes() {
		RecipeUtil.addShapelessRecipe("book_honey", new ItemStack(getItems().book), Items.BOOK, OreDictUtil.DROP_HONEY);
		RecipeUtil.addShapelessRecipe("book_tree", new ItemStack(getItems().book), Items.BOOK, OreDictUtil.TREE_SAPLING);
		if (ModuleHelper.isEnabled(ForestryModuleUids.LEPIDOPTEROLOGY)) {
			ItemRegistryLepidopterology itemsLepi = ModuleLepidopterology.getItems();
			RecipeUtil.addShapelessRecipe("book_butterfly", new ItemStack(getItems().book), Items.BOOK, itemsLepi.butterflyGE);
		}
	}

	@Override
	public void postInit() {
		ModuleManager.getInternalHandler().runBookInit();
	}
}
