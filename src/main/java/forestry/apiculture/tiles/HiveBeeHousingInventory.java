package forestry.apiculture.tiles;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeHousingInventory;

class HiveBeeHousingInventory implements IBeeHousingInventory {
	private ItemStack queen;
	private ItemStack drone;

	private final TileHive hive;

	public HiveBeeHousingInventory(@Nonnull TileHive hive) {
		this.hive = hive;
	}

	@Override
	public ItemStack getQueen() {
		if (queen == null) {
			IBee bee = hive.getContainedBee();
			if (bee != null) {
				queen = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.QUEEN);
			}
		}
		return queen;
	}

	@Override
	public ItemStack getDrone() {
		if (drone == null) {
			IBee bee = hive.getContainedBee();
			if (bee != null) {
				drone = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.DRONE);
			}
		}
		return drone;
	}

	@Override
	public void setQueen(ItemStack itemstack) {
		this.queen = itemstack;
	}

	@Override
	public void setDrone(ItemStack itemstack) {
		this.drone = itemstack;
	}

	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		return false;
	}
}
