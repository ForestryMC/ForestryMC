package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
 
public class RenderForestryItem extends BlockEntityWithoutLevelRenderer {

	private final IForestryRenderer<?> renderer;
	private final RenderHelper helper = new RenderHelper();

	public RenderForestryItem(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet, IForestryRenderer<?> renderer) {
		super(pBlockEntityRenderDispatcher, pEntityModelSet);
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack transform, MultiBufferSource buffer, int combinedLight, int packetLight) {
		helper.update(0, transform, buffer, combinedLight, packetLight);
		renderer.renderItem(itemStack, helper);
	}
}

