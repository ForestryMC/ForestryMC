/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginArboriculture;

public class BlockSapling extends BlockTreeContainer {

	public static TileSapling getSaplingTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileSapling))
			return null;

		return (TileSapling) tile;
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
		defaultIcon = TextureManager.getInstance().registerTex(register, "germlings/sapling.treeBalsa");

		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values()) {
			if (allele instanceof IAlleleTreeSpecies)
				((IAlleleTreeSpecies) allele).getIconProvider().registerIcons(register);
			if (allele instanceof IAlleleFruit)
				((IAlleleFruit) allele).getProvider().registerIcons(register);
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
		if (sapling == null)
			return defaultIcon;

		if (sapling.getTree() == null)
			return defaultIcon;

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
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
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
