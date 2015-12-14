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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.LeafDecayHelper;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.render.TextureLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.plugins.PluginArboriculture;

public class BlockForestryLeaves extends BlockNewLeaf implements ITileEntityProvider, IGrowable {

	public BlockForestryLeaves() {
		this.setCreativeTab(Tabs.tabArboriculture);
	}

	/* TILE ENTITY */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLeaves();
	}

	public static TileLeaves getLeafTile(IBlockAccess world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileLeaves) {
			return (TileLeaves) tile;
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {

		for (ITree tree : TreeManager.treeRoot.getIndividualTemplates()) {
			TileLeaves leaves = new TileLeaves();
			leaves.setDecorative();
			leaves.setTree(tree);

			NBTTagCompound leavesNBT = new NBTTagCompound();
			leaves.writeToNBTDecorative(leavesNBT);

			ItemStack itemStack = new ItemStack(item, 1, 0);
			itemStack.setTagCompound(leavesNBT);

			list.add(itemStack);
		}
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting leaves we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<ArrayList<ItemStack>> drops = new ThreadLocal<>();

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int metadata, EntityPlayer player) {
		TileLeaves leafTile = getLeafTile(world, x, y, z);
		if (leafTile == null || leafTile.isDecorative()) {
			return;
		}

		int fortune = EnchantmentHelper.getFortuneModifier(player);
		float saplingModifier = 1.0f;

		if (!world.isRemote) {
			ItemStack held = player.inventory.getCurrentItem();
			if (held != null && held.getItem() instanceof IToolGrafter) {
				saplingModifier = ((IToolGrafter) held.getItem()).getSaplingModifier(held, world, player, x, y, z);
				held.damageItem(1, player);
				if (held.stackSize <= 0) {
					player.destroyCurrentEquippedItem();
				}
			}
		}
		GameProfile playerProfile = player.getGameProfile();
		ArrayList<ItemStack> leafDrops = getLeafDrop(world, playerProfile, x, y, z, saplingModifier, fortune);
		drops.set(leafDrops);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = drops.get();
		drops.remove();

		// leaves not harvested, get drops normally
		if (ret == null) {
			ret = getLeafDrop(world, null, x, y, z, 1.0f, fortune);
		}

		return ret;
	}

	private static ArrayList<ItemStack> getLeafDrop(World world, @Nullable GameProfile playerProfile, int x, int y, int z, float saplingModifier, int fortune) {
		ArrayList<ItemStack> prod = new ArrayList<>();

		TileLeaves tile = getLeafTile(world, x, y, z);
		if (tile == null || tile.getTree() == null || tile.isDecorative()) {
			return prod;
		}

		// Add saplings
		ITree[] saplings = tile.getTree().getSaplings(world, playerProfile, x, y, z, saplingModifier);

		for (ITree sapling : saplings) {
			if (sapling != null) {
				prod.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING.ordinal()));
			}
		}

		// Add fruits
		if (tile.hasFruit()) {
			Collections.addAll(prod, tile.getTree().produceStacks(world, x, y, z, tile.getRipeningTime()));
		}

		return prod;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		ItemStack itemStack = super.getPickBlock(target, world, x, y, z);
		TileLeaves leaves = getLeafTile(world, x, y, z);
		NBTTagCompound leavesNBT = new NBTTagCompound();
		leaves.writeToNBTDecorative(leavesNBT);
		itemStack.setTagCompound(leavesNBT);
		return itemStack;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = super.onSheared(item, world, x, y, z, fortune);

		TileLeaves leaves = getLeafTile(world, x, y, z);
		NBTTagCompound shearedLeavesNBT = new NBTTagCompound();
		leaves.writeToNBTDecorative(shearedLeavesNBT);

		for (ItemStack stack : ret) {
			if (stack.getItem() instanceof ItemBlockLeaves) {
				stack.setTagCompound(shearedLeavesNBT);
			}
		}

		return ret;
	}

	@Override
	public void beginLeavesDecay(World world, int x, int y, int z) {
		TileLeaves tile = getLeafTile(world, x, y, z);
		if (tile == null || tile.isDecorative()) {
			return;
		}
		super.beginLeavesDecay(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		TileLeaves tileLeaves = getLeafTile(world, x, y, z);
		if (tileLeaves != null && TreeDefinition.Willow.getUID().equals(tileLeaves.getSpeciesUID())) {
			return null;
		}
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		entity.motionX *= 0.4D;
		entity.motionZ *= 0.4D;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		TileLeaves tileLeaves = getLeafTile(world, x, y, z);
		if (tileLeaves == null || tileLeaves.isDecorative()) {
			return;
		}

		LeafDecayHelper.leafDecay(this, world, x, y, z);

		// check leaves tile again because they can decay in super.updateTick
		if (tileLeaves.isInvalid()) {
			return;
		}

		if (world.rand.nextFloat() > 0.1) {
			return;
		}
		tileLeaves.onBlockTick();
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
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {

		TileLeaves leaves = getLeafTile(world, x, y, z);
		if (leaves == null) {
			return super.colorMultiplier(world, x, y, z);
		}

		int colour = leaves.getFoliageColour(Proxies.common.getClientInstance().thePlayer);
		if (colour == PluginArboriculture.proxy.getFoliageColorBasic()) {
			colour = super.colorMultiplier(world, x, y, z);
		}

		return colour;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	private static IIcon defaultIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		TextureLeaves.registerAllIcons(register);
		defaultIcon = TextureLeaves.get(EnumLeafType.DECIDUOUS).getFancy();
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
		if (leaves != null) {
			return leaves.getIcon(Proxies.render.fancyGraphicsEnabled());
		}

		return defaultIcon;
	}

	/* LOCALIZATION */
	@Override
	public String[] func_150125_e() {
		return new String[0];
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
		if (face == ForgeDirection.DOWN) {
			return 20;
		} else if (face != ForgeDirection.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {

		ItemStack heldItem = player.getHeldItem();
		TileEntity tile = world.getTileEntity(x, y, z);
		IButterfly caterpillar = tile instanceof TileLeaves ? ((TileLeaves) tile).getCaterpillar() : null;
		if (heldItem != null && (heldItem.getItem() instanceof IToolScoop) && caterpillar != null) {
			ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.CATERPILLAR.ordinal());
			ItemStackUtil.dropItemStackAsEntity(butterfly, world, x, y, z);
			((TileLeaves) tile).setCaterpillar(null);
			return true;
		}

		return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}

	/* IGrowable */

	@Override
	// canFertilize
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient) {
		TileLeaves leafTile = getLeafTile(world, x, y, z);
		if (leafTile != null) {
			return leafTile.hasFruit() && leafTile.getRipeness() < 1.0f;
		}
		return false;
	}

	@Override
	// shouldFertilize
	public boolean func_149852_a(World world, Random random, int x, int y, int z) {
		return true;
	}

	@Override
	// fertilize
	public void func_149853_b(World world, Random random, int x, int y, int z) {
		TileLeaves leafTile = getLeafTile(world, x, y, z);
		if (leafTile != null) {
			leafTile.addRipeness(1.0f);
		}
	}
}
