package forestry.farming.logic;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
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
	private static final Set<Direction> VALID_DIRECTIONS = ImmutableSet.of(Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST);
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
		NonNullList<ItemStack> products = produce;
		produce = collectEntityItems(world, farmHousing, true);
		return products;
	}

	private final Map<BlockPos, Integer> lastExtentsHarvest = new HashMap<>();

	@Override
	public Collection<ICrop> harvest(World world, BlockPos pos, FarmDirection direction, int extent) {
		if (!lastExtentsHarvest.containsKey(pos)) {
			lastExtentsHarvest.put(pos, 0);
		}

		int lastExtent = lastExtentsHarvest.get(pos);
		if (lastExtent > extent) {
			lastExtent = 0;
		}

		BlockPos position = translateWithOffset(pos.up(), direction, lastExtent);
		Collection<ICrop> crops = harvestBlocks(world, position);
		lastExtent++;
		lastExtentsHarvest.put(pos, lastExtent);

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
		harvestBlock(world, position, Direction.DOWN, plants, crops);
		//Remove all flowers before remove all plants
		if (!crops.isEmpty()) {
			return crops;
		}
		return plants;
	}

	private boolean harvestBlock(World world, BlockPos pos, Direction from, Stack<ICrop> plants, Stack<ICrop> flowers) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == Blocks.CHORUS_FLOWER) {
			ICrop crop = chorusFarmable.getCropAt(world, pos, blockState);
			if (crop != null) {
				flowers.push(crop);
				return false;
			}
			return false;
		} else if (blockState.getBlock() == Blocks.CHORUS_PLANT) {
			boolean canHarvest = true;
			for (Direction facing : VALID_DIRECTIONS) {
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
			BlockState state = world.getBlockState(position);
			if (!world.isAirBlock(position) && !BlockUtil.isReplaceableBlock(state, world, position)) {
				continue;
			}

			BlockPos soilPos = position.down();
			BlockState blockState = world.getBlockState(soilPos);
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
