package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public final class ClientUtils {

    private ClientUtils() {
    }

    @Nullable
    public static RecipeManager getRecipeManager() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if(connection != null){
            return connection.getRecipeManager();
        }
        return null;
    }
}
