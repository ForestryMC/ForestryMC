/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.gadgets.BlockStructure;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.farming.gadgets.TileFarm.EnumFarmBlock;
import forestry.plugins.PluginFarming;

public class BlockFarm extends BlockStructure {

	public BlockFarm() {
		super(Material.rock);
		setHardness(1.0f);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 6; i++) {
			if (i == 1)
				continue;

			for (EnumFarmBlock block : EnumFarmBlock.values()) {
				ItemStack stack = new ItemStack(item, 1, i);
				NBTTagCompound compound = new NBTTagCompound();
				block.saveToCompound(compound);
				stack.setTagCompound(compound);
				list.add(stack);
			}
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ArrayList<ItemStack> drops = getDrops(world, x, y, z, 0, 0);
		if (drops.isEmpty())
			return super.getPickBlock(target, world, x, y, z);
		return drops.get(0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityliving, stack);
		if (!stack.hasTagCompound())
			return;

		TileFarm tile = (TileFarm) world.getTileEntity(x, y, z);
		tile.setFarmBlock(EnumFarmBlock.getFromCompound(stack.getTagCompound()));
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, meta)) {
			List<ItemStack> drops = getDrops(world, x, y, z, 0, 0);
			for (ItemStack drop : drops) {
				StackUtils.dropItemStackAsEntity(drop, world, x, y, z);
			}
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileFarm) {
			TileFarm farm = (TileFarm) tile;

			ItemStack stack = new ItemStack(this, 1, meta != 1 ? meta : 0);
			NBTTagCompound compound = new NBTTagCompound();
			farm.getFarmBlock().saveToCompound(compound);
			stack.setTagCompound(compound);
			drops.add(stack);
		}
		return drops;
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return meta != 1 ? meta : 0;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch (metadata) {
		case 2:
			return new TileGearbox();
		case 3:
			return new TileHatch();
		case 4:
			return new TileValve();
		case 5:
			return new TileControl();
		default:
			return new TileFarmPlain();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return createTileEntity(world, meta);
	}

	/* ICONS */
	@Override
	public int getRenderType() {
		return PluginFarming.modelIdFarmBlock;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		EnumFarmBlock.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return getBlockTextureFromSideAndMetadata(EnumFarmBlock.BRICK, side, metadata);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getBlockTextureFromSideAndMetadata(EnumFarmBlock type, int side, int metadata) {
		return StackUtils.getBlock(type.base).getIcon(side, type.base.getItemDamage());
	}

	@SideOnly(Side.CLIENT)
	public IIcon getOverlayTextureForBlock(int side, int metadata) {

		EnumFarmBlock type = EnumFarmBlock.BRICK_STONE;
		if (metadata == 0 && side == 2)
			return type.getIcon(TileFarm.TYPE_REVERSE);
		else if (metadata == 0 && (side == 0 || side == 1))
			return type.getIcon(TileFarm.TYPE_TOP);

		switch (metadata) {
		case 1:
			return type.getIcon(TileFarm.TYPE_BAND);
		case 2:
			return type.getIcon(TileFarm.TYPE_GEARS);
		case 3:
			return type.getIcon(TileFarm.TYPE_HATCH);
		case 4:
			return type.getIcon(TileFarm.TYPE_VALVE);
		case 5:
			return type.getIcon(TileFarm.TYPE_CONTROL);
		default:
			return type.getIcon(TileFarm.TYPE_PLAIN);
		}
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		if (world.getBlockMetadata(x, y, z) == 5)
			return true;
		else
			return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		ItemStack base = EnumFarmBlock.BRICK_STONE.base;

		if (tile instanceof TileFarm)
			base = ((TileFarm) tile).farmBlock.base;

		return StackUtils.getBlock(base).getIcon(side, base.getItemDamage());
	}
}
