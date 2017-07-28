package forestry.factory.tiles;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICrafterWorktable {
	@Nullable
	ItemStack getResult(InventoryCrafting inventoryCrafting, World world);

	boolean canTakeStack(int slotIndex);

	boolean onCraftingStart(EntityPlayer player);

	void onCraftingComplete(EntityPlayer player);
}
