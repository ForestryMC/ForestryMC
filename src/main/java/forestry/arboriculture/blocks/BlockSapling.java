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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.arboriculture.tiles.TileSapling;
import forestry.core.render.TextureManager;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginArboriculture;

public class BlockSapling extends BlockTreeContainer implements IGrowable {

	public static TileSapling getSaplingTile(IBlockAccess world, int x, int y, int z) {
		return TileUtil.getTile(world, x, y, z, TileSapling.class);
	}

	public BlockSapling() {
		super(Material.plants);

		float factor = 0.4F;
		setBlockBounds(0.5F - factor, 0.0F, 0.5F - factor, 0.5F + factor, factor * 2.0F, 0.5F + factor);
		setStepSound(soundTypeGrass);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int meta) {
		return new TileSapling();
	}

	/* COLLISION BOX */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return PluginArboriculture.modelIdSaplings;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private static IIcon defaultIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		defaultIcon = TextureManager.registerTex(register, "germlings/sapling.treeBalsa");

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				((IAlleleTreeSpecies) allele).getIconProvider().registerIcons(register);
			}
			if (allele instanceof IAlleleFruit) {
				((IAlleleFruit) allele).getProvider().registerIcons(register);
			}
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return defaultIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling == null) {
			return defaultIcon;
		}

		if (sapling.getTree() == null) {
			return defaultIcon;
		}

		return sapling.getTree().getGenome().getPrimary().getGermlingIcon(EnumGermlingType.SAPLING, 0);
	}

	/* PLANTING */
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		TileSapling tile = getSaplingTile(world, x, y, z);
		if (tile == null) {
			return false;
		}
		if (tile.getTree() == null) {
			return false;
		}

		return tile.getTree().canStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		super.onNeighborBlockChange(world, x, y, z, neighbour);
		if (!world.isRemote && !this.canBlockStay(world, x, y, z)) {
			dropAsSapling(world, x, y, z);
			world.setBlockToAir(x, y, z);
		}

	}

	/* REMOVING */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<>();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling == null || sapling.getTree() == null) {
			return null;
		}
		return TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!world.isRemote && canHarvestBlock(player, world.getBlockMetadata(x, y, z))) {
			if (!player.capabilities.isCreativeMode) {
				dropAsSapling(world, x, y, z);
			}
		}

		return world.setBlockToAir(x, y, z);
	}

	private static void dropAsSapling(World world, int x, int y, int z) {
		if (world.isRemote) {
			return;
		}

		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = TreeManager.treeRoot.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
			ItemStackUtil.dropItemStackAsEntity(saplingStack, world, x, y, z);
		}

	}

	@Override
	/** canFertilize */
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		TileSapling saplingTile = getSaplingTile(world, x, y, z);
		if (saplingTile != null) {
			return saplingTile.canAcceptBoneMeal();
		}
		return true;
	}

	@Override
	/** shouldFertilize */
	public boolean func_149852_a(World world, Random random, int x, int y, int z) {
		return world.rand.nextFloat() < 0.45F;
	}

	@Override
	/** fertilize */
	public void func_149853_b(World world, Random random, int x, int y, int z) {
		TileSapling saplingTile = getSaplingTile(world, x, y, z);
		if (saplingTile != null) {
			saplingTile.tryGrow(true);
		}
	}
}
