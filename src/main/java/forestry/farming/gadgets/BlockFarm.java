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
package forestry.farming.gadgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
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
import forestry.api.core.IVariantObject;
import forestry.core.gadgets.BlockStructure;
import forestry.core.proxy.Proxies;
import forestry.core.render.ModelManager;
import forestry.core.render.ParticleHelper;
import forestry.core.render.ParticleHelperCallback;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginFarming;

public class BlockFarm extends BlockStructure {

	private static BlockFarm instance;

	public BlockFarm() {
		super(Material.rock);
		setHardness(1.0f);
		instance = this;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 6; i++) {
			if (i == 1) {
				continue;
			}

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
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		List<ItemStack> drops = getDrops(world, pos, state, 0);
		if (drops.isEmpty()) {
			return super.getPickBlock(target, world, pos);
		}
		return drops.get(0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityliving, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, entityliving, stack);
		if (!stack.hasTagCompound()) {
			return;
		}

		TileFarm tile = (TileFarm) world.getTileEntity(pos);
		tile.setFarmBlock(EnumFarmBlock.getFromCompound(stack.getTagCompound()));
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		IBlockState state = world.getBlockState(pos);
		int meta = getMetaFromState(state);
		if (Proxies.common.isSimulating(world) && canHarvestBlock(world, pos, player)) {
			List<ItemStack> drops = getDrops(world, pos, state, 0);
			for (ItemStack drop : drops) {
				StackUtils.dropItemStackAsEntity(drop, world, pos);
			}
		}
		return world.setBlockToAir(pos);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		int meta = getMetaFromState(state);
		TileEntity tile = world.getTileEntity(pos);
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
	public int getDamageValue(World world, BlockPos pos) {
		int meta = getMetaFromState(world.getBlockState(pos));
		return meta != 1 ? meta : 0;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (getMetaFromState(state)) {
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
		return createTileEntity(world, getStateFromMeta(meta));
	}

	/* ICONS */
	@Override
	public int getRenderType() {
		return PluginFarming.modelIdFarmBlock;
	}

	private static class ParticleCallback implements ParticleHelperCallback {

		@Override
		@SideOnly(Side.CLIENT)
		public void addHitEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
			setTexture(fx, world, x, y, z, meta);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addDestroyEffects(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
			setTexture(fx, world, x, y, z, meta);
		}

		@SideOnly(Side.CLIENT)
		private void setTexture(EntityDiggingFX fx, World world, int x, int y, int z, int meta) {
			fx.setParticleIcon(instance.getIcon(world, x, y, z, 0));
		}

	}

	private static final ParticleHelperCallback callback = new ParticleCallback();
	
	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer) {
		return ParticleHelper.addDestroyEffects(world, instance, pos.getX(), pos.getY(), pos.getZ(), getMetaFromState(world.getBlockState(pos)), effectRenderer, callback);
	}
	
	@Override
	public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return getMetaFromState(world.getBlockState(pos)) == 5;
	}

	@Override
	public String[] getVariants() {
		return null;
	}
}
