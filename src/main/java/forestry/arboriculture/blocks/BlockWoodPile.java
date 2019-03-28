package forestry.arboriculture.blocks;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.ICharcoalManager;
import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.arboriculture.ModuleCharcoal;
import forestry.core.config.Config;

public class BlockWoodPile extends Block implements IItemModelRegister, IStateMapperRegister {

	public static final PropertyBool IS_ACTIVE = PropertyBool.create("active");
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
	public static final int RANDOM_TICK = 160;

	public BlockWoodPile() {
		super(Material.WOOD);
		setHardness(1.5f);
		setCreativeTab(ModuleCharcoal.getTag());
		setSoundType(SoundType.WOOD);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, IS_ACTIVE, AGE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(IS_ACTIVE) ? 8 + state.getValue(AGE) : state.getValue(AGE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean isActive = meta > 7;
		return getDefaultState().withProperty(IS_ACTIVE, isActive).withProperty(AGE, meta - (isActive ? 8 : 0));
	}

	@Override
	public int tickRate(World world) {
		return 960;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (!state.getValue(IS_ACTIVE)) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				IBlockState facingState = world.getBlockState(pos.offset(facing));
				if (facingState.getBlock() == this && facingState.getValue(IS_ACTIVE)) {
					world.setBlockState(pos, state.withProperty(IS_ACTIVE, true));
					break;
				}
			}
		}
		world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos) {
		boolean isActive = state.getValue(IS_ACTIVE);
		if (fromBlock == Blocks.FIRE) {
			if (!isActive) {
				activatePile(state, world, pos, true);
			}
		}
	}

	private void activatePile(IBlockState state, World world, BlockPos pos, boolean scheduleUpdate) {
		world.setBlockState(pos, state.withProperty(IS_ACTIVE, true), 2);
		if (scheduleUpdate) {
			world.scheduleUpdate(pos, this, (this.tickRate(world) + world.rand.nextInt(RANDOM_TICK)) / 4);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if (state.getValue(IS_ACTIVE)) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos position = pos.offset(facing);
				if (!world.isBlockLoaded(position)) {
					continue;
				}
				IBlockState blockState = world.getBlockState(position);
				Block block = blockState.getBlock();
				if (block == this) {
					if (!state.getValue(IS_ACTIVE) && blockState.getValue(IS_ACTIVE)) {
						activatePile(state, world, pos, false);
					} else if (!blockState.getValue(IS_ACTIVE) && state.getValue(IS_ACTIVE)) {
						activatePile(blockState, world, position, true);
					}
				} else if (world.isAirBlock(position) || !blockState.isSideSolid(world, position, facing.getOpposite()) || block.isFlammable(world, position, facing.getOpposite())) {
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
					return;
				}
			}
			if (rand.nextFloat() < 0.5F) {
				if (state.getValue(AGE) < 7) {
					world.setBlockState(pos, state.withProperty(AGE, state.getValue(AGE) + 1), 2);
				} else {
					IBlockState ashState = ModuleCharcoal.getBlocks().getAshState(Math.round(Config.charcoalAmountBase + getCharcoalAmount(world, pos)));
					world.setBlockState(pos, ashState, 2);
				}
			}
			world.scheduleUpdate(pos, this, this.tickRate(world) + world.rand.nextInt(RANDOM_TICK));
		}
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 12;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 25;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(IS_ACTIVE)) {
			return 10;
		}
		return super.getLightValue(state, world, pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (state.getValue(IS_ACTIVE)) {
			if (rand.nextDouble() < 0.1D) {
				world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
			}
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 0.0F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			float f3 = 0.52F;
			float f4 = rand.nextFloat() * 0.6F - 0.3F;
			if (rand.nextDouble() < 0.2D) {
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.15D, 0.0D);
			} else {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, f + f3 - 0.5, f1 + 1, f2 + f4, 0.0D, 0.15D, 0.0D);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	private float getCharcoalAmount(World world, BlockPos pos) {
		float charcoalAmount = 0F;
		for (EnumFacing facing : EnumFacing.VALUES) {
			charcoalAmount += getCharcoalFaceAmount(world, pos, facing);
		}
		return MathHelper.clamp(charcoalAmount / 6, Config.charcoalAmountBase, 63.0F - Config.charcoalAmountBase);
	}

	private int getCharcoalFaceAmount(World world, BlockPos pos, EnumFacing facing) {
		ICharcoalManager charcoalManager = Preconditions.checkNotNull(TreeManager.charcoalManager);
		Collection<ICharcoalPileWall> walls = charcoalManager.getWalls();

		BlockPos.MutableBlockPos testPos = new BlockPos.MutableBlockPos(pos);
		testPos.move(facing);
		int i = 0;
		while (i < Config.charcoalWallCheckRange && world.isBlockLoaded(testPos) && !world.isAirBlock(testPos)) {
			IBlockState state = world.getBlockState(testPos);
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

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(AGE, IS_ACTIVE).build());
	}

}
