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
package forestry.arboriculture.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import forestry.arboriculture.items.ItemWoodBlock;
import forestry.arboriculture.items.ItemWoodBlock.WoodMeshDefinition;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.core.utils.Utils;

public abstract class BlockWood extends Block implements ITileEntityProvider, IWoodTyped, IModelRegister {
	
	private final String blockKind;
	private final boolean fireproof;

	protected BlockWood(String blockKind, boolean fireproof) {
		super(Material.wood);
		this.blockKind = blockKind;
		this.fireproof = fireproof;

		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
		setDefaultState(this.blockState.getBaseState().withProperty(WoodType.WOODTYPE, WoodType.LARCH));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{WoodType.WOODTYPE});
	}
	
    @Override
	public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

	@Override
	public final String getBlockKind() {
		return blockKind;
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	protected static NBTTagCompound getTagCompound(IBlockAccess world, BlockPos pos) {
		WoodType type = getWoodType(world, pos);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		if (type == null) {
			return nbttagcompound;
		}
		type.saveToCompound(nbttagcompound);
		return nbttagcompound;
	}
	
	public static TileWood getWoodTile(IBlockAccess world, BlockPos pos) {
		return Utils.getTile(world, pos, TileWood.class);
	}
	
	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}
	
	protected static WoodType getWoodType(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		TileWood wood = (TileWood) world.getTileEntity(pos);
		WoodType type = wood.getWoodType();
		return type;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		if(!fireproof)
		{
			manager.registerVariant(item, ItemWoodBlock.getVariants(this));
		}
		manager.registerItemModel(item, new WoodMeshDefinition(this));
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack itemStack = new ItemStack(this);
		NBTTagCompound nbt = getTagCompound(world, pos);
		itemStack.setTagCompound(nbt);
		return itemStack;
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		return blockRemovedByPlayer(this, world, player, pos);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}
	
	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		WoodType type = getWoodType(world, pos);
		if (type == null) {
			return WoodType.DEFAULT_HARDNESS;
		}
		return type.getHardness();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileWood)
		{
			TileWood wood = (TileWood) tile;
			state = state.withProperty(WoodType.WOODTYPE, wood.getWoodType());
		}
		return super.getActualState(state, world, pos);
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return isFireproof() ? 0 : 5;
	}
	
	public static <T extends Block & IWoodTyped> boolean blockRemovedByPlayer(T block, World world, EntityPlayer player, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (Proxies.common.isSimulating(world) && block.canHarvestBlock(world, pos, player) && !player.capabilities.isCreativeMode) {

			Object obj = state.getValue(WoodType.WOODTYPE);
			if (obj != null && obj instanceof WoodType) {
				WoodType type = (WoodType) state.getValue(WoodType.WOODTYPE);

				ItemStack stack = new ItemStack(block);
				NBTTagCompound compound = new NBTTagCompound();
				type.saveToCompound(compound);
				stack.setTagCompound(compound);
				StackUtils.dropItemStackAsEntity(stack, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return world.setBlockToAir(pos);
	}

}
