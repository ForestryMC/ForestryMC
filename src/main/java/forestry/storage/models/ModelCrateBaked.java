package forestry.storage.models;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.common.model.TRSRTransformation;

import forestry.core.models.BlankModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ModelUtil;

public class ModelCrateBaked extends BlankModel {
	private static final float CONTENT_RENDER_OFFSET_X = 1f/16f; // how far to offset content model from the left edge of the crate model
	private static final float CONTENT_RENDER_OFFSET_Z = 1f/128f; // how far to render the content model away from the crate model
	private static final float CONTENT_RENDER_BLOCK_Z_SCALE = 1f/16f + (2f * CONTENT_RENDER_OFFSET_Z); // how much to scale down blocks so they look flat on the crate model

	private ContentModel contentModel;

	ModelCrateBaked(List<BakedQuad> quads) {
		this.contentModel = new ContentModel(quads);
	}

	ModelCrateBaked(List<BakedQuad> quads, ItemStack content) {
		this.contentModel = new RawContentModel(quads, content);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		if (side != null) {
			return ImmutableList.of();
		}
		if (contentModel.hasBakedModel()) {
			contentModel = contentModel.bake();
		}
		return contentModel.getQuads();
	}

	private class ContentModel {
		final List<BakedQuad> quads;

		private ContentModel(List<BakedQuad> quads) {
			this.quads = quads;
		}

		public List<BakedQuad> getQuads() {
			return quads;
		}

		public ContentModel bake() {
			return this;
		}

		public boolean hasBakedModel() {
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
			if (bakedModel != null) {
				IBakedModel guiModel = bakedModel.handlePerspective(ItemCameraTransforms.TransformType.GUI).getKey();
				if (bakedModel instanceof BakedItemModel) {
					TRSRTransformation frontTransform = new TRSRTransformation(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, CONTENT_RENDER_OFFSET_Z),
						null,
						new Vector3f(0.5F, 0.5F, 1F),
						null);
					TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
					quads.addAll(frontModel.getQuads(null, null, 0L));
					TRSRTransformation backTransform = new TRSRTransformation(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, -CONTENT_RENDER_OFFSET_Z),
						null,
						new Vector3f(0.5F, 0.5F, 1f),
						TRSRTransformation.quatFromYXZ((float) Math.PI, 0, 0));
					TRSRBakedModel backModel = new TRSRBakedModel(guiModel, backTransform);
					quads.addAll(backModel.getQuads(null, null, 0L));
				} else {
					TRSRTransformation frontTransform = new TRSRTransformation(
						new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, 0),
						null,
						new Vector3f(0.5F, 0.5F, CONTENT_RENDER_BLOCK_Z_SCALE),
						null);
					TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
					quads.addAll(frontModel.getQuads(null, null, 0L));
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
