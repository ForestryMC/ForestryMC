package forestry.energy.blocks;

import javax.annotation.Nullable;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.tiles.TileEngine;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class MachinePropertiesEngine<T extends TileEngine> extends MachinePropertiesTesr<T> {
	public MachinePropertiesEngine(Class<T> teClass, String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, String particleTextureLocation) {
		super(teClass, name, renderer, particleTextureLocation);
	}
}
