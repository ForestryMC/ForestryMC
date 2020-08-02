package forestry.core.render;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import forestry.api.core.ISpriteRegistry;

public class ForestrySpriteUploader extends SpriteUploader implements ISpriteRegistry {
    private final Set<ResourceLocation> registeredSprites = new HashSet<>();

    public ForestrySpriteUploader(TextureManager manager, ResourceLocation atlasLocation, String prefix) {
        super(manager, atlasLocation, prefix);
    }

    public boolean addSprite(ResourceLocation location) {
        return this.registeredSprites.add(location);
    }

    @Override
    protected Stream<ResourceLocation> getResourceLocations() {
        return Collections.unmodifiableSet(this.registeredSprites).stream();
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }
}
