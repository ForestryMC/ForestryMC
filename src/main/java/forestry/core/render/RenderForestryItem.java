package forestry.core.render;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

import com.mojang.blaze3d.matrix.MatrixStack;

public class RenderForestryItem extends ItemStackTileEntityRenderer {

    private final IForestryRenderer<?> renderer;
    private final RenderHelper helper = new RenderHelper();

    public RenderForestryItem(IForestryRenderer<?> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void func_239207_a_(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack transform, IRenderTypeBuffer buffer, int combinedLight, int packetLight) {
        helper.update(0, transform, buffer, combinedLight, packetLight);
        renderer.renderItem(itemStack, helper);
    }
}
