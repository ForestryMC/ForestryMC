package forestry.book;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.SidedProxy;

import forestry.api.modules.ForestryModule;
import forestry.book.items.ItemRegistryBook;
import forestry.book.proxy.ProxyBook;
import forestry.core.config.Constants;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
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
	public void postInit() {
		ModuleManager.getInternalHandler().runBookInit();
	}
}
