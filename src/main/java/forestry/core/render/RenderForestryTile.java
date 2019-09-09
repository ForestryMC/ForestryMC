package forestry.core.render;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RenderForestryTile<T extends TileEntity> extends TileEntityRenderer<T> {

	private final IForestryRenderer<T> renderer;

	public RenderForestryTile(IForestryRenderer<T> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void render(T tile, double x, double y, double z, float partialTicks, int destroyStage) {
		World worldObj = tile.getWorld();
		if (worldObj == null || !worldObj.isBlockLoaded(tile.getPos())) {
			return;
		}
		renderer.renderTile(tile, x, y, z, partialTicks, destroyStage);
	}
}
