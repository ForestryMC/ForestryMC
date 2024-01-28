package forestry.core.models;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

public class FluidContainerModel extends AbstractItemModel {
	private final BakedModel emptyModel;
	private final BakedModel filledModel;

	public FluidContainerModel(BakedModel emptyModel, BakedModel filledModel) {
		this.emptyModel = emptyModel;
		this.filledModel = filledModel;
	}

	@Override
	protected BakedModel getOverride(BakedModel model, ItemStack stack) {
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).map((handler) -> {
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
		public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
			return new FluidContainerModel(
				bakery.bake(empty, modelTransform, spriteGetter),
				bakery.bake(filled, modelTransform, spriteGetter)
			);
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return ImmutableList.of();
		}
	}

	public static class Loader implements IModelLoader<Geometry> {
		@Override
		public void onResourceManagerReload(ResourceManager resourceManager) {
		}

		@Override
		public FluidContainerModel.Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			String empty = GsonHelper.getAsString(modelContents, "empty");
			String filled = GsonHelper.getAsString(modelContents, "filled");
			String type = GsonHelper.getAsString(modelContents, "type");
			return new FluidContainerModel.Geometry(new ResourceLocation(empty), new ResourceLocation(filled), type);
		}
	}
}
