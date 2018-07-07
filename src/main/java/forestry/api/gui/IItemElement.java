package forestry.api.gui;

import net.minecraft.item.ItemStack;

/**
 * A element that contains and displays a {@link ItemStack}.
 */
public interface IItemElement extends IGuiElement {
	/**
	 * @return The contained {@link ItemStack}.
	 */
	ItemStack getStack();

	IItemElement setStack(ItemStack stack);
}
