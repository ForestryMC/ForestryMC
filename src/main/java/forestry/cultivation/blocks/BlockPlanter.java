package forestry.cultivation.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.blocks.BlockBase;
import forestry.core.render.ParticleRender;
import forestry.cultivation.tiles.TilePlanter;

public class BlockPlanter extends BlockBase<BlockTypePlanter> {
	public static final PropertyBool MANUAL = PropertyBool.create("manual");

	public BlockPlanter(BlockTypePlanter blockType) {
		super(blockType, Material.WOOD);
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(MANUAL, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, MANUAL);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (blockType == BlockTypePlanter.FARM_ENDER) {
			for (int i = 0; i < 3; ++i) {
				ParticleRender.addPortalFx(worldIn, pos, rand);
			}
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = meta & 7;
		return getDefaultState().withProperty(FACING, EnumFacing.fromAngle(facing)).withProperty(MANUAL, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if (state.getValue(MANUAL)) {
			return 8 + state.getValue(FACING).ordinal();
		}
		return state.getValue(FACING).ordinal();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		TileEntity tile = super.createNewTileEntity(world, 0);
		if (tile instanceof TilePlanter) {
			TilePlanter planter = (TilePlanter) tile;
			planter.setManual(state.getValue(MANUAL));
		}
		return tile;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(MANUAL) ? 1 : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new PlanterStateMapper());
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		ItemStack stack = placer.getHeldItem(hand);
		return getDefaultState().withProperty(MANUAL, isManual(stack));
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (byte i = 0; i < 2; i++) {
			items.add(get(i == 1));
		}
	}

	public static boolean isManual(ItemStack stack) {
		return stack.getMetadata() == 1;
	}

	public ItemStack get(boolean isManual) {
		return new ItemStack(this, 1, isManual ? 1 : 0);
	}
}
