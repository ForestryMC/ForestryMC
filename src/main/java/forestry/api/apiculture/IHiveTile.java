package forestry.api.apiculture;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHiveTile {
	/**
	 * Call to calm agitated bees. Used by the smoker to stop wild bees from attacking as much.
	 * Bees will not stay calm for very long.
	 */
	void calmBees();

	boolean isAngry();

	/**
	 * Called when the hive is attacked.
	 */
	void onAttack(World world, BlockPos pos, EntityPlayer player);

	/**
	 * Called when the hive is broken.
	 */
	void onBroken(World world, BlockPos pos, EntityPlayer player, boolean canHarvest);
}
