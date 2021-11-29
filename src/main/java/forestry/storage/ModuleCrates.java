package forestry.storage;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.modules.ISidedModuleHandler;
import forestry.storage.proxy.ProxyCrates;
import forestry.storage.proxy.ProxyCratesClient;

@ForestryModule(moduleID = ForestryModuleUids.CRATE, containerID = Constants.MOD_ID, name = "Crate", author = "SirSengir", url = Constants.URL, unlocalizedDescription = "for.module.crates.description")
public class ModuleCrates extends BlankForestryModule {

	private final ProxyCrates proxy = DistExecutor.runForDist(() -> ProxyCratesClient::new, () -> ProxyCrates::new);

	@Override
	public ISidedModuleHandler getModuleHandler() {
		return proxy;
	}
}
