package forestry.worktable.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class WorktableContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerWorktable> WORKTABLE;

	public WorktableContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		WORKTABLE = register(ContainerWorktable::fromNetwork, "worktable");
	}
}
