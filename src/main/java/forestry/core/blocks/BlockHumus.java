package forestry.core.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.config.Config;
import forestry.core.config.Constants;

public class BlockHumus extends Block implements IItemModelRegister {
	private static final int degradeDelimiter = Config.humusDegradeDelimiter;
	public static final IntegerProperty DEGRADE = IntegerProperty.create("degrade", 0, degradeDelimiter); // degradation level of humus

	public BlockHumus() {
		super(Block.Properties.create(Material.EARTH)
			.tickRandomly()
			.hardnessAndResistance(0.5f)
			.sound(SoundType.GROUND));
		//		setCreativeTab(CreativeTabForestry.tabForestry);

		setDefaultState(this.getStateContainer().getBaseState().with(DEGRADE, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(DEGRADE);
	}

	@Override
	public int tickRate(IWorldReader world) {
		return 500;
	}

	//TODO - loot tables
	//	@Override
	//	public void getDrops(NonNullList<ItemStack> drops, IBlockReader world, BlockPos pos, BlockState state, int fortune) {
	//		drops.add(new ItemStack(Blocks.DIRT));
	//	}

	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.isRemote || world.rand.nextInt(140) != 0) {
			return;
		}

		if (isEnrooted(world, pos)) {
			degradeSoil(world, pos);
		}
	}

	private static boolean isEnrooted(World world, BlockPos pos) {
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i == 0 && j == 0) {
					continue; // We are not returning true if we are the base of a sapling.
				}
				BlockPos blockPos = pos.add(i, 1, j);
				BlockState state = world.getBlockState(blockPos);
				Block block = state.getBlock();
				if (block instanceof LogBlock || block instanceof IGrowable) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * If a tree or sapling is in the vicinity, there is a chance, that the soil will degrade.
	 */
	private static void degradeSoil(World world, final BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);

		int degrade = blockState.get(DEGRADE);
		degrade++;

		if (degrade >= degradeDelimiter) {
			world.setBlockState(pos, Blocks.SAND.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlockState(pos, blockState.with(DEGRADE, degrade), Constants.FLAG_BLOCK_SYNC);
		}
		//TODO: Is this still needed ? Should now be marked with setBlockState
		Minecraft.getInstance().worldRenderer.markForRerender(pos.getX(), pos.getY(), pos.getZ());

		//		world.markForRerender(pos);
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction direction, IPlantable plantable) {
		PlantType plantType = plantable.getPlantType(world, pos);
		return plantType == PlantType.Crop || plantType == PlantType.Plains;
	}

	//TODO - loot tables?
	//	@Override
	//	public boolean canSilkHarvest(World world, BlockPos pos, BlockState state, PlayerEntity player) {
	//		return false;
	//	}

	/* MODELS */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < degradeDelimiter; i++) {
			manager.registerItemModel(item, i, "soil/humus");
		}
	}
}
