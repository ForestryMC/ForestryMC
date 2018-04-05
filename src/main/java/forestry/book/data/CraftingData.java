package forestry.book.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CraftingData {
	/**
	 * The {@link ResourceLocation}s of the recipes of this crafting data.
	 */
	public ResourceLocation[] locations = new ResourceLocation[0];
	/**
	 * The result stack of the recipes of this crafting data.
	 */
	public ItemStack stack = ItemStack.EMPTY;
	/**
	 * The result stacks of the recipes of this crafting data.
	 */
	public ItemStack[] stacks = new ItemStack[0];
}
