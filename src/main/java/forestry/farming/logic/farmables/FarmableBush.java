package forestry.farming.logic.farmables;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmable;
import forestry.api.genetics.IFruitBearer;
import forestry.api.genetics.IFruitFamily;
import forestry.farming.logic.crops.CropFruit;

public class FarmableBush implements IFarmable, IFruitBearer {		//TODO: BOP and Natura bushes - find a way to get blockstates

	private IBlockState unripeState;
	private IBlockState ripeState;
	private IFruitFamily fruitFamily;

	public FarmableBush(IBlockState unripeState, IBlockState ripeState, IFruitFamily fruitFamily) {
		this.unripeState = unripeState;
		this.ripeState = ripeState;
		this.fruitFamily = fruitFamily;
	}

	@Nullable
	@Override
	public ICrop getCropAt(World world, BlockPos pos, IBlockState blockState) {
		return new CropFruit(world, pos);
	}

	@Override
	public boolean isGermling(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean isWindfall(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean plantSaplingAt(EntityPlayer player, ItemStack germling, World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean hasFruit() {
		return false;
	}

	@Override
	public IFruitFamily getFruitFamily() {
		return fruitFamily;
	}

	@Override
	public NonNullList<ItemStack> pickFruit(ItemStack tool) {
		return null;
	}

	@Override
	public float getRipeness() {    //bushes are either ripe or not
		return hasFruit() ? 1 : 0;
	}

	@Override
	public void addRipeness(float add) {

	}
}
