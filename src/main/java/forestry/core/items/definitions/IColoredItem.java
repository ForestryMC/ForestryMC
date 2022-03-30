package forestry.core.items.definitions;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gives an item the ability to be colored.
 * <p>
 * Item marked with this class get registered to the minecraft color system by {@link forestry.core.models.ClientManager}
 */
public interface IColoredItem {
	/**
	 * Defines the color of the texture sprite with the given index in the model file of the item.
	 *
	 * @param stack     The stack that contains this item
	 * @param tintIndex The index of the texture sprite in the model
	 * @return The color that the sprite with the given index should have
	 */
	@OnlyIn(Dist.CLIENT)
	int getColorFromItemStack(ItemStack stack, int tintIndex);
}
