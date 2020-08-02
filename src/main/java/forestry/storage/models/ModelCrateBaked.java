package forestry.storage.models;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.common.model.TransformationHelper;

import forestry.core.models.AbstractBakedModel;
import forestry.core.models.TRSRBakedModel;
import forestry.core.utils.ResourceUtil;

public class ModelCrateBaked extends AbstractBakedModel {
    private static final float CONTENT_RENDER_OFFSET_X = 1f / 16f; // how far to offset content model from the left edge of the crate model
    private static final float CONTENT_RENDER_OFFSET_Z = 1f / 128f; // how far to render the content model away from the crate model
    private static final float CONTENT_RENDER_BLOCK_Z_SCALE = 1f / 16f + (2f * CONTENT_RENDER_OFFSET_Z); // how much to scale down blocks so they look flat on the crate model

    private ContentModel contentModel;

    ModelCrateBaked(List<BakedQuad> quads) {
        this.contentModel = new ContentModel(quads);
    }

    ModelCrateBaked(List<BakedQuad> quads, ItemStack content) {
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
            IBakedModel bakedModel = ResourceUtil.getModel(content);
            if (bakedModel != null) {
                IBakedModel guiModel = bakedModel.handlePerspective(ItemCameraTransforms.TransformType.GUI, new MatrixStack());
                if (bakedModel instanceof BakedItemModel) {
                    TransformationMatrix frontTransform = new TransformationMatrix(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, CONTENT_RENDER_OFFSET_Z),
                            null,
                            new Vector3f(0.5F, 0.5F, 1F),
                            null);
                    TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
                    quads.addAll(frontModel.getQuads(null, null, new Random(0L)));
                    TransformationMatrix backTransform = new TransformationMatrix(new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, -CONTENT_RENDER_OFFSET_Z),
                            null,
                            new Vector3f(0.5F, 0.5F, 1f),
                            TransformationHelper.quatFromXYZ(new Vector3f(0, (float) Math.PI, 0), false)
                            /*TransformationMatrix.quatFromYXZ((float) Math.PI, 0, 0)*/);//TODO: Test if this works
                    TRSRBakedModel backModel = new TRSRBakedModel(guiModel, backTransform);
                    quads.addAll(backModel.getQuads(null, null, new Random(0L)));
                } else {
                    TransformationMatrix frontTransform = new TransformationMatrix(
                            new Vector3f(-CONTENT_RENDER_OFFSET_X, 0, 0),
                            null,
                            new Vector3f(0.5F, 0.5F, CONTENT_RENDER_BLOCK_Z_SCALE),
                            null);
                    TRSRBakedModel frontModel = new TRSRBakedModel(guiModel, frontTransform);
                    quads.addAll(frontModel.getQuads(null, null, new Random(0L)));
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
