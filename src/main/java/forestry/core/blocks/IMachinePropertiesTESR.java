package forestry.core.blocks;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public interface IMachinePropertiesTESR extends IMachineProperties {

	TileEntitySpecialRenderer getRenderer();
	
}
