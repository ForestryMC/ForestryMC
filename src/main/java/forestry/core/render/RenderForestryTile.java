package forestry.core.render;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

import com.mojang.blaze3d.vertex.PoseStack;

public class RenderForestryTile<T extends BlockEntity> extends BlockEntityRenderer<T> {

	private final IForestryRenderer<T> renderer;
	private final RenderHelper helper = new RenderHelper();

	public RenderForestryTile(BlockEntityRenderDispatcher dispatcher, IForestryRenderer<T> renderer) {
		super(dispatcher);
		this.renderer = renderer;
	}

	@Override
	public void render(T tile, float partialTicks, PoseStack transformation, MultiBufferSource buffer, int combinedLight, int packetLight) {
		Level worldObj = tile.getLevel();
		if (worldObj == null || !worldObj.hasChunkAt(tile.getBlockPos())) {
			return;
		}
		helper.update(partialTicks, transformation, buffer, combinedLight, packetLight);
		renderer.renderTile(tile, helper);
	}
}
