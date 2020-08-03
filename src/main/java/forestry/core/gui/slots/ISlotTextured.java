package forestry.core.gui.slots;

import forestry.core.render.TextureManagerForestry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public interface ISlotTextured {

    default Function<ResourceLocation, TextureAtlasSprite> getBackgroundAtlas() {
        return TextureManagerForestry.getInstance().getSpriteUploader()::getSprite;
    }

    @Nullable
    ResourceLocation getBackgroundTexture();
}
