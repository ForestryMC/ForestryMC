package forestry.energy.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.tiles.TileEngine;

public class MachinePropertiesEngine<T extends TileEngine> extends MachinePropertiesTesr<T> {
	public MachinePropertiesEngine(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, String particleTextureLocation) {
		super(teClass, name, renderer, particleTextureLocation);
	}
}
