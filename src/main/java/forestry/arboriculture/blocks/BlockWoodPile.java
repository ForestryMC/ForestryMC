package forestry.arboriculture.blocks;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Config;
import org.jetbrains.annotations.Nullable;

// TODO: Fix propagation, aging
public class BlockWoodPile extends Block {

	public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("active");
	public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
	public static final int RANDOM_TICK = 160;
	public static final int TICK_RATE = 960;

	public BlockWoodPile() {
		super(Block.Properties.of(Material.WOOD)
				.strength(1.5f)
				.sound(SoundType.WOOD)
				.noOcclusion());
		registerDefaultState(getStateDefinition().any()
				.setValue(AGE, 0)
				.setValue(IS_ACTIVE, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(IS_ACTIVE, AGE);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		for (Direction facing : Direction.VALUES) {
			BlockState facingState = context.getLevel().getBlockState(context.getClickedPos().relative(facing));

			if (facingState.is(this) && facingState.getValue(IS_ACTIVE)) {
				return defaultBlockState().setValue(IS_ACTIVE, true);
			}
		}

		return defaultBlockState();
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
		BlockState neighborState = world.getBlockState(fromPos);

		if (neighborState.getBlock() == Blocks.FIRE || (neighborState.is(this) && neighborState.getValue(IS_ACTIVE))) {
			if (!state.getValue(IS_ACTIVE)) {
				activatePile(state, world, pos, true);
			}
		}
	}

	private void activatePile(BlockState state, Level world, BlockPos pos, boolean scheduleUpdate) {
		world.setBlock(pos, state.setValue(IS_ACTIVE, true), Block.UPDATE_CLIENTS);

		if (scheduleUpdate) {
			// world.getBlockTicks().scheduleTick(pos, this, (TICK_RATE + world.random.nextInt(RANDOM_TICK)) / 4);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (state.getValue(IS_ACTIVE)) {
			for (Direction facing : Direction.VALUES) {
				BlockPos position = pos.relative(facing);
				if (!world.hasChunkAt(position)) {
					continue;
				}
				BlockState blockState = world.getBlockState(position);
				Block block = blockState.getBlock();
				if (block == this) {
					if (!state.getValue(IS_ACTIVE) && blockState.getValue(IS_ACTIVE)) {
						activatePile(state, world, pos, false);
					} else if (!blockState.getValue(IS_ACTIVE) && state.getValue(IS_ACTIVE)) {
						activatePile(blockState, world, position, true);
					}
				} else if (world.isEmptyBlock(position)
						|| !Block.canSupportCenter(world, position, facing.getOpposite())
						|| block.isFlammable(blockState, world, position, facing.getOpposite())) {
					world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
					return;
				}
			}
			if (rand.nextFloat() < 0.5F) {
				if (state.getValue(AGE) < 7) {
					world.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_CLIENTS);
				} else {
					BlockState ashState = CharcoalBlocks.ASH.with(BlockAsh.AMOUNT, Math.min(Math.round(Config.charcoalAmountBase + getCharcoalAmount(world, pos)), 63));
					world.setBlock(pos, ashState, Block.UPDATE_CLIENTS);
				}
			}
			// world.getBlockTicks().scheduleTick(pos, this, TICK_RATE + world.random.nextInt(RANDOM_TICK));
		}
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 12;
	}

	@Override
	public boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 25;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		if (state.getValue(IS_ACTIVE)) {
			return 10;
		}
		return super.getLightEmission(state, world, pos);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		if (rand.nextInt() < 0.25F) {
			return;
		}
		if (state.getValue(IS_ACTIVE)) {
			if (rand.nextDouble() < 0.05D) {
				world.playLocalSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
			}
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;
			if (rand.nextDouble() < 0.2D) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.15D, 0.0D);
			} else {
				world.addParticle(ParticleTypes.SMOKE, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.15D, 0.0D);
			}
		}
	}

	//TODO: Precalculate, like leaf distance
	private float getCharcoalAmount(Level world, BlockPos pos) {
		float charcoalAmount = 0F;
		for (Direction facing : Direction.VALUES) {
			charcoalAmount += getCharcoalFaceAmount(world, pos, facing);
		}
		return Mth.clamp(charcoalAmount / 6, 0, 63.0F - Config.charcoalAmountBase);
	}

	private int getCharcoalFaceAmount(Level world, BlockPos pos, Direction facing) {
		ICharcoalManager charcoalManager = Preconditions.checkNotNull(TreeManager.charcoalManager);
		Collection<ICharcoalPileWall> walls = charcoalManager.getWalls();

		BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos();
		testPos.set(pos).move(facing);
		int i = 0;
		while (i < Config.charcoalWallCheckRange && world.hasChunkAt(testPos) && !world.isEmptyBlock(testPos)) {
			BlockState state = world.getBlockState(testPos);
			ICharcoalPileWall wall = charcoalManager.getWall(state);
			if (wall != null) {
				return wall.getCharcoalAmount();
			}
			testPos.move(facing);
			i++;
		}
		return 0;
	}

}
