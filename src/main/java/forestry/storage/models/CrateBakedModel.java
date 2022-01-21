package forestry.storage.models;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.common.model.TransformationHelper;

import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;

public class CrateBakedModel extends AbstractBakedModel {
	private static final float CONTENT_RENDER_OFFSET_X = 1f / 16f; // how far to offset content model from the left edge of the crate model
	private static final float CONTENT_RENDER_OFFSET_Z = 1f / 512f; // how far to render the content model away from the crate model
	private static final float CONTENT_RENDER_BLOCK_Z_SCALE = 1f / 16f + CONTENT_RENDER_OFFSET_Z; // how much to scale down blocks so they look flat on the crate model

	private ContentModel contentModel;

	CrateBakedModel(List<BakedQuad> quads) {
		this.contentModel = new ContentModel(quads);
	}

	CrateBakedModel(List<BakedQuad> quads, ItemStack content) {
		this.contentModel = new RawContentModel(quads, content);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		if (side != null) {
			return ImmutableList.of();
		}
		if (contentModel.hasBakedModel()) {
			contentModel = contentModel.bake();
		}
		return contentModel.getQuads();
	}

	private static class ContentModel {
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

	private static class RawContentModel extends ContentModel {
		private final ItemStack content;

		private RawContentModel(List<BakedQuad> quads, ItemStack content) {
			super(quads);
			this.content = content;
		}

		@Override
		public ContentModel bake() {
			BakedModel bakedModel = ResourceUtil.getModel(content);
			if (bakedModel != null) {
				BakedModel guiModel = bakedModel.handlePerspective(ItemTransforms.TransformType.GUI, new PoseStack());
				//TODO: Currently very hacky, find a better way to differentiate between item and block
				List<BakedQuad> general = guiModel.getQuads(null, null, new Random(0L));
				if (!general.isEmpty()) {
					Transformation frontTransform = new Transformation(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, CONTENT_RENDER_OFFSET_Z),
						null,
						new Vector3f(0.5F, 0.5F, 1F),
						null);
					TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
					quads.addAll(frontModel.getQuads(null, null, new Random(0L)));
					Transformation backTransform = new Transformation(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, -CONTENT_RENDER_OFFSET_Z),
						null,
						new Vector3f(0.5F, 0.5F, 1f),
						TransformationHelper.quatFromXYZ(new Vector3f(0, (float) Math.PI, 0), false));
					TRSRBakedModel backModel = new TRSRBakedModel(guiModel, backTransform);
					quads.addAll(backModel.getQuads(null, null, new Random(0L)));
				} else {
					Transformation frontTransform = new Transformation(
						new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, 0),
						null,
						new Vector3f(0.5F, 0.5F, CONTENT_RENDER_BLOCK_Z_SCALE),
						null);
					TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
					for (Direction direction : Direction.VALUES) {
						quads.addAll(frontModel.getQuads(null, direction, new Random(0L)));
					}
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
