package forestry.cultivation.blocks;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import forestry.api.core.IModelManager;
import forestry.core.blocks.MachineProperties;
import forestry.cultivation.tiles.TilePlanter;

public class PlanterProperties<T extends TilePlanter> extends MachineProperties<T> {

	PlanterProperties(Class<T> teClass, String name) {
		super(teClass, name);
	}

	@Override
	public void registerModel(Item item, IModelManager manager) {
		ResourceLocation itemNameFromRegistry = item.getRegistryName();
		Preconditions.checkNotNull(itemNameFromRegistry, "No registry name for item");
		String identifier = itemNameFromRegistry.getPath();
		manager.registerItemModel(item, 0, identifier);
		manager.registerItemModel(item, 1, identifier);
	}
}
