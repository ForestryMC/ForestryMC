package forestry.factory.recipes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.server.integrated.IntegratedServer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

class ClientCraftingHelper {
	@OnlyIn(Dist.CLIENT)
	private static final RecipeManager DUMMY = new RecipeManager();

	@OnlyIn(Dist.CLIENT)
	static RecipeManager adjustClient() {
		Minecraft minecraft = Minecraft.getInstance();
		IntegratedServer integratedServer = minecraft.getSingleplayerServer();

		if (integratedServer != null) {
			if (integratedServer.isSameThread()) {
				throw new NullPointerException("RecipeManager was null on the integrated server");
			}
		}

		ClientPlayNetHandler connection = minecraft.getConnection();

		if (connection == null) {
			// Usage of this code path is probably a bug
			return DUMMY;
		} else {
			return connection.getRecipeManager();
		}
	}
}
