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
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import forestry.plugins.PluginArboriculture;

public class BlockArbFence extends BlockFence implements IWoodTyped, IModelRegister {

	public static final PropertyEnum WOODTYPE = PropertyEnum.create("woodtype", WoodType.class);
	private final boolean fireproof;

	public BlockArbFence(boolean fireproof) {
		super(Material.wood);

		this.fireproof = fireproof;

		setHardness(2.0F);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
		setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(WOODTYPE, WoodType.LARCH));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (WoodType woodType : WoodType.VALUES) {
			list.add(woodType.getFence(fireproof));
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, new WoodMeshDefinition("fence"));
	}
	
	@Override
	public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	@Override
	public boolean canConnectTo(IBlockAccess world, BlockPos pos) {
		if (!isFence(world, pos)) {
			Block block = world.getBlockState(pos).getBlock();
			if (block == this || block instanceof BlockFenceGate) {
				return true;
			}

			return block.getMaterial().isOpaque() && block.getMaterial() != Material.gourd;
		} else {
			return true;
		}
	}

	@Override
	public int getRenderType() {
		return PluginArboriculture.modelIdFences;
	}

	private static boolean isFence(IBlockAccess world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return PluginArboriculture.validFences.contains(block);
	}

	/* PROPERTIES */
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack itemStack = new ItemStack(this);
		NBTTagCompound nbt = BlockWood.getTagCompound(world, pos);
		itemStack.setTagCompound(nbt);
		return itemStack;
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		return BlockWood.blockRemovedByPlayer(this, world, player, pos);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public final float getBlockHardness(World world, BlockPos pos) {
		WoodType type = ItemWoodBlock.getWoodType(world, pos);
		if (type == null) {
			return WoodType.DEFAULT_HARDNESS;
		}
		return type.getHardness();
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return isFireproof() ? 0 : 5;
	}

	@Override
	public String getBlockKind() {
		return "fences";
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}
}
