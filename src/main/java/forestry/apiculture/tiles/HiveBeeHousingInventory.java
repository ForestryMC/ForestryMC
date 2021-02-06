package forestry.apiculture.tiles;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.apiculture.genetics.IBee;

class HiveBeeHousingInventory implements IBeeHousingInventory {
	@Nullable
	private ItemStack queen;
	@Nullable
	private ItemStack drone;

	private final TileHive hive;

	public HiveBeeHousingInventory(TileHive hive) {
		this.hive = hive;
	}

	@Override
	public ItemStack getQueen() {
		if (queen == null) {
			IBee bee = hive.getContainedBee();
			queen = BeeManager.beeRoot.getTypes().createStack(bee, EnumBeeType.QUEEN);
		}
		return queen;
	}

	@Override
	public ItemStack getDrone() {
		if (drone == null) {
			IBee bee = hive.getContainedBee();
			drone = BeeManager.beeRoot.getTypes().createStack(bee, EnumBeeType.DRONE);
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
		return true;
	}
}
