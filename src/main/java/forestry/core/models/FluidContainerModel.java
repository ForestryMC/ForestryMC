package forestry.core.models;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class FluidContainerModel extends AbstractItemModel {
    private final IBakedModel emptyModel;
    private final IBakedModel filledModel;

    public FluidContainerModel(IBakedModel emptyModel, IBakedModel filledModel) {
        this.emptyModel = emptyModel;
        this.filledModel = filledModel;
    }

    @Override
    protected IBakedModel getOverride(IBakedModel model, ItemStack stack) {
        return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).map((handler) -> {
            FluidStack fluid = handler.getFluidInTank(0);
            return fluid.isEmpty() ? emptyModel : filledModel;
        }).orElse(emptyModel);
    }

    @Override
    protected boolean complexOverride() {
        return true;
    }

    public static class Geometry implements IModelGeometry<FluidContainerModel.Geometry> {

        public final String type;
        public final ResourceLocation empty;
        public final ResourceLocation filled;

        public Geometry(ResourceLocation empty, ResourceLocation filled, String type) {
            this.filled = filled;
            this.empty = empty;
            this.type = type;
        }

        @Override
        public IBakedModel bake(
                IModelConfiguration owner,
                ModelBakery bakery,
                Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                IModelTransform modelTransform,
                ItemOverrideList overrides,
                ResourceLocation modelLocation
        ) {
            return new FluidContainerModel(
                    bakery.getBakedModel(empty, modelTransform, spriteGetter),
                    bakery.getBakedModel(filled, modelTransform, spriteGetter)
            );
        }

        @Override
        public Collection<RenderMaterial> getTextures(
                IModelConfiguration owner,
                Function<ResourceLocation, IUnbakedModel> modelGetter,
                Set<Pair<String, String>> missingTextureErrors
        ) {
            return ImmutableList.of();
        }
    }

    public static class Loader implements IModelLoader<Geometry> {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public FluidContainerModel.Geometry read(
                JsonDeserializationContext deserializationContext,
                JsonObject modelContents
        ) {
            String empty = JSONUtils.getString(modelContents, "empty");
            String filled = JSONUtils.getString(modelContents, "filled");
            String type = JSONUtils.getString(modelContents, "type");
            return new FluidContainerModel.Geometry(new ResourceLocation(empty), new ResourceLocation(filled), type);
        }
    }
}
