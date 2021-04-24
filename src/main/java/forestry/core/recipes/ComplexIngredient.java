package forestry.core.recipes;

import net.minecraft.item.ItemStack;

import net.minecraftforge.common.crafting.NBTIngredient;

/**
 * Only used to bypass the 'protected' constructor of NBTIngredient.
 */
public class ComplexIngredient extends NBTIngredient {
	public ComplexIngredient(ItemStack stack) {
		super(stack);
	}
}
