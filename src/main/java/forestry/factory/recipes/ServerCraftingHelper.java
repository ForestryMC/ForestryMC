package forestry.factory.recipes;

import net.minecraft.world.item.crafting.RecipeManager;

public class ServerCraftingHelper {

	protected static RecipeManager adjustServer() {
		throw new NullPointerException("RecipeManager was null on the dedicated server");
	}
}
