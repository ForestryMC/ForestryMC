/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.TextureStitchEvent;

/**
 * Helper interface to register sprites.
 */
@FunctionalInterface
public interface ISpriteRegistry {
    /**
     * Registers a sprite to the registry
     *
     * @param sprite The path to the location of the sprite in the asset/modid/textures/ directory.
     * @return False if the sprite already was registered, true otherwise.
     */
    boolean addSprite(ResourceLocation sprite);

    /**
     * Creates a registry from the texture stitch event.
     *
     * @param event Event that is used by forge to register textures to a atlas.
     * @return The created registry.
     */
    static ISpriteRegistry fromEvent(TextureStitchEvent.Pre event) {
        return event::addSprite;
    }
}
