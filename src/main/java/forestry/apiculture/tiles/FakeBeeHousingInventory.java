package forestry.apiculture.tiles;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IBeeHousingInventory;

public class FakeBeeHousingInventory implements IBeeHousingInventory {
	public static final FakeBeeHousingInventory instance = new FakeBeeHousingInventory();

	private FakeBeeHousingInventory() {

	}

	@Override
	public ItemStack getQueen() {
		return null;
	}

	@Override
	public ItemStack getDrone() {
		return null;
	}

	@Override
	public void setQueen(ItemStack itemstack) {

	}

	@Override
	public void setDrone(ItemStack itemstack) {

	}

	@Override
	public boolean addProduct(ItemStack product, boolean all) {
		return false;
	}
}
