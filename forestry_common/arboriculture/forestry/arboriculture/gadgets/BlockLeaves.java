/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
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
import forestry.api.core.IToolScoop;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.config.Defaults;
import forestry.core.proxy.Proxies;
import forestry.core.render.TextureManager;
import forestry.core.utils.StackUtils;
import forestry.plugins.PluginArboriculture;
import forestry.plugins.PluginLepidopterology;

public class BlockLeaves extends BlockTreeContainer {

	int[] adjacentTreeBlocks;

	public BlockLeaves() {
		super(Material.leaves);
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
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
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
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return defaultIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves != null)
			return leaves.getIcon(Proxies.render.fancyGraphicsEnabled());

		return defaultIcon;
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
