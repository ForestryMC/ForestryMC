package forestry.worktable;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.api.modules.ForestryModule;
import forestry.core.config.Constants;
import forestry.core.network.IPacketRegistry;
import forestry.modules.BlankForestryModule;
import forestry.modules.ForestryModuleUids;
import forestry.worktable.features.WorktableBlocks;
import forestry.worktable.gui.GuiWorktable;
import forestry.worktable.gui.WorktableContainerTypes;
import forestry.worktable.network.PacketRegistryWorktable;

@ForestryModule(containerID = Constants.MOD_ID, moduleID = ForestryModuleUids.WORKTABLE, name = "Worktable", author = "Nedelosk", url = Constants.URL, unlocalizedDescription = "for.module.worktable.description")
public class ModuleWorktable extends BlankForestryModule {
	@Nullable
	private static WorktableContainerTypes containers;

	public static WorktableContainerTypes getContainerTypes() {
		Preconditions.checkNotNull(containers);
		return containers;
	}

	@Override
	public void registerContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		containers = new WorktableContainerTypes(registry);
	}

	@Override
	public void registerGuiFactories() {
		ScreenManager.registerFactory(getContainerTypes().WORKTABLE, GuiWorktable::new);
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
