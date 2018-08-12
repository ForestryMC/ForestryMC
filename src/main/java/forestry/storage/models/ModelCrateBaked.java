package forestry.storage.models;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import forestry.core.models.BlankModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ModelUtil;

public class ModelCrateBaked extends BlankModel {

	private ContentModel contentModel;

	ModelCrateBaked(List<BakedQuad> quads) {
		this.contentModel = new ContentModel(quads);
	}

	ModelCrateBaked(List<BakedQuad> quads, ItemStack content) {
		this.contentModel = new RawContentModel(quads, content);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		if(contentModel.hasBakedModel()) {
			contentModel = contentModel.bake(rand);
		}
		return contentModel.getQuads();
	}

	private class ContentModel{
		final List<BakedQuad> quads;

		private ContentModel(List<BakedQuad> quads) {
			this.quads = quads;
		}

		public List<BakedQuad> getQuads(){
			return quads;
		}

		public ContentModel bake(long rand){
			return this;
		}

		public boolean hasBakedModel(){
			return false;
		}
	}

	private class RawContentModel extends ContentModel {
		private final ItemStack content;

		private RawContentModel(List<BakedQuad> quads, ItemStack content) {
			super(quads);
			this.content = content;
		}

		@Override
		public ContentModel bake(long rand) {
			IBakedModel bakedModel = ModelUtil.getModel(content);
			if(bakedModel != null) {
				quads.addAll(new TRSRBakedModel(bakedModel, -0.0625F, 0, 0.0625F, 0.5F).getQuads(null, null, rand));
				quads.addAll(new TRSRBakedModel(bakedModel, -0.0625F, 0, -0.0625F, 0.5F).getQuads(null, null, rand));
			}
			return new ContentModel(quads);
		}

		@Override
		public boolean hasBakedModel() {
			return true;
		}
	}
}
