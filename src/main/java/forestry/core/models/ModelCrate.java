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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.core.items.ItemCrated;
import forestry.core.utils.ItemStackUtil;
import forestry.storage.PluginStorage;

@SideOnly(Side.CLIENT)
public class ModelCrate extends BlankModel {

	//Cache not needed because each baked quad is only 116 bytes big (minium size) and you do not have more then 1000 CrateTypes would only eat up 8 MB of ram.
	private static Map<String, IBakedModel> cache = new HashMap<String, IBakedModel>();
	private static final String CUSTOM_CRATES = "forestry:item/crates/";

	/**
	 * Init the model with datas from the ModelBakeEvent.
	 */
	public static void onModelBake(ModelBakeEvent event){
		cache.clear();
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
			String crateUID = ItemStackUtil.getItemNameFromRegistry(crated).getResourcePath();
			IBakedModel model = cache.get(crateUID);
			if(model == null)
			{
				//Fastest list with a unknown quad size
				List<BakedQuad> list = new LinkedList<BakedQuad>();
				IBakedModel baseBaked = getModel(new ItemStack(PluginStorage.items.crate, 1, 1));
				for(BakedQuad quad : baseBaked.getQuads(null, null, 0L))
				{
					list.add(new BakedQuad(quad.getVertexData(), 100, quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
				}
				List<IBakedModel> textures = bakeModel(crated);
				for(IBakedModel bakybake : textures)
				{
					list.addAll(bakybake.getQuads(null, null, 0L));
				}
				model = new BakedCrateModel(list);
				cache.put(crateUID, model);
			}
			return model;
		}
		
	}
	
	public static class BakedCrateModel extends BlankModel
	{
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		private List<BakedQuad> emptyList = new ArrayList<BakedQuad>();
		
		public BakedCrateModel(List<BakedQuad> data)
		{
			quads.addAll(data);
		}
		
		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
		{
			return side == null ? quads : emptyList;
		}
	}
    
	@Override
	public boolean isGui3d() {
		return false;
	}
}
