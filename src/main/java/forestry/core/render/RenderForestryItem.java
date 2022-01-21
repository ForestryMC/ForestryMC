package forestry.core.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;

public class RenderForestryItem extends BlockEntityWithoutLevelRenderer {

	private final IForestryRenderer<?> renderer;
	private final RenderHelper helper = new RenderHelper();

	public RenderForestryItem(IForestryRenderer<?> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack transform, MultiBufferSource buffer, int combinedLight, int packetLight) {
		helper.update(0, transform, buffer, combinedLight, packetLight);
		renderer.renderItem(itemStack, helper);
	}
}
