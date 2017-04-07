package forestry.core.blocks;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.config.Config;
import forestry.core.config.Constants;

public class BlockHumus extends Block implements IItemModelRegister {
	private static final int degradeDelimiter = Config.humusDegradeDelimiter;
	public static final PropertyInteger DEGRADE = PropertyInteger.create("degrade", 0, degradeDelimiter); // degradation level of humus

	public BlockHumus() {
		super(Material.GROUND);
		setTickRandomly(true);
		setHardness(0.5f);
		setSoundType(SoundType.GROUND);
		setCreativeTab(CreativeTabForestry.tabForestry);

		setDefaultState(this.blockState.getBaseState().withProperty(DEGRADE, 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(DEGRADE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DEGRADE, meta);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DEGRADE);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(DEGRADE);
	}

	@Override
	public int tickRate(World world) {
		return 500;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.singletonList(new ItemStack(Blocks.DIRT));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
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
				IBlockState state = world.getBlockState(blockPos);
				Block block = state.getBlock();
				if (block instanceof BlockLog || block instanceof IGrowable) {
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
		IBlockState blockState = world.getBlockState(pos);

		int degrade = blockState.getValue(DEGRADE);
		degrade++;

		if (degrade >= degradeDelimiter) {
			world.setBlockState(pos, Blocks.SAND.getDefaultState(), Constants.FLAG_BLOCK_SYNC);
		} else {
			world.setBlockState(pos, blockState.withProperty(DEGRADE, degrade), Constants.FLAG_BLOCK_SYNC);
		}
		world.markBlockRangeForRenderUpdate(pos, pos);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains) {
			return false;
		}

		return true;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> itemList) {
		itemList.add(new ItemStack(this));
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "soil/humus");
		manager.registerItemModel(item, 1, "soil/humus");
		manager.registerItemModel(item, 2, "soil/humus");
		manager.registerItemModel(item, 3, "soil/humus");
	}
}
