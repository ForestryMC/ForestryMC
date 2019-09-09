package forestry.core.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IForestryRenderer<T extends TileEntity> {

	void renderTile(T tile, double x, double y, double z, float partialTicks, int destroyStage);

	void renderItem(ItemStack stack);

	default void bindTexture(ResourceLocation location) {
		TextureManager texturemanager = Minecraft.getInstance().textureManager;
		if (texturemanager != null) {
			texturemanager.bindTexture(location);
		}

	}
}
