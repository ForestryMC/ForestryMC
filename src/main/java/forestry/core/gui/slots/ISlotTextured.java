package forestry.core.gui.slots;

import javax.annotation.Nullable;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import forestry.core.render.TextureManagerForestry;

public interface ISlotTextured {

    default Function<ResourceLocation, TextureAtlasSprite> getBackgroundAtlas() {
        return TextureManagerForestry.getInstance().getSpriteUploader()::getSprite;
    }

    @Nullable
    ResourceLocation getBackgroundTexture();
}
