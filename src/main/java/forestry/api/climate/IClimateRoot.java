package forestry.api.climate;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @since Forestry 5.8.1
 */
public interface IClimateRoot {

	/**
	 * @return the lister at the given position in the given world if there is any.
	 */
	@Nullable
	IClimateListener getListener(World world, BlockPos pos);

	IClimateState getTransformerState(World world, BlockPos pos);

	IClimateState getBiomeState(World worldObj, BlockPos coordinates);

	/**
	 * @return Create a climate provider.
	 */
	IClimateProvider getDefaultClimate(World world, BlockPos pos);

	IWorldClimateHolder getWorldClimate(World world);
}
