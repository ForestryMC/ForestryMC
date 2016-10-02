package forestry.apiculture.modules.handlers;

import de.nedelosk.modularmachines.api.modules.IModule;
import de.nedelosk.modularmachines.api.modules.handlers.filters.IContentFilter;
import de.nedelosk.modularmachines.api.modules.state.IModuleState;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import net.minecraft.item.ItemStack;

public class ItemFilterBee implements IContentFilter<ItemStack, IModule> {

	private final boolean isDrone;
	
	public ItemFilterBee(boolean isDrone) {
		this.isDrone = isDrone;
	}
	
	@Override
	public boolean isValid(int index, ItemStack content, IModuleState<IModule> module) {
		EnumBeeType type = BeeManager.beeRoot.getType(content);
		return type == null ? false : isDrone && type == EnumBeeType.DRONE || !isDrone && (type == EnumBeeType.PRINCESS || type == EnumBeeType.QUEEN);
	}

}
