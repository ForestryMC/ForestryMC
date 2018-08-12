/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http:www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.storage.models;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.models.BlankModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.ModelManager;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.Log;
import forestry.core.utils.ModelUtil;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;

@SideOnly(Side.CLIENT)
public class ModelBakedCrate extends BlankModel {
	private static final Map<String, IBakedModel> cache = new HashMap<>();
	private static final String CUSTOM_CRATES = "forestry:item/crates/";

	/**
	 * Init the model with datas from the ModelBakeEvent.
	 */
	public static void onModelBake(ModelBakeEvent event) {
		cache.clear();
	}

	@Override
	public ItemOverrideList createOverrides() {
		return new CrateOverrideList();
	}
	
	@Nullable
	private static IBakedModel getCustomContentModel(ItemCrated crateItem){
		ResourceLocation registryName = crateItem.getRegistryName();
		String containedName = registryName.getResourcePath().replace("crated.", "");
		ResourceLocation location = new ResourceLocation(CUSTOM_CRATES + containedName);
		IModel model;
		try{
			model = ModelLoaderRegistry.getModel(location);
		}catch(Exception e){
			return null;
		}
		return model.bake(ModelManager.getInstance().getDefaultItemState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
	}

	/**
	 * Bake the crate model.
	 */
	private List<IBakedModel> bakeModel(ItemCrated crateItem) {
		List<IBakedModel> models = new ArrayList<>();
		ItemStack contained = crateItem.getContained();
		if (!contained.isEmpty()) {
			IBakedModel containedModel = getCustomContentModel(crateItem);
			if(containedModel == null){
				containedModel = ModelUtil.getModel(contained);
			}
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, 0.0625F, 0.5F));
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, -0.0625F, 0.5F));
		}
		return models;
	}

	private class CrateOverrideList extends ItemOverrideList {

		public CrateOverrideList() {
			super(new ArrayList<>());
		}

		/**
		 * Bake the crated model
		 */
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			ItemCrated crated = (ItemCrated) stack.getItem();
			ResourceLocation itemName = crated.getRegistryName();
			if(itemName == null){
				IBakedModel baseCrateModel = cache.get("base");
				if(baseCrateModel == null){
					baseCrateModel = ModelUtil.getModel(new ItemStack(ModuleCrates.getItems().crate, 1, 1));
					baseCrateModel = ForgeHooksClient.handleCameraTransforms(baseCrateModel, TransformType.GROUND, false);
				}
				Log.error("Failed to load the model of the create item: {}, please report this to the authors of forestry.", itemName);
				return baseCrateModel;
			}
			String crateUID = itemName.getResourcePath();
			IBakedModel model = cache.get(crateUID);
			if (model == null) {
				//Fastest list with a unknown quad size
				List<BakedQuad> list = new LinkedList<>();
				IBakedModel baseCrateModel = cache.get("base");
				if(baseCrateModel == null){
					baseCrateModel = ModelUtil.getModel(new ItemStack(ModuleCrates.getItems().crate, 1, 1));
					baseCrateModel = ForgeHooksClient.handleCameraTransforms(baseCrateModel, TransformType.GROUND, false);
				}
				for (BakedQuad quad : baseCrateModel.getQuads(null, null, 0L)) {
					list.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
				}
				List<IBakedModel> textures = bakeModel(crated);
				for (IBakedModel bakybake : textures) {
					list.addAll(ForgeHooksClient.handleCameraTransforms(bakybake, TransformType.GROUND, false).getQuads(null, null, 0L));
				}
				model = new BakedCrateModel(list);
				cache.put(crateUID, model);
			}
			return model;
		}

	}
	
	public static class BakedCrateModel extends BlankModel implements IBakedModel {
		private final BakedCrateModel other;
		private final boolean gui;
		private final List<BakedQuad> quads = new ArrayList<>();
		private final List<BakedQuad> emptyList = new ArrayList<>();

		public BakedCrateModel(BakedCrateModel noneGui) {
			gui = true;
			other = noneGui;
			for (BakedQuad quad : other.quads) {
				if (quad.getFace() == EnumFacing.SOUTH) {
					quads.add(quad);
				}
			}
		}

		public BakedCrateModel(List<BakedQuad> data) {
			quads.addAll(data);
			gui = false;
			other = new BakedCrateModel(this);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			return side == null ? quads : emptyList;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			Pair<? extends IBakedModel, Matrix4f> pair = PerspectiveMapWrapper.handlePerspective(this, ModelManager.getInstance().getDefaultItemState(), cameraTransformType);
			if (cameraTransformType == TransformType.GUI && !gui && pair.getRight() == null) {
				return Pair.of(other, null);
			} else if (cameraTransformType != TransformType.GUI && gui) {
				return Pair.of(other, pair.getRight());
			}
			return pair;
		}
	}

	@Override
	public boolean isGui3d() {
		return false;
	}
}
