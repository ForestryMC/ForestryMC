package forestry.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

public class ShapelessRecipeCustom implements IRecipe {

	@SuppressWarnings("unchecked")
	public static ShapelessRecipeCustom buildRecipe(ItemStack product, ItemStack... ingredients) {
		ShapelessRecipeCustom recipe = new ShapelessRecipeCustom(product, ingredients);
		CraftingManager.getInstance().getRecipeList().add(recipe);
		return recipe;
	}

	@SuppressWarnings("unchecked")
	public static ShapelessRecipeCustom buildPriorityRecipe(ItemStack product, ItemStack... ingredients) {
		ShapelessRecipeCustom recipe = new ShapelessRecipeCustom(product, ingredients);
		CraftingManager.getInstance().getRecipeList().add(0, recipe);
		return recipe;
	}

	private final List<ItemStack> ingredients;
	private final ItemStack product;
	private boolean preservesNbt = false;

	private ShapelessRecipeCustom(ItemStack product, ItemStack... ingredients) {
		this.ingredients = Arrays.asList(ingredients);
		this.product = product;
	}

	public ShapelessRecipeCustom setPreserveNBT() {
		this.preservesNbt = true;
		return this;
	}

	public boolean preservesNbt() {
		return preservesNbt;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>(this.ingredients);

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				ItemStack itemstack = inventoryCrafting.getStackInRowAndColumn(j, i);

				if (itemstack != null) {
					boolean flag = false;

					for (ItemStack itemstack1 : arraylist) {
						if (itemstack.getItem() == itemstack1.getItem()
								&& (itemstack1.getItemDamage() == OreDictionary.WILDCARD_VALUE
										|| itemstack.getItemDamage() == itemstack1.getItemDamage())) {
							flag = true;
							arraylist.remove(itemstack1);
							break;
						}
					}

					if (!flag) {
						return false;
					}
				}
			}
		}

		if (!arraylist.isEmpty()) {
			return false;
		}

		if (preservesNbt) {
			if (RecipeUtil.getCraftingNbt(inventoryCrafting) == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		ItemStack result = product.copy();

		if (preservesNbt) {
			NBTTagCompound craftingNbt = RecipeUtil.getCraftingNbt(inventoryCrafting);
			if (craftingNbt == null) {
				return null;
			}

			result.setTagCompound(craftingNbt);
		}

		return result;
	}

	@Override
	public int getRecipeSize() {
		return ingredients.size();
	}

	public List<ItemStack> getIngredients() {
		return ingredients;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return product.copy();
	}

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting p_179532_1_) {
		ItemStack[] aitemstack = new ItemStack[p_179532_1_.getSizeInventory()];

		for (int i = 0; i < aitemstack.length; ++i) {
			ItemStack itemstack = p_179532_1_.getStackInSlot(i);
			aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
		}

		return aitemstack;
	}

}
