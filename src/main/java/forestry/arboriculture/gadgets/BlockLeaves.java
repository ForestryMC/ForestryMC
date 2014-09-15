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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraftforge.common.util.ForgeDirection;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.core.IToolScoop;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeGenome;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginArboriculture;
import forestry.plugins.PluginLepidopterology;

public class BlockLeaves extends BlockTreeContainer implements IAlleleSpeciesTyped {

	private static final int leavesPerCategory = 16;
	public int leavesCategory;
	int[] adjacentTreeBlocks;
	ArrayList<IAlleleTreeSpecies> alleles;

	public BlockLeaves(int leavesCategory) {
		super(Material.leaves);

		this.leavesCategory = leavesCategory;
		this.alleles = new ArrayList<IAlleleTreeSpecies>();
		this.setTickRandomly(true);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.setStepSound(soundTypeGrass);
	}

	public static TileLeaves getLeafTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileLeaves))
			return null;

		return (TileLeaves) tile;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLeaves();
	}

	/* DROP HANDLING */
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		int metadata = world.getBlockMetadata(x, y, z);

		if (Proxies.common.isSimulating(world) && canHarvestBlock(player, metadata)) {

			ItemStack held = player.inventory.getCurrentItem();
			if (held != null && held.getItem() instanceof IToolGrafter) {
				float saplingModifier = 1.0f;
				saplingModifier = ((IToolGrafter) held.getItem()).getSaplingModifier(held, world, player, x, y, z);
				held.damageItem(1, player);
				if(held.stackSize <= 0)
					player.destroyCurrentEquippedItem();

				spawnLeafDrops(world, x, y, z, metadata, saplingModifier, false);
			}
		}

		return world.setBlockToAir(x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {

		byte offset = 1;
		int shift = offset + 1;

		if (world.checkChunksExist(x - shift, y - shift, z - shift, x + shift, y + shift, z + shift))
			for (int i = -offset; i <= offset; ++i)
				for (int j = -offset; j <= offset; ++j)
					for (int k = -offset; k <= offset; ++k) {
						Block neighborBlock = world.getBlock(x + i, y + j, z + k);
						neighborBlock.beginLeavesDecay(world, x + i, y + j, z + k);
					}

		spawnLeafDrops(world, x, y, z, metadata, 1.0f, true);
		super.breakBlock(world, x, y, z, block, metadata);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return StackUtils.EMPTY_STACK_LIST;
	}

	private void removeLeaves(World world, int x, int y, int z) {
		this.spawnLeafDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 1.0f, true);
		world.setBlockToAir(x, y, z);
	}

	private void spawnLeafDrops(World world, int x, int y, int z, int metadata, float saplingModifier, boolean doFruitDrop) {
		for (ItemStack drop : getLeafDrop(world, x, y, z, metadata, saplingModifier, doFruitDrop))
			if (drop != null)
				StackUtils.dropItemStackAsEntity(drop, world, x, y, z);
	}

	private ArrayList<ItemStack> getLeafDrop(World world, int x, int y, int z, int metadata, float saplingModifier, boolean doFruitDrop) {
		ArrayList<ItemStack> prod = new ArrayList<ItemStack>();

		TileLeaves tile = getLeafTile(world, x, y, z);
		if (tile == null)
			return prod;

		if (tile.getTree() == null)
			return prod;

		// Add saplings
		ITree[] saplings = tile.getTree().getSaplings(world, x, y, z, saplingModifier);
		for (ITree sapling : saplings)
			if (sapling != null)
				prod.add(PluginArboriculture.treeInterface.getMemberStack(sapling, EnumGermlingType.SAPLING.ordinal()));

		// Add fruits
		if (doFruitDrop && tile.hasFruit())
			for (ItemStack stack : tile.getTree().produceStacks(world, x, y, z, tile.getRipeningTime()))
				prod.add(stack);

		return prod;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube() {
		return !Proxies.render.fancyGraphicsEnabled();
	}

	@Override
	public int getRenderType() {
		return PluginArboriculture.modelIdLeaves;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		double var1 = 0.5D;
		double var3 = 1.0D;
		return ColorizerFoliage.getFoliageColor(var1, var3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {

		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves == null)
			return ColorizerFoliage.getFoliageColorBasic();

		int colour = leaves.getFoliageColour(Proxies.common.getClientInstance().thePlayer);
		if (colour == PluginArboriculture.proxy.getFoliageColorBasic())
			colour = world.getBiomeGenForCoords(x, z).getBiomeFoliageColor(x, y, z); // TODO: vanilla uses a more elaborate color query

		return colour;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private static IIcon defaultIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		defaultIcon = TextureManager.getInstance().registerTex(register, "leaves/deciduous.fancy");
		registerTreeAlleles();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		IAlleleTreeSpecies allele = getAlleleForMeta(metadata);
		Tree fakeTree = new Tree((TreeGenome)null);
		short iconIndex = allele.getLeafIconIndex(fakeTree, Proxies.render.fancyGraphicsEnabled());
		return TextureManager.getInstance().getIcon(iconIndex);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves != null)
			return leaves.getIcon(Proxies.render.fancyGraphicsEnabled());

		return defaultIcon;
	}

	/* IAlleleSpeciesTyped */
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
		return leavesCategory * leavesPerCategory;
	}

	private int maxAlleleIndex() {
		return ((leavesCategory + 1) * leavesPerCategory) - 1;
	}

	public boolean hasAllele(IAlleleSpecies allele) {
		int index = alleles.indexOf(allele);
		return index >= minAlleleIndex() && index <= maxAlleleIndex();
	}

	public int getMetaForAllele(IAlleleSpecies allele) {
		if (!hasAllele(allele))
			return -1;
		return alleles.indexOf(allele) - minAlleleIndex();
	}

	public IAlleleTreeSpecies getAlleleForMeta(int metadata) {
		int index = minAlleleIndex() + metadata;
		if (index >= alleles.size())
			return null;
		return alleles.get(index);
	}

	public String getBlockKind() {
		return "leaves";
	}

	/* BREAKING, LEAF DECAY */
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {

		super.updateTick(world, x, y, z, random);
		if (!Proxies.common.isSimulating(world))
			return;

		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) != 0 && (meta & 4) == 0) {
			byte offset = 4;
			int shift = offset + 1;
			byte var9 = 32;
			int var10 = var9 * var9;
			int var11 = var9 / 2;

			if (this.adjacentTreeBlocks == null)
				this.adjacentTreeBlocks = new int[var9 * var9 * var9];

			int var12;

			if (world.checkChunksExist(x - shift, y - shift, z - shift, x + shift, y + shift, z + shift)) {

				int var13;
				int var14;
				int var15;

				for (var12 = -offset; var12 <= offset; ++var12)
					for (var13 = -offset; var13 <= offset; ++var13)
						for (var14 = -offset; var14 <= offset; ++var14) {
							Block block = world.getBlock(x + var12, y + var13, z + var14);

							if (block.canSustainLeaves(world, x + var12, y + var13, z + var14))
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
							else if (block.isLeaves(world, x + var12, y + var13, z + var14))
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
							else
								this.adjacentTreeBlocks[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
						}

				for (var12 = 1; var12 <= 4; ++var12)
					for (var13 = -offset; var13 <= offset; ++var13)
						for (var14 = -offset; var14 <= offset; ++var14)
							for (var15 = -offset; var15 <= offset; ++var15)
								if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11] == var12 - 1) {

									if (this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2)
										this.adjacentTreeBlocks[(var13 + var11 - 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;

									if (this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] == -2)
										this.adjacentTreeBlocks[(var13 + var11 + 1) * var10 + (var14 + var11) * var9 + var15 + var11] = var12;

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] == -2)
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 - 1) * var9 + var15 + var11] = var12;

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] == -2)
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11 + 1) * var9 + var15 + var11] = var12;

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] == -2)
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + (var15 + var11 - 1)] = var12;

									if (this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] == -2)
										this.adjacentTreeBlocks[(var13 + var11) * var10 + (var14 + var11) * var9 + var15 + var11 + 1] = var12;
								}
			}

			var12 = this.adjacentTreeBlocks[var11 * var10 + var11 * var9 + var11];

			if (var12 >= 0)
				world.setBlockMetadataWithNotify(x, y, z, meta & -9, Defaults.FLAG_BLOCK_SYNCH);
			else
				this.removeLeaves(world, x, y, z);
		}
	}

	@Override
	public void beginLeavesDecay(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) | 8, Defaults.FLAG_BLOCK_SYNCH);
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 60;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		if (face == ForgeDirection.DOWN)
			return 20;
		else if (face != ForgeDirection.UP)
			return 10;
		else
			return 5;
	}

	@Override
	public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y,
			int z, EntityPlayer player, int par6, float par7,
			float par8, float par9) {

		ItemStack heldItem = player.getHeldItem();
		TileEntity tile = world.getTileEntity(x, y, z);
		IButterfly caterpillar = tile instanceof TileLeaves ? ((TileLeaves) tile).getCaterpillar() : null;
		if(heldItem != null && heldItem.getItem() instanceof IToolScoop
				&& caterpillar != null) {
			StackUtils.dropItemStackAsEntity(PluginLepidopterology.butterflyInterface.getMemberStack(caterpillar, EnumFlutterType.CATERPILLAR.ordinal()),
					world, x, y, z);
			((TileLeaves) tile).setCaterpillar(null);
			return true;
		}

		return super.onBlockActivated(world, x, y, z, player,
				par6, par7, par8, par9);
	}

}
