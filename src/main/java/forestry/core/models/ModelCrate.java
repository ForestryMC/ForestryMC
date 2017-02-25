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
package forestry.core.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.core.items.ItemCrated;
import forestry.core.utils.ItemStackUtil;
import forestry.storage.PluginStorage;

@SideOnly(Side.CLIENT)
public class ModelCrate extends BlankModel {

	private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> itemTransforms;
	private static Map<String, IBakedModel> cache = new HashMap<String, IBakedModel>();

	/**
	 * Init the model with datas from the ModelBakeEvent.
	 */
	public static void onModelBake(ModelBakeEvent event){
		cache.clear();
		itemTransforms = getMap(new ResourceLocation("minecraft:models/item/generated"));
	}
	
	public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getMap(ResourceLocation par1){
		return IPerspectiveAwareModel.MapWrapper.getTransforms(getTransformFromJson(par1));
	}
	
	private static Reader getReaderForResource(ResourceLocation location) throws IOException{
		ResourceLocation file = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
		IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(file);
		return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
	}
	
	private static ItemCameraTransforms getTransformFromJson(ResourceLocation par1){
		try{
			return ModelBlock.deserialize(getReaderForResource(par1)).getAllTransforms();
		}catch(Exception e){
			e.printStackTrace();
		}
		return ItemCameraTransforms.DEFAULT;
	}


	/**
	 * @return The model from the item of the stack.
	 */
	private IBakedModel getModel(ItemStack stack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
	}
	
	@Override
	public ItemOverrideList createOverrides() {
		return new CrateOverrideList();
	}
	
	/**
	 * Bake the crate model.
	 */
	private List<IBakedModel> bakeModel(ItemCrated crateItem) {
		List<IBakedModel> models = new ArrayList<>();
		ItemStack contained = crateItem.getContained();
		if (contained != null) {
			IBakedModel containedModel = getModel(contained);
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, 0.0625F, 0.5F));
			models.add(new TRSRBakedModel(containedModel, -0.0625F, 0, -0.0625F, 0.5F));
		}
		return models;
	}
	
	private class CrateOverrideList extends ItemOverrideList{
		
		public CrateOverrideList() {
			super(new ArrayList());
		}
		
		/**
		 * Bake the crated model
		 */
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			ItemCrated crated = (ItemCrated) stack.getItem();
			String crateUID = crated.getUID();
			if(crateUID != null){
				IBakedModel model = cache.get(crateUID);
				if(model == null){
					//Fastest list with a unknown quad size
					List<BakedQuad> list = new LinkedList<BakedQuad>();
					IBakedModel baseBaked = getModel(new ItemStack(PluginStorage.items.crate, 1, 1));
					for(BakedQuad quad : ForgeHooksClient.handleCameraTransforms(baseBaked, TransformType.GROUND, false).getQuads(null, null, 0L)){
						list.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
					}
					List<IBakedModel> textures = bakeModel(crated);
					for(IBakedModel bakybake : textures){
						list.addAll(ForgeHooksClient.handleCameraTransforms(bakybake, TransformType.GROUND, false).getQuads(null, null, 0L));
					}
					model = new BakedCrateModel(list);
					cache.put(crateUID, model);
				}
				return model;
			}
			return originalModel;
		}
		
	}
	
	public static class BakedCrateModel extends BlankModel implements IPerspectiveAwareModel{
		BakedCrateModel other;
		boolean gui;
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		private List<BakedQuad> emptyList = new ArrayList<BakedQuad>();
		
		public BakedCrateModel(BakedCrateModel noneGui){
			gui = true;
			other = noneGui;
			for(BakedQuad quad : other.quads){
				if(quad.getFace() == EnumFacing.SOUTH){
					quads.add(quad);
				}
			}
		}
		
		public BakedCrateModel(List<BakedQuad> data){
			quads.addAll(data);
			gui = false;
			other = new BakedCrateModel(this);
		}
		
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
			return side == null ? quads : emptyList;
		}
		
		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType){
			Pair<? extends IBakedModel, Matrix4f> pair = IPerspectiveAwareModel.MapWrapper.handlePerspective(this, itemTransforms, cameraTransformType);
			if(cameraTransformType == TransformType.GUI && !gui && pair.getRight() == null){
				return Pair.of(other, null);
			}else if(cameraTransformType != TransformType.GUI && gui){
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
