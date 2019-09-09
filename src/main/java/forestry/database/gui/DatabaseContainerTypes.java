package forestry.database.gui;

import net.minecraft.inventory.container.ContainerType;

import net.minecraftforge.registries.IForgeRegistry;

import forestry.core.gui.ContainerTypes;

public class DatabaseContainerTypes extends ContainerTypes {

	public final ContainerType<ContainerDatabase> DATABASE;

	public DatabaseContainerTypes(IForgeRegistry<ContainerType<?>> registry) {
		super(registry);

		DATABASE = register(ContainerDatabase::fromNetwork, "database");
	}
}
