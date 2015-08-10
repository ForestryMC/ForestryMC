package forestry.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ICrafterWorktable {
	ItemStack getResult();

	boolean canTakeStack(int slotIndex);

	boolean onCraftingStart(EntityPlayer player);

	void onCraftingComplete(EntityPlayer player);
}
