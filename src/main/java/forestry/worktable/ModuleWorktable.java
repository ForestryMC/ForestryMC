package forestry.worktable;

import net.minecraft.client.gui.ScreenManager;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.worktable.features.WorktableBlocks;
import forestry.worktable.features.WorktableContainers;
import forestry.worktable.gui.GuiWorktable;
import forestry.worktable.network.PacketRegistryWorktable;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.WORKTABLE, name = "Worktable", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.worktable.description")
public class ModuleWorktable extends BlankForestryModule {

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(WorktableContainers.WORKTABLE.containerType(), GuiWorktable::new);
	}

	@Override
	public IPacketRegistry getPacketRegistry() {
		return new PacketRegistryWorktable();
	}

	@Override
	public void doInit() {
		WorktableBlocks.WORKTABLE.block().init();
	}
}
