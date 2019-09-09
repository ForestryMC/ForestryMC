package forestry.modules;

import forestry.core.network.IPacketRegistry;

public class ClientModuleHandler extends CommonModuleHandler {
	public ClientModuleHandler(ModuleManager moduleManager) {
		super(moduleManager);
	}

	@Override
	protected void registerPackages(IPacketRegistry packetRegistry) {
		super.registerPackages(packetRegistry);
		packetRegistry.registerPacketsClient();
	}
}
