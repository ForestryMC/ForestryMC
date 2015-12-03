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
package forestry.farming.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
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

import forestry.core.blocks.BlockStructure;
import forestry.core.render.ParticleHelper;
import forestry.core.utils.ItemStackUtil;
import forestry.farming.render.EnumFarmBlockTexture;
import forestry.farming.tiles.TileFarm;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;
import forestry.plugins.PluginFarming;

public class BlockFarm extends BlockStructure {

	private final ParticleHelper.Callback particleCallback;

	public BlockFarm() {
		super(Material.rock);
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 0);
		this.particleCallback = new ParticleHelper.DefaultCallback(this);
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}

			for (EnumFarmBlockTexture block : EnumFarmBlockTexture.values()) {
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
		if (drops.isEmpty()) {
			return super.getPickBlock(target, world, x, y, z);
		}
		return drops.get(0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, entityLiving, stack);
		if (!stack.hasTagCompound()) {
			return;
		}

		TileFarm tile = (TileFarm) world.getTileEntity(x, y, z);
		tile.setFarmBlockTexture(EnumFarmBlockTexture.getFromCompound(stack.getTagCompound()));
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (!world.isRemote && canHarvestBlock(player, meta)) {
			List<ItemStack> drops = getDrops(world, x, y, z, 0, 0);
			for (ItemStack drop : drops) {
				ItemStackUtil.dropItemStackAsEntity(drop, world, x, y, z);
			}
		}
		return world.setBlockToAir(x, y, z);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileFarm) {
			TileFarm farm = (TileFarm) tile;

			ItemStack stack = new ItemStack(this, 1, meta != 1 ? meta : 0);
			NBTTagCompound compound = new NBTTagCompound();
			farm.getFarmBlockTexture().saveToCompound(compound);
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
				return new TileFarmGearbox();
			case 3:
				return new TileFarmHatch();
			case 4:
				return new TileFarmValve();
			case 5:
				return new TileFarmControl();
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
		EnumFarmBlockTexture.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return getBlockTextureForSide(EnumFarmBlockTexture.BRICK, side);
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getBlockTextureForSide(EnumFarmBlockTexture type, int side) {
		Block block = ItemStackUtil.getBlock(type.getBase());
		if (block == null) {
			return null;
		}
		int damage = type.getBase().getItemDamage();
		return block.getIcon(side, damage);
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getOverlayTextureForBlock(int side, int metadata) {
		BlockFarmType type = BlockFarmType.VALUES[metadata];
		return EnumFarmBlockTexture.getIcon(type, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
		ItemStack base = EnumFarmBlockTexture.BRICK_STONE.getBase();

		if (tile instanceof TileFarm) {
			base = ((TileFarm) tile).getFarmBlockTexture().getBase();
		}

		Block block = ItemStackUtil.getBlock(base);
		if (block == null) {
			return null;
		}

		return block.getIcon(side, base.getItemDamage());
	}

	/* Particles */
	@SideOnly(Side.CLIENT)
	@Override
	public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
		return ParticleHelper.addHitEffects(worldObj, this, target, effectRenderer, particleCallback);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean addDestroyEffects(World worldObj, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		return ParticleHelper.addDestroyEffects(worldObj, this, x, y, z, meta, effectRenderer, particleCallback);
	}

	@Override
	public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return world.getBlockMetadata(x, y, z) == 5;
	}

	public ItemStack get(BlockFarmType type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
