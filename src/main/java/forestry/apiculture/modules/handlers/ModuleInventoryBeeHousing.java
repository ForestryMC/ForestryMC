package forestry.apiculture.modules.handlers;

import de.nedelosk.modularmachines.api.modules.handlers.inventory.IModuleInventory;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.apiculture.InventoryBeeHousing;
import forestry.core.utils.InventoryUtil;
import net.minecraft.item.ItemStack;

public class ModuleInventoryBeeHousing implements IBeeHousingInventory {

	private final IModuleInventory inventory;
	
	public ModuleInventoryBeeHousing(IModuleInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public final ItemStack getQueen() {
		return inventory.getStackInSlot(InventoryBeeHousing.SLOT_QUEEN);
	}

	@Override
	public final ItemStack getDrone() {
		return inventory.getStackInSlot(InventoryBeeHousing.SLOT_DRONE);
	}

	@Override
	public final void setQueen(ItemStack itemstack) {
		inventory.setStackInSlot(InventoryBeeHousing.SLOT_QUEEN, itemstack);
	}

	@Override
	public final void setDrone(ItemStack itemstack) {
		inventory.setStackInSlot(InventoryBeeHousing.SLOT_DRONE, itemstack);
	}

	@Override
	public final boolean addProduct(ItemStack product, boolean all) {
		return InventoryUtil.tryAddStack(inventory, product, InventoryBeeHousing.SLOT_PRODUCT_1, InventoryBeeHousing.SLOT_PRODUCT_COUNT, all, true);
	}

}
