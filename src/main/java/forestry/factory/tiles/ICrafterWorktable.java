package forestry.factory.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICrafterWorktable {
	ItemStack getResult(InventoryCrafting inventoryCrafting, World world);

	boolean canTakeStack(int slotIndex);

	boolean onCraftingStart(EntityPlayer player);

	void onCraftingComplete(EntityPlayer player);
}
