package forestry.core.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RenderForestryTile<T extends TileEntity> extends TileEntityRenderer<T> {

    private final IForestryRenderer<T> renderer;
    private final RenderHelper helper = new RenderHelper();

    public RenderForestryTile(TileEntityRendererDispatcher dispatcher, IForestryRenderer<T> renderer) {
        super(dispatcher);
        this.renderer = renderer;
    }

    @Override
    public void render(T tile, float partialTicks, MatrixStack transformation, IRenderTypeBuffer buffer, int combinedLight, int packetLight) {
        World worldObj = tile.getWorld();
        if (worldObj == null || !worldObj.isBlockLoaded(tile.getPos())) {
            return;
        }
        helper.update(partialTicks, transformation, buffer, combinedLight, packetLight);
        renderer.renderTile(tile, helper);
    }
}
