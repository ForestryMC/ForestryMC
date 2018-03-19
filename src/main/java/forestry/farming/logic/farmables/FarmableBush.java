package forestry.farming.logic.farmables;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.farming.logic.crops.CropDestroy;

public class FarmableBush extends FarmableBase {

	public FarmableBush(IBlockState unripeState, IBlockState ripeState) {
		super(ItemStack.EMPTY, unripeState, ripeState, true);
	}
}
