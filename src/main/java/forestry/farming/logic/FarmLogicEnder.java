package forestry.farming.logic;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.api.farming.IFarmProperties;
import forestry.api.farming.IFarmable;
import forestry.core.utils.BlockUtil;
import forestry.farming.logic.crops.CropDestroy;
import forestry.farming.logic.farmables.FarmableChorus;

public class FarmLogicEnder extends FarmLogicHomogeneous {
	private static final Set<EnumFacing> VALID_DIRECTIONS = ImmutableSet.of(EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST);
	private final IFarmable chorusFarmable;

	public FarmLogicEnder(IFarmProperties properties, boolean isManual) {
		super(properties, isManual);
		chorusFarmable = FarmableChorus.INSTANCE;
	}

	@Override
	public String getUnlocalizedName() {
		return "for.farm.ender";
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.ENDER_EYE);
	}

	@Override
	public int getFertilizerConsumption() {
		return 20;
	}

	@Override
	public int getWaterConsumption(float hydrationModifier) {
		return 0;
	}

	@Override
	public NonNullList<ItemStack> collect(World world, IFarmHousing farmHousing) {
		return collectEntityItems(world, farmHousing, true);
	}

	@Override
	public Collection<ICrop> harvest(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		BlockPos position = farmHousing.getValidPosition(direction, pos, extent, pos.up());
		Collection<ICrop> crops = harvestBlocks(world, position);
		farmHousing.increaseExtent(direction, pos, extent);

		return crops;
	}

	private Collection<ICrop> harvestBlocks(World world, BlockPos position) {
		if (!world.isBlockLoaded(position) || world.isAirBlock(position)) {
			return Collections.emptySet();
		}

		ICrop crop = getCrop(world, position);
		if (crop != null) {
			return Collections.singleton(crop);
		}

		Stack<ICrop> crops = new Stack<>();
		Stack<ICrop> plants = new Stack<>();
		harvestBlock(world, position, EnumFacing.DOWN, plants, crops);
		//Remove all flowers before remove all plants
		if (!crops.isEmpty()) {
			return crops;
		}
		return plants;
	}

	private boolean harvestBlock(World world, BlockPos pos, EnumFacing from, Stack<ICrop> plants, Stack<ICrop> flowers) {
		IBlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.CHORUS_FLOWER) {
			ICrop crop = chorusFarmable.getCropAt(world, pos, blockState);
			if (crop != null) {
				flowers.push(crop);
				return false;
			}
			return false;
		} else if (blockState.getBlock() == Blocks.CHORUS_PLANT) {
			boolean canHarvest = true;
			for (EnumFacing facing : VALID_DIRECTIONS) {
				if (facing == from) {
					continue;
				}
				canHarvest &= harvestBlock(world, pos.offset(facing), facing.getOpposite(), plants, flowers);
			}
			if (canHarvest) {
				plants.push(new CropDestroy(world, Blocks.CHORUS_PLANT.getDefaultState(), pos, null));
			}
			return canHarvest;
		}
		return true;
	}

	@Override
	protected boolean maintainGermlings(World world, IFarmHousing farmHousing, BlockPos pos, FarmDirection direction, int extent) {
		for (int i = 0; i < extent; i++) {
			BlockPos position = translateWithOffset(pos, direction, i);
			IBlockState state = world.getBlockState(position);
			if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			BlockPos soilPos = position.down();
			IBlockState blockState = world.getBlockState(soilPos);
			if (!isAcceptedSoil(blockState)) {
				continue;
			}

			if (trySetCrop(world, farmHousing, position, direction)) {
				return true;
			}
		}

		return false;
	}
}
