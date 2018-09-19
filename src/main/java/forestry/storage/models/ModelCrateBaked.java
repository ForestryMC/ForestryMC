package forestry.storage.models;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.client.model.BakedItemModel;

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
			contentModel = contentModel.bake();
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

		public ContentModel bake(){
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
		public ContentModel bake() {
			IBakedModel bakedModel = ModelUtil.getModel(content);
			if(bakedModel != null) {
				if(bakedModel instanceof BakedItemModel) {
					quads.addAll(new TRSRBakedModel(bakedModel, -0.0625F, 0, 0.0625F, 0.5F).getQuads(null, null, 0L));
					quads.addAll(new TRSRBakedModel(bakedModel, -0.0625F, 0, -0.0625F, 0.5F).getQuads(null, null, 0L));
				}else{
					quads.addAll(new TRSRBakedModel(bakedModel, -0.0625F, 0, 0, 0.5F).getQuads(null, null, 0L));
				}
			}
			return new ContentModel(quads);
		}

		@Override
		public boolean hasBakedModel() {
			return true;
		}
	}
}
