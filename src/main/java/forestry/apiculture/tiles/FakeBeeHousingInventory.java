package forestry.apiculture.tiles;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.IBeeHousingInventory;

public class FakeBeeHousingInventory implements IBeeHousingInventory {
	public static final FakeBeeHousingInventory instance = new FakeBeeHousingInventory();

	private FakeBeeHousingInventory() {

	}

	@Override
	public ItemStack getQueen() {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getDrone() {
		return ItemStack.EMPTY;
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
