package forestry.core.render;

import forestry.core.items.ItemBlockBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.IItemRenderProperties;
import net.minecraftforge.common.util.NonNullLazy;

public class RenderForestryItemProperties implements IItemRenderProperties {
	private final NonNullLazy<RenderForestryItem> renderItem;

	// initializeClient is called during ItemBlockBase super
	// field itemBlock.blockTypeTesr is not initialized until after constructor done
	// lazy has two purposes:
	// - itemBlock.blockTypeTesr filled in after 
	// - getEntityModels not available until later in loading
	public RenderForestryItemProperties(ItemBlockBase<?> itemBlock) {
		this.renderItem = NonNullLazy.of(() -> itemBlock.blockTypeTesr.initRenderItem());
	}

	@Override
	public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
		return renderItem.get();
	}
}
