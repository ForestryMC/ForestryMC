package forestry.apiculture.modules.handlers;

import de.nedelosk.modularmachines.api.modules.handlers.inventory.IModuleInventory;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.DefaultBeeListener;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.apiculture.IHiveFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModuleBeeListener extends DefaultBeeListener {
	
	private final IBeeHousing beeHousing;
	private final IBeeHousingInventory beeHousingInventory;
	private final IModuleInventory frameHousingInventory;

	public ModuleBeeListener(IBeeHousing beeHousing, IBeeHousingInventory beeHousingInventory, IModuleInventory frameHolderInventory) {
		this.beeHousing = beeHousing;
		this.beeHousingInventory = beeHousingInventory;
		this.frameHousingInventory = frameHolderInventory;
	}

	@Override
	public void wearOutEquipment(int amount) {
		IBeekeepingMode beekeepingMode = BeeManager.beeRoot.getBeekeepingMode(beeHousing.getWorldObj());
		int wear = Math.round(amount * beekeepingMode.getWearModifier());

		for (int i = 0; i < 3; i++) {
			ItemStack hiveFrameStack = frameHousingInventory.getStackInSlot(i);
			if (hiveFrameStack == null) {
				continue;
			}

			Item hiveFrameItem = hiveFrameStack.getItem();
			if (!(hiveFrameItem instanceof IHiveFrame)) {
				continue;
			}

			IHiveFrame hiveFrame = (IHiveFrame) hiveFrameItem;

			ItemStack queenStack = beeHousingInventory.getQueen();
			IBee queen = BeeManager.beeRoot.getMember(queenStack);
			ItemStack usedFrame = hiveFrame.frameUsed(beeHousing, hiveFrameStack, queen, wear);

			frameHousingInventory.setStackInSlot(i, usedFrame);
		}
	}
	
}
