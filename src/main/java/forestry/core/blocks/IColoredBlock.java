package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//TODO use BlockColors and ItemColors for this
//TODO how to load this. Look for what vanilla does for shulker I guess? (probably flatten...
public interface IColoredBlock {
	@OnlyIn(Dist.CLIENT)
	int colorMultiplier(BlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex);
}
