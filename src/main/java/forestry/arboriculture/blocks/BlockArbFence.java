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
package forestry.arboriculture.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.render.IconProviderWood;
import forestry.arboriculture.tiles.TileWood;
import forestry.core.render.ParticleHelper;
import forestry.plugins.PluginArboriculture;

public class BlockArbFence extends BlockFence implements IWoodTyped, ITileEntityProvider {

	private final ParticleHelper.Callback particleCallback;
	private final boolean fireproof;

	public BlockArbFence(boolean fireproof) {
		super("", Material.wood);

		this.fireproof = fireproof;

		setHardness(2.0F);
		setResistance(5.0F);
		setHarvestLevel("axe", 0);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);

		this.particleCallback = new ParticleHelper.DefaultCallback(this);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (EnumWoodType woodType : EnumWoodType.VALUES) {
			list.add(TreeManager.woodItemAccess.getFence(woodType, fireproof));
		}
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z) {
		if (!isFence(world, x, y, z)) {
			Block block = world.getBlock(x, y, z);
			if (block == this || block instanceof BlockFenceGate) {
				return true;
			}

			return block.getMaterial().isOpaque() && block.renderAsNormalBlock() && block.getMaterial() != Material.gourd;
		} else {
			return true;
		}
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		IconProviderWood.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return IconProviderWood.getPlankIcon(EnumWoodType.LARCH);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileWood wood = TileWood.getWoodTile(world, x, y, z);
		EnumWoodType woodType = wood.getWoodType();
		return IconProviderWood.getPlankIcon(woodType);
	}

	private static boolean isFence(IBlockAccess world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return PluginArboriculture.validFences.contains(block);
	}

	@Override
	public final TileEntity createNewTileEntity(World world, int meta) {
		return new TileWood();
	}

	/* PROPERTIES */
	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return TileWood.getPickBlock(this, world, x, y, z);
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ArrayList<ItemStack>> drops = new ThreadLocal<>();

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer playerProfile) {
		drops.set(TileWood.getDrops(this, world, x, y, z));
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = drops.get();
		drops.remove();

		// not harvested, get drops normally
		if (ret == null) {
			ret = TileWood.getDrops(this, world, x, y, z);
		}

		return ret;
	}

	@Override
	public final float getBlockHardness(World world, int x, int y, int z) {
		TileWood wood = TileWood.getWoodTile(world, x, y, z);
		if (wood == null) {
			return EnumWoodType.DEFAULT_HARDNESS;
		}
		return wood.getWoodType().getHardness();
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return !isFireproof();
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return isFireproof() ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
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
}
