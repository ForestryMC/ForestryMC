package forestry.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * Handler for the rendering of a forestry tile entity and its item.
 *
 * @param <T> The type of the tile entity
 */
public interface IForestryRenderer<T extends TileEntity> {

    @Deprecated
    default void renderTile(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
    }

    /**
     * Renders the given tile entity.
     *
     * @param tile The tile entity that this handler renders.
     */
    void renderTile(T tile, RenderHelper helper);

    /**
     * Renders the given stack of the tile entity
     *
     * @param stack The item stack of the tile entity.
     */
    void renderItem(ItemStack stack, RenderHelper helper);

    default void renderItem(ItemStack stack) {
    }

    default void bindTexture(ResourceLocation location) {
        TextureManager texturemanager = Minecraft.getInstance().textureManager;
        if (texturemanager != null) {
            texturemanager.bindTexture(location);
        }

    }
}
