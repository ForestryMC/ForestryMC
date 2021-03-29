package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public final class ClientUtils {

    private ClientUtils() {
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
        if(connection != null){
            return connection.getRecipeManager();
        }
        return null;
    }
}
