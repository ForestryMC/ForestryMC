package forestry.book;

import forestry.api.modules.ForestryModule;
import forestry.book.proxy.ProxyBook;
import forestry.book.proxy.ProxyBookClient;
import forestry.core.config.Constants;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ModuleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.BOOK, name = "Book", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.book.description")
public class ModuleBook extends BlankForestryModule {
    @SuppressWarnings("NullableProblems")
    public static ProxyBook proxy;

    public ModuleBook() {
        proxy = DistExecutor.safeRunForDist(() -> ProxyBookClient::new, () -> ProxyBook::new);
    }

    @Override
    public void setupAPI() {
        proxy.setupAPI();
    }

    @Override
    public void preInit() {
        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(new EventHandlerBook());
    }

    @Override
    public void postInit() {
        ModuleManager.getModuleHandler().runBookInit();
    }
}
