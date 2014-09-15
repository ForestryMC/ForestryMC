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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.core.proxy.Proxies;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginArboriculture;

public class BlockSapling extends BlockTreeContainer {

	public static TileSapling getSaplingTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileSapling))
			return null;

		return (TileSapling) tile;
	}

	private static final int saplingsPerCategory = 16;
	public int saplingCategory;

	ArrayList<IAlleleTreeSpecies> alleles;

	public BlockSapling(int saplingCategory) {
		super(Material.plants);

		this.saplingCategory = saplingCategory;
		this.alleles = new ArrayList<IAlleleTreeSpecies>();
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
	@Override
	public void registerBlockIcons(IIconRegister register) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies)
				((IAlleleTreeSpecies) allele).getIconProvider().registerIcons(register);
			if (allele instanceof IAlleleFruit)
				((IAlleleFruit) allele).getProvider().registerIcons(register);
		}
		registerTreeAlleles();
	}

	private void registerTreeAlleles() {
		if (!alleles.isEmpty())
			return;

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies) {
				IAlleleTreeSpecies treeAllele = (IAlleleTreeSpecies) allele;
				alleles.add(treeAllele);
			}
		}
	}

	private int minAlleleIndex() {
		return saplingCategory * saplingsPerCategory;
	}

	private int maxAlleleIndex() {
		return ((saplingCategory + 1) * saplingsPerCategory) - 1;
	}

	public boolean hasAllele(IAlleleTreeSpecies allele) {
		int index = alleles.indexOf(allele);
		return index >= minAlleleIndex() && index <= maxAlleleIndex();
	}

	public int getMetaForAllele(IAlleleTreeSpecies allele) {
		if (!hasAllele(allele))
			return -1;
		return alleles.indexOf(allele) - minAlleleIndex();
	}

	public IAlleleTreeSpecies getAllele(int metadata) {
		int index = minAlleleIndex() + metadata;
		if (index >= alleles.size())
			return null;
		return alleles.get(index);
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		IAlleleTreeSpecies allele = getAllele(metadata);
		return allele.getGermlingIcon(EnumGermlingType.SAPLING, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling == null || sapling.getTree() == null)
			return null;

		return sapling.getTree().getGenome().getPrimary().getGermlingIcon(EnumGermlingType.SAPLING, 0);
	}

	/* PLANTING */
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		TileSapling tile = getSaplingTile(world, x, y, z);
		if (tile == null)
			return false;
		if (tile.getTree() == null)
			return false;

		return tile.getTree().canStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		if (Proxies.common.isSimulating(world) && !this.canBlockStay(world, x, y, z)) {
			dropAsSapling(world, x, y, z);
			world.setBlockToAir(x, y, z);
		}

	}

	/* REMOVING */
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return new ArrayList<ItemStack>();
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, world.getBlockMetadata(x, y, z)))
			if (!player.capabilities.isCreativeMode)
				dropAsSapling(world, x, y, z);

		return world.setBlockToAir(x, y, z);
	}

	private void dropAsSapling(World world, int x, int y, int z) {
		if (!Proxies.common.isSimulating(world))
			return;

		TileSapling sapling = getSaplingTile(world, x, y, z);
		if (sapling != null && sapling.getTree() != null) {
			ItemStack saplingStack = PluginArboriculture.treeInterface.getMemberStack(sapling.getTree(), EnumGermlingType.SAPLING.ordinal());
			StackUtils.dropItemStackAsEntity(saplingStack, world, x, y, z);
		}

	}
}
