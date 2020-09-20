package forestry.core.gui.slots;

import com.mojang.datafixers.util.Pair;
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
    Pair<ResourceLocation, ResourceLocation> getBackground();
}
