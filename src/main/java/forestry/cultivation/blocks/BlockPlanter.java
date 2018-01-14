package forestry.cultivation.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
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

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = meta & 7;
		return getDefaultState().withProperty(FACING, EnumFacing.fromAngle(facing)).withProperty(MANUAL, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		if(state.getValue(MANUAL)){
			return state.getValue(FACING).ordinal();
		}
		return 8 + state.getValue(FACING).ordinal();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		TileEntity tile = super.createNewTileEntity(world, 0);
		if(tile instanceof TilePlanter){
			TilePlanter planter = (TilePlanter) tile;
			planter.setManual(state.getValue(MANUAL));
		}
		return tile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new PlanterStateMapper());
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		ItemStack stack = placer.getHeldItem(hand);
		return getDefaultState().withProperty(MANUAL, isManual(stack));
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for(byte i = 0;i < 2;i++){
			items.add(get(i == 1));
		}
	}

	public static boolean isManual(ItemStack stack){
		NBTTagCompound tagCompound = stack.getTagCompound();
		if(tagCompound != null){
			return tagCompound.getBoolean("Manual");
		}
		return false;
	}

	public ItemStack get(boolean isManual){
		ItemStack stack = new ItemStack(this);
		stack.setTagInfo("Manual", new NBTTagByte((byte) (isManual ? 1 : 0)));
		return stack;
	}
}
