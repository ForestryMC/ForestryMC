package forestry.sorting.blocks;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;
import forestry.core.blocks.BlockForestry;
import forestry.core.gui.GuiHandler;
import forestry.core.tiles.TileUtil;
import forestry.sorting.tiles.TileGeneticFilter;

public class BlockGeneticFilter extends BlockForestry implements IItemModelRegister {
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");

	private static final AxisAlignedBB BOX_CENTER = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB BOX_DOWN = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.3125, 0.75);
	private static final AxisAlignedBB BOX_UP = new AxisAlignedBB(0.25, 0.6875, 0.25, 0.75, 1, 0.75);
	private static final AxisAlignedBB BOX_NORTH = new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 0.3125);
	private static final AxisAlignedBB BOX_SOUTH = new AxisAlignedBB(0.25, 0.25, 0.6875, 0.75, 0.75, 1);
	private static final AxisAlignedBB BOX_WEST = new AxisAlignedBB(0, 0.25, 0.25, 0.3125, 0.75, 0.75);
	private static final AxisAlignedBB BOX_EAST = new AxisAlignedBB(0.6875, 0.25, 0.25, 1, 0.75, 0.75);
	private static final AxisAlignedBB[] BOX_FACES = {BOX_DOWN, BOX_UP, BOX_NORTH, BOX_SOUTH, BOX_WEST, BOX_EAST};

	public BlockGeneticFilter() {
		super(Material.WOOD);
		setCreativeTab(CreativeTabForestry.tabForestry);
		this.setDefaultState(this.blockState.getBaseState()
			.withProperty(NORTH, false)
			.withProperty(EAST, false)
			.withProperty(SOUTH, false)
			.withProperty(WEST, false)
			.withProperty(UP, false)
			.withProperty(DOWN, false));
		setLightOpacity(0);
		setHardness(0.25f);
		setResistance(3.0f);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileGeneticFilter geneticFilter = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
		if (geneticFilter == null) {
			return getDefaultState();
		}
		return state.withProperty(NORTH, geneticFilter.isConnected(EnumFacing.NORTH))
			.withProperty(EAST, geneticFilter.isConnected(EnumFacing.EAST))
			.withProperty(SOUTH, geneticFilter.isConnected(EnumFacing.SOUTH))
			.withProperty(WEST, geneticFilter.isConnected(EnumFacing.WEST))
			.withProperty(UP, geneticFilter.isConnected(EnumFacing.UP))
			.withProperty(DOWN, geneticFilter.isConnected(EnumFacing.DOWN));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileGeneticFilter tile = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
		if (tile != null) {
			if (TileUtil.isUsableByPlayer(playerIn, tile)) {
				if (!worldIn.isRemote) {
					GuiHandler.openGui(playerIn, tile);
				}
				return true;
			}
		}
		return false;
	}

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
		RayTraceResult result = rayTrace(pos, start, end, BOX_CENTER);
		if (result != null) {
			result.subHit = 0;
			return result;
		}
		TileGeneticFilter geneticFilter = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
		if (geneticFilter != null) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (geneticFilter.isConnected(facing)) {
					result = rayTrace(pos, start, end, BOX_FACES[facing.ordinal()]);
					if (result != null) {
						result.subHit = 1 + facing.ordinal();
						return result;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_CENTER);
		TileGeneticFilter geneticFilter = TileUtil.getTile(worldIn, pos, TileGeneticFilter.class);
		if (geneticFilter != null) {
			for (EnumFacing facing : EnumFacing.VALUES) {
				if (geneticFilter.isConnected(facing)) {
					addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_FACES[facing.ordinal()]);
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		RayTraceResult trace = Minecraft.getMinecraft().objectMouseOver;
		if (trace == null || trace.subHit < 0 || !pos.equals(trace.getBlockPos())) {
			return FULL_BLOCK_AABB.offset(pos);
		}
		AxisAlignedBB aabb = FULL_BLOCK_AABB;
		int sub = trace.subHit;
		if (sub == 0) {
			aabb = BOX_CENTER;
		} else if (sub < 1 + 6) {
			aabb = BOX_FACES[sub - 1];
		}
		return aabb.offset(pos);
	}

	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileGeneticFilter();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
