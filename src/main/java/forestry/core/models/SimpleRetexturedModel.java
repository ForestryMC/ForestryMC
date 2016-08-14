package forestry.core.models;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;

public class SimpleRetexturedModel extends BlankModel {
	
	public final IModel model;
	public IBakedModel bakedModel;
	
	public SimpleRetexturedModel(IModel model) {
		this.model = model;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return getModel().getQuads(state, side, rand);
	}
	
	@Override
	protected ItemOverrideList createOverrides() {
		return new RetexturedOverrideList(model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, new DefaultTextureGetter()));
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return getModel().getParticleTexture();
	}
	
	private IBakedModel getModel(){
		if(bakedModel == null){
			return bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.BLOCK, new DefaultTextureGetter());
		}
		return bakedModel;
	}
	
	private static class RetexturedOverrideList extends ItemOverrideList{
		public IBakedModel bakedModel;
		
		public RetexturedOverrideList(IBakedModel bakedModel) {
			super(Collections.emptyList());
			
			this.bakedModel = bakedModel;
		}
		
		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			return bakedModel;
		}
		
	}

}
