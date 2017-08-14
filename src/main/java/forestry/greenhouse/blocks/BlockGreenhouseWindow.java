/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.greenhouse.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.greenhouse.api.greenhouse.GreenhouseManager;
import forestry.core.blocks.IBlockRotatable;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.tiles.IActivatable;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.Log;
import forestry.greenhouse.PluginGreenhouse;
import forestry.greenhouse.tiles.TileGreenhouseWindow;
import forestry.greenhouse.tiles.TileGreenhouseWindow.WindowMode;

public class BlockGreenhouseWindow extends Block implements IBlockRotatable, ITileEntityProvider, IItemModelRegister {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

	private boolean roofWindow;

	public BlockGreenhouseWindow(boolean roofWindow) {
		super(Material.ROCK);
		IBlockState defaultState = this.blockState.getBaseState();
		setDefaultState(defaultState.withProperty(FACING, EnumFacing.NORTH));

		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(PluginGreenhouse.getGreenhouseTab());
		setSoundType(SoundType.GLASS);
		this.roofWindow = roofWindow;
	}

	public ItemStack getItem(String glass) {
		ItemStack stack = new ItemStack(this);
		NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setString("Glass", glass);
		stack.setTagCompound(tagCompound);
		return stack;
	}

	protected int getPlaySound(boolean open) {
		if (open) {
			return 1007;
		} else {
			return 1013;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta + 2]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).ordinal() - 2;
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity tile = TileUtil.getTile(worldIn, pos, TileEntity.class);
		if (tile instanceof IActivatable) {
			state = state.withProperty(State.PROPERTY, ((IActivatable) tile).isActive() ? State.ON : State.OFF);
		}
		return super.getActualState(state, worldIn, pos);
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		EnumFacing facing = state.getValue(FACING);
		return state.withProperty(FACING, rot.rotate(facing));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		try {
			TileUtil.actOnTile(worldIn, pos, TileGreenhouseWindow.class, TileGreenhouseWindow::onNeighborBlockChange);
		} catch (StackOverflowError error) {
			Log.error("Stack Overflow Error in BlockMachine.onNeighborBlockChange()", error);
			throw error;
		}
	}

	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			return false;
		}

		TileGreenhouseWindow window = TileUtil.getTile(worldIn, pos, TileGreenhouseWindow.class);
		if (window == null) {
			return false;
		}

		if (playerIn.getHeldItemMainhand().isEmpty()) {
			if (window.getMode() != WindowMode.CONTROL) {
				if (!worldIn.isRemote) {
					if (window.isBlocked() == WindowMode.OPEN) {
						if (window.getMode() == WindowMode.OPEN) {
							window.setMode(WindowMode.PLAYER);
						} else {
							window.setMode(WindowMode.OPEN);
						}
					}
				}
				worldIn.playEvent(playerIn, getPlaySound(!window.isActive()), pos, 0);
				return true;
			}
		}

		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileUtil.actOnTile(world, pos, TileGreenhouseWindow.class, window -> {
			if (!window.getWorld().isRemote) {
				window.setMode(window.isBlocked());
			}
			NBTTagCompound itemTag = stack.getTagCompound();
			if (itemTag != null) {
				window.setGlass(itemTag.getString("Glass"));
			}
		});
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (String glass : GreenhouseManager.helper.getWindowGlasses()) {
			list.add(getItem(glass));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{State.PROPERTY, FACING}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		String glass = "glass";
		TileGreenhouseWindow window = TileUtil.getTile(world, pos, TileGreenhouseWindow.class);
		if (window != null) {
			glass = window.getGlass();
		}
		ret.add(getItem(glass));
		return ret;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		List<ItemStack> drops = getDrops(world, pos, world.getBlockState(pos), 0);
		if (drops.isEmpty()) {
			return super.getPickBlock(state, target, world, pos, player);
		}
		return drops.get(0);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
			.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	public void rotateAfterPlacement(EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(FACING, player.getHorizontalFacing().getOpposite()));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileGreenhouseWindow();
	}

	public boolean isRoofWindow() {
		return roofWindow;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if (roofWindow) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse_window_up", "inventory"));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:greenhouse_window", "inventory"));
		}
	}

}
