package forestry.core.blocks;

import forestry.core.render.RenderForestryItem;
import net.minecraft.client.Minecraft;

public interface IBlockTypeTesr extends IBlockType {
	@Override
	IMachinePropertiesTesr<?> getMachineProperties();
	
	default RenderForestryItem initRenderItem() {
		IMachineProperties<?> machineProperties = this.getMachineProperties();
		if (!(machineProperties instanceof IMachinePropertiesTesr<?> machinePropertiesTesr)) {
			return null;
		}
		if (machinePropertiesTesr.getRenderer() == null) {
			return null;
		}
		if (machinePropertiesTesr.getModelLayer() == null) {
			return null;
		}
		return new RenderForestryItem(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
				Minecraft.getInstance().getEntityModels(), 
				machinePropertiesTesr.getModelLayer(), 
				machinePropertiesTesr.getRenderer());	
	}
}
