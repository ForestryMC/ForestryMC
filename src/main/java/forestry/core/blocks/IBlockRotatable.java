package forestry.core.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockRotatable {

	void rotateAfterPlacement(EntityPlayer player, World world, BlockPos pos, EnumFacing side);

}
