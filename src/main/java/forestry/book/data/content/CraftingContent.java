package forestry.book.data.content;

import forestry.api.book.BookContent;
import forestry.book.data.CraftingData;
import forestry.book.gui.elements.CraftingElement;
import forestry.core.gui.elements.lib.IElementGroup;
import forestry.core.gui.elements.lib.IGuiElement;
import forestry.core.gui.elements.lib.IGuiElementFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A book content that displays one ore more crafting recipes.
 */
@OnlyIn(Dist.CLIENT)
public class CraftingContent extends BookContent<CraftingData> {
    @Override
    public Class<CraftingData> getDataClass() {
        return CraftingData.class;
    }

    @Override
    public boolean addElements(
            IElementGroup page,
            IGuiElementFactory factory,
            @Nullable BookContent previous,
            @Nullable IGuiElement previousElement,
            int pageHeight
    ) {
        if (data == null || data.locations.length == 0) {
            return false;
        }

        List<IRecipe> recipes = new LinkedList<>();
        for (ResourceLocation location : data.locations) {
            //TODO sides
            Map<ResourceLocation, IRecipe<CraftingInventory>> recipeMap =
                    Minecraft.getInstance().world.getRecipeManager().getRecipes(IRecipeType.CRAFTING);
            IRecipe recipe = recipeMap.get(location);
            if (recipe != null) {
                recipes.add(recipe);
            }
        }

        page.add(new CraftingElement(0, 0, recipes.toArray(new IRecipe[0])));
        return true;
    }
}
