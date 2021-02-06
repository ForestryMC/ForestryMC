package forestry.core.utils;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.crafting.RecipeManager;

public final class ClientUtils {
	@Nullable
	public static RecipeManager getRecipeManager() {
		ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
		if (connection != null) {
			return connection.getRecipeManager();
		}
		return null;
	}
}