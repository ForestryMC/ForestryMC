/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelTransform;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Util methods used at the installation of the game or at the reloading or baking of resources like models or
 * textures.
 */
@OnlyIn(Dist.CLIENT)
public class ResourceUtil {

    private ResourceUtil() {
    }

    public static ITextComponent tryTranslate(String optionalKey, String defaultKey) {
        return tryTranslate(
                () -> new TranslationTextComponent(optionalKey),
                () -> new TranslationTextComponent(defaultKey)
        );
    }

    public static ITextComponent tryTranslate(Supplier<TranslationTextComponent> optionalKey, String defaultKey) {
        return tryTranslate(optionalKey, () -> new TranslationTextComponent(defaultKey));
    }

    public static ITextComponent tryTranslate(String optionalKey, Supplier<ITextComponent> defaultKey) {
        return tryTranslate(() -> new TranslationTextComponent(optionalKey), defaultKey);
    }

    /**
     * Tries to translate the optional key component. Returns the default key component if the first can't be translated.
     *
     * @return The optional component if it can be translated the other component otherwise.
     */
    public static ITextComponent tryTranslate(
            Supplier<TranslationTextComponent> optionalKey,
            Supplier<ITextComponent> defaultKey
    ) {
        TranslationTextComponent component = optionalKey.get();
        if (canTranslate(component)) {
            return component;
        } else {
            return defaultKey.get();
        }
    }

    public static boolean canTranslate(TranslationTextComponent component) {
        String translatedText = component.getString();
        return !translatedText.startsWith(component.getKey());
    }

    public static Minecraft client() {
        return Minecraft.getInstance();
    }

    public static IResourceManager resourceManager() {
        return client().getResourceManager();
    }

    public static TextureAtlasSprite getMissingTexture() {
        return getSprite(PlayerContainer.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation());
    }

    public static TextureAtlasSprite getSprite(ResourceLocation atlas, ResourceLocation sprite) {
        return client().getAtlasSpriteGetter(atlas).apply(sprite);
    }

    public static TextureAtlasSprite getBlockSprite(ResourceLocation location) {
        return getSprite(PlayerContainer.LOCATION_BLOCKS_TEXTURE, location);
    }

    public static TextureAtlasSprite getBlockSprite(String location) {
        return getBlockSprite(new ResourceLocation(location));
    }

    public static boolean resourceExists(ResourceLocation location) {
        try {
            resourceManager().getResource(location);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static BufferedReader createReader(IResource resource) {
        return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
    }

    @Nullable
    public static IResource getResource(ResourceLocation location) {
        try {
            return resourceManager().getResource(location);
        } catch (IOException e) {
            return null;
        }
    }

    public static List<IResource> getResources(ResourceLocation location) {
        try {
            return resourceManager().getAllResources(location);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    /**
     * @return The model from the item of the stack.
     */
    @Nullable
    public static IBakedModel getModel(ItemStack stack) {
        ItemRenderer renderItem = client().getItemRenderer();
        if (renderItem == null || renderItem.getItemModelMesher() == null) {
            return null;
        }
        return renderItem.getItemModelMesher().getItemModel(stack);
    }

    public static SimpleModelTransform loadTransform(ResourceLocation location) {
        return new SimpleModelTransform(PerspectiveMapWrapper.getTransforms(loadTransformFromJson(location)));
    }

    private static ItemCameraTransforms loadTransformFromJson(ResourceLocation location) {
        try (Reader reader = getReaderForResource(location)) {
            return BlockModel.deserialize(reader).getAllTransforms();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ItemCameraTransforms.DEFAULT;
    }

    private static Reader getReaderForResource(ResourceLocation location) throws IOException {
        ResourceLocation file = new ResourceLocation(
                location.getNamespace(),
                "models/" + location.getPath() + ".json"
        );
        IResource iresource = resourceManager().getResource(file);
        return new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
    }
}
