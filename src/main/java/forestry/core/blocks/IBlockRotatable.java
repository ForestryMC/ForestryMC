package forestry.core.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockRotatable {

	void rotateAfterPlacement(PlayerEntity player, World world, BlockPos pos, Direction side);

}
