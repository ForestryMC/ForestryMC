package forestry.apiculture.modules.handlers;

import de.nedelosk.modularmachines.api.modules.IModule;
import de.nedelosk.modularmachines.api.modules.handlers.filters.IContentFilter;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.api.apiculture.IHiveFrame;
import net.minecraft.item.ItemStack;

public class ItemFilterFrame implements IContentFilter<ItemStack, IModule> {
	
	@Override
	public boolean isValid(int index, ItemStack content, IModuleState<IModule> module) {
		return content != null && content.getItem() instanceof IHiveFrame;
	}

}
