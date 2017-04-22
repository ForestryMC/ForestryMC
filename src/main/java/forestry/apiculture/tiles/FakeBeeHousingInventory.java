package forestry.apiculture.tiles;

import forestry.api.apiculture.IBeeHousingInventory;
import net.minecraft.item.ItemStack;

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
