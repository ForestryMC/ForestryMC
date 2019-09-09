package forestry.core.render;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class RenderForestryItem extends ItemStackTileEntityRenderer {

	private final IForestryRenderer<?> renderer;

	public RenderForestryItem(IForestryRenderer<?> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void renderByItem(ItemStack itemStack) {
		renderer.renderItem(itemStack);
	}
}
