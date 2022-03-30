package forestry.factory.recipes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.client.server.IntegratedServer;

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

		ClientPacketListener connection = minecraft.getConnection();

		if (connection == null) {
			// Usage of this code path is probably a bug
			return DUMMY;
		} else {
			return connection.getRecipeManager();
		}
	}
}
