package forestry.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import forestry.modules.features.FeatureItem;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.Nullable;

public class JeiUtil {
	public static final String DESCRIPTION_KEY = "for.jei.description.";

	@Nullable
	private ICraftingGridHelper craftingGridHelper;

	private JeiUtil() {
	}

	public static void addDescription(IRecipeRegistration registry, String itemKey, FeatureItem<?>... items) {
		List<ItemStack> itemStacks = new ArrayList<>();
		for (FeatureItem<?> item : items) {
			itemStacks.add(item.stack());
		}

		registry.addIngredientInfo(itemStacks, VanillaTypes.ITEM, new TranslatableComponent(DESCRIPTION_KEY + itemKey));
	}

	public static void addDescription(IRecipeRegistration registry, String itemKey, Item... items) {
		List<ItemStack> itemStacks = new ArrayList<>();
		for (Item item : items) {
			itemStacks.add(new ItemStack(item));
		}

		registry.addIngredientInfo(itemStacks, VanillaTypes.ITEM, new TranslatableComponent(DESCRIPTION_KEY + itemKey));
	}

	public static void addDescription(IRecipeRegistration registry, Block... blocks) {
		for (Block block : blocks) {
			Item item = Item.byBlock(block);
			if (item != Items.AIR) {
				addDescription(registry, item);
			} else {
				Log.error("No item for block {}", block);
			}
		}
	}

	public static void addDescription(IRecipeRegistration registry, FeatureItem<?>... items) {
		for (FeatureItem<?> item : items) {
			addDescription(registry, item.get());
		}
	}

	public static void addDescription(IRecipeRegistration registry, Item... items) {
		for (Item item : items) {
			addDescription(registry, item);
		}
	}

	public static void addDescription(IRecipeRegistration registry, Item item) {
		ResourceLocation registryName = item.getRegistryName();
		String resourcePath = registryName.getPath();
		addDescription(registry, item, resourcePath);
	}

	public static void addDescription(IRecipeRegistration registry, Item item, String itemKey) {
		ItemStack itemStack = new ItemStack(item);
		registry.addIngredientInfo(itemStack, VanillaTypes.ITEM, new TranslatableComponent(DESCRIPTION_KEY + itemKey));
	}

	public static List<IRecipeSlotBuilder> layoutSlotGrid(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int width, int height, int xOffset, int yOffset, int slotSpacing) {
		List<IRecipeSlotBuilder> craftingSlots = new ArrayList<>();
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				IRecipeSlotBuilder slot = builder.addSlot(role, xOffset + x * slotSpacing, yOffset + y * slotSpacing);
				craftingSlots.add(slot);
			}
		}
		return craftingSlots;
	}

    public static void setCraftingItems(List<IRecipeSlotBuilder> craftingSlots, CraftingRecipe craftingGridRecipe, ICraftingGridHelper craftingGridHelper) {
        int width = 0;
        int height = 0;
        if (craftingGridRecipe instanceof IShapedRecipe<?> shapedRecipe) {
			width = shapedRecipe.getRecipeWidth();
			height = shapedRecipe.getRecipeHeight();
		}
		setCraftingItems(craftingSlots, craftingGridRecipe.getIngredients(), width, height, craftingGridHelper);
    }

	public static void setCraftingItems(
			List<IRecipeSlotBuilder> craftingSlots,
			NonNullList<Ingredient> ingredients,
			int width,
			int height,
			ICraftingGridHelper craftingGridHelper
	) {
		List<List<ItemStack>> itemStacks = ingredients.stream()
				.map(ingredient -> Arrays.asList(ingredient.getItems()))
				.toList();
		craftingGridHelper.setInputs(craftingSlots, VanillaTypes.ITEM, itemStacks, width, height);
	}

	public static NonNullList<ItemStack> getFirstItemStacks(IRecipeSlotsView recipeSlots) {
		List<IRecipeSlotView> slotViews = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
		return slotViews.stream()
				.map(JeiUtil::getFirstItemStack)
				.collect(Collectors.toCollection(NonNullList::create));
	}

	private static ItemStack getFirstItemStack(IRecipeSlotView slotView) {
		return slotView.getDisplayedIngredient(VanillaTypes.ITEM)
			.or(() -> slotView.getIngredients(VanillaTypes.ITEM).findFirst())
			.map(ItemStack::copy)
			.orElse(ItemStack.EMPTY);
	}

}
