package forestry.cultivation.blocks;

import java.util.Locale;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.core.IBlockSubtype;
import forestry.core.blocks.BlockBase;
import forestry.core.render.ParticleRender;
import forestry.cultivation.tiles.TilePlanter;

public class BlockPlanter extends BlockBase<BlockTypePlanter> {
	private Mode mode;

	//TODO can probably propagate mode further through the code
	public enum Mode implements IBlockSubtype {
		MANUAL,
		MANAGED;

		@Override
		public String getSerializedName() {
			return toString().toLowerCase(Locale.ENGLISH);
		}
	}

	public BlockPlanter(BlockTypePlanter blockType, Mode mode) {
		super(blockType, Material.WOOD);
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		if (blockType == BlockTypePlanter.FARM_ENDER) {
			for (int i = 0; i < 3; ++i) {
				ParticleRender.addPortalFx(worldIn, pos, rand);
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		BlockEntity tile = super.newBlockEntity(pos, state);

		if (tile instanceof TilePlanter planter) {
			planter.setManual(getMode());
		}

		return tile;
	}
}
