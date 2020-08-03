package forestry.arboriculture.blocks;

import com.google.common.base.Preconditions;
import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.features.CharcoalBlocks;
import forestry.core.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import java.util.Collection;
import java.util.Random;

public class BlockWoodPile extends Block {

    public static final BooleanProperty IS_ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;
    public static final int RANDOM_TICK = 160;
    public static final int TICK_RATE = 960;

    public BlockWoodPile() {
        super(Block.Properties.create(Material.WOOD)
                .hardnessAndResistance(1.5f)
                .sound(SoundType.WOOD)
                .harvestLevel(0)
                .harvestTool(ToolType.AXE));
        setDefaultState(getStateContainer().getBaseState().with(AGE, 0).with(IS_ACTIVE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(IS_ACTIVE, AGE);
    }

    //TODO voxelShape
    //	@Override
    //	public boolean isOpaqueCube(BlockState state) {
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean isNormalCube(BlockState state) {
    //		return false;
    //	}
    //
    //	@Override
    //	public boolean isFullBlock(BlockState state) {
    //		return false;
    //	}

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
        if (!state.get(IS_ACTIVE)) {
            for (Direction facing : Direction.VALUES) {
                BlockState facingState = world.getBlockState(pos.offset(facing));
                if (facingState.getBlock() == this && facingState.get(IS_ACTIVE)) {
                    world.setBlockState(pos, state.with(IS_ACTIVE, true));
                    break;
                }
            }
        }

        world.getPendingBlockTicks().scheduleTick(pos, this, TICK_RATE + world.rand.nextInt(RANDOM_TICK));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        boolean isActive = state.get(IS_ACTIVE);
        if (world.getBlockState(fromPos).getBlock() == Blocks.FIRE) {
            if (!isActive) {
                activatePile(state, world, pos, true);
            }
        }
    }

    private void activatePile(BlockState state, World world, BlockPos pos, boolean scheduleUpdate) {
        world.setBlockState(pos, state.with(IS_ACTIVE, true), 2);
        if (scheduleUpdate) {
            world.getPendingBlockTicks().scheduleTick(pos, this, (TICK_RATE + world.rand.nextInt(RANDOM_TICK)) / 4);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
        if (state.get(IS_ACTIVE)) {
            for (Direction facing : Direction.VALUES) {
                BlockPos position = pos.offset(facing);
                if (!world.isBlockLoaded(position)) {
                    continue;
                }
                BlockState blockState = world.getBlockState(position);
                Block block = blockState.getBlock();
                if (block == this) {
                    if (!state.get(IS_ACTIVE) && blockState.get(IS_ACTIVE)) {
                        activatePile(state, world, pos, false);
                    } else if (!blockState.get(IS_ACTIVE) && state.get(IS_ACTIVE)) {
                        activatePile(blockState, world, position, true);
                    }
                } else if (world.isAirBlock(position) || !Block.hasEnoughSolidSide(world, position, facing.getOpposite()) || block.isFlammable(state, world, position, facing.getOpposite())) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    return;
                }
            }
            if (rand.nextFloat() < 0.5F) {
                if (state.get(AGE) < 7) {
                    world.setBlockState(pos, state.with(AGE, state.get(AGE) + 1), 2);
                } else {
                    BlockState ashState = CharcoalBlocks.ASH.with(BlockAsh.AMOUNT, Math.min(Math.round(Config.charcoalAmountBase + getCharcoalAmount(world, pos)), 63));
                    world.setBlockState(pos, ashState, 2);
                }
            }
            world.getPendingBlockTicks().scheduleTick(pos, this, TICK_RATE + world.rand.nextInt(RANDOM_TICK));
        }
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 12;
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 25;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if (state.get(IS_ACTIVE)) {
            return 10;
        }
        return super.getLightValue(state, world, pos);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (state.get(IS_ACTIVE)) {
            if (rand.nextDouble() < 0.1D) {
                world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
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

    private float getCharcoalAmount(World world, BlockPos pos) {
        float charcoalAmount = 0F;
        for (Direction facing : Direction.VALUES) {
            charcoalAmount += getCharcoalFaceAmount(world, pos, facing);
        }
        return MathHelper.clamp(charcoalAmount / 6, Config.charcoalAmountBase, 63.0F - Config.charcoalAmountBase);
    }

    private int getCharcoalFaceAmount(World world, BlockPos pos, Direction facing) {
        ICharcoalManager charcoalManager = Preconditions.checkNotNull(TreeManager.charcoalManager);
        Collection<ICharcoalPileWall> walls = charcoalManager.getWalls();

        BlockPos.Mutable testPos = new BlockPos.Mutable();
        testPos.setPos(pos).move(facing);
        int i = 0;
        while (i < Config.charcoalWallCheckRange && world.isBlockLoaded(testPos) && !world.isAirBlock(testPos)) {
            BlockState state = world.getBlockState(testPos);
            for (ICharcoalPileWall wall : walls) {
                if (wall.matches(state)) {
                    return wall.getCharcoalAmount();
                }
            }
            testPos.move(facing);
            i++;
        }
        return 0;
    }

}
