package forestry.energy.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import forestry.core.blocks.MachinePropertiesTesr;
import forestry.core.tiles.TileEngine;
import forestry.core.tiles.TileUtil;

public class MachinePropertiesEngine<T extends TileEngine> extends MachinePropertiesTesr<T> {
	public MachinePropertiesEngine(int meta, @Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, String particleTextureLocation) {
		super(meta, teClass, name, renderer, particleTextureLocation);
	}

	@Override
	public boolean isSolidOnSide(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EnumFacing side) {
		TileEngine tile = TileUtil.getTile(world, pos, TileEngine.class);
		if (tile == null) {
			return false;
		}

		return tile.getOrientation().getOpposite() == side;
	}
}
