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
package forestry.lepidopterology.render;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import forestry.api.lepidopterology.genetics.ButterflyChromosomes;
import forestry.api.lepidopterology.genetics.IAlleleButterflySpecies;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.config.Constants;
import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;

import genetics.api.GeneticHelper;
import genetics.api.alleles.IAlleleValue;
import genetics.api.organism.IOrganism;
import genetics.utils.AlleleUtils;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

@OnlyIn(Dist.CLIENT)
public class ButterflyItemModel extends AbstractBakedModel {

	private final ImmutableMap<String, BakedModel> subModels;
	private final Cache<Pair<String, Float>, BakedModel> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	public ButterflyItemModel(ImmutableMap<String, BakedModel> subModels) {
		this.subModels = subModels;
	}

	@Override
	protected ItemOverrides createOverrides() {
		return new OverrideList();
	}


	private class OverrideList extends ItemOverrides {
		public OverrideList() {
			super();
		}

		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int p_173469_) {
			IOrganism<IButterfly> organism = GeneticHelper.getOrganism(stack);
			IAlleleButterflySpecies species = organism.getAllele(ButterflyChromosomes.SPECIES, true);
			IAlleleValue<Float> size = organism.getAllele(ButterflyChromosomes.SIZE, true);
			Preconditions.checkNotNull(species);
			Preconditions.checkNotNull(size);
			BakedModel bakedModel = cache.getIfPresent(Pair.of(species.getRegistryName().getPath(), size));
			if (bakedModel != null) {
				return bakedModel;
			}
			float scale = 1F / 16F;
			float sizeValue = size.getValue();
			String identifier = species.getRegistryName().getPath();
			ModelState transform = new SimpleModelState(getTransformations(sizeValue));//-0.03125F, 0.25F - sizeValue * 0.37F, -0.03125F + sizeValue * scale, sizeValue * 1.4F
			bakedModel = new PerspectiveMapWrapper(new TRSRBakedModel(subModels.get(identifier), 0, 0, 0, 1), transform);
			cache.put(Pair.of(identifier, sizeValue), bakedModel);
			return bakedModel;
		}

		private ImmutableMap<ItemTransforms.TransformType, Transformation> getTransformations(float size) {
			float scale = 1F / 16F;
			float sSize = size * 1.15F;
			Vector3f scaledSize = new Vector3f(sSize, sSize, sSize);
			ImmutableMap.Builder<ItemTransforms.TransformType, Transformation> builder = ImmutableMap.builder();
			builder.put(ItemTransforms.TransformType.FIXED,
				new Transformation(new Vector3f(scale * 0.5F, scale - (size / 0.75F) * scale, scale * 1.25F), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
				new Transformation(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
				new Transformation(new Vector3f(0, -scale * 4.75F, 0), null, scaledSize, null));
			builder.put(ItemTransforms.TransformType.GUI,
				new Transformation(new Vector3f(0, -scale, 0), new Quaternion(new Vector3f(1, 0, 0), 90F, true), scaledSize, null));
			builder.put(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
				new Transformation(new Vector3f(0, 0, 0), null, scaledSize, null));
			return builder.build();
		}
	}

	private static class Geometry implements IUnbakedGeometry<Geometry> {

		public final ImmutableMap<String, String> subModels;

		public Geometry(ImmutableMap<String, String> subModels) {
			this.subModels = subModels;
		}

		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
			UnbakedModel modelButterfly = bakery.getModel(new ResourceLocation(Constants.MOD_ID, "item/butterfly"));
			if (!(modelButterfly instanceof BlockModel modelBlock)) {
				return null;
			}
			ImmutableMap.Builder<String, BakedModel> subModelBuilder = new ImmutableMap.Builder<>();
			for (Map.Entry<String, String> subModel : this.subModels.entrySet()) {
				String identifier = subModel.getKey();
				String texture = subModel.getValue();

				BlockModel model = new BlockModel(modelBlock.getParentLocation(), modelBlock.getElements(), ImmutableMap.of("butterfly", Either.left(new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(texture)))), modelBlock.hasAmbientOcclusion, modelBlock.getGuiLight(), modelBlock.getTransforms(), modelBlock.getOverrides());
				ResourceLocation location = new ResourceLocation(Constants.MOD_ID, "item/butterfly");
				ModelState transform = ResourceUtil.loadTransform(new ResourceLocation(Constants.MOD_ID, "item/butterfly"));
				subModelBuilder.put(identifier, model.bake(bakery, model, spriteGetter, transform, location, true));
			}
			return new ButterflyItemModel(subModelBuilder.build());
		}

		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return subModels.values().stream().map((location) -> new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(location))).collect(Collectors.toSet());
		}
	}

	public static class Loader implements IGeometryLoader<ButterflyItemModel.Geometry> {

		@Override
		public ButterflyItemModel.Geometry read(JsonObject modelContents, JsonDeserializationContext context) throws JsonParseException {
			ImmutableMap.Builder<String, String> subModels = new ImmutableMap.Builder<>();
			AlleleUtils.forEach(ButterflyChromosomes.SPECIES, (butterfly) -> {
				ResourceLocation registryName = butterfly.getRegistryName();
				subModels.put(registryName.getPath(), butterfly.getItemTexture().toString());
			});
			return new ButterflyItemModel.Geometry(subModels.build());
		}
	}
}
