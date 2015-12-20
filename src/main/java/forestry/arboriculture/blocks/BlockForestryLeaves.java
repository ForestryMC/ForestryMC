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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.LeafDecayHelper;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.proxy.Proxies;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.UnlistedBlockAccess;
import forestry.core.utils.UnlistedBlockPos;
import forestry.plugins.PluginArboriculture;

public class BlockForestryLeaves extends BlockLeavesBase implements ITileEntityProvider, IShearable, IGrowable, IModelRegister {

	public BlockForestryLeaves() {
		super(Material.leaves, false);
		this.setCreativeTab(Tabs.tabArboriculture);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] { UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS });
	}

	/* TILE ENTITY */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLeaves();
	}

	public static TileLeaves getLeafTile(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
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
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		TileLeaves leafTile = getLeafTile(world, pos);
		if (leafTile == null || leafTile.isDecorative()) {
			return;
		}

		int fortune = EnchantmentHelper.getFortuneModifier(player);
		float saplingModifier = 1.0f;

		if (!world.isRemote) {
			ItemStack held = player.inventory.getCurrentItem();
			if (held != null && held.getItem() instanceof IToolGrafter) {
				saplingModifier = ((IToolGrafter) held.getItem()).getSaplingModifier(held, world, player, pos);
				held.damageItem(1, player);
				if (held.stackSize <= 0) {
					player.destroyCurrentEquippedItem();
				}
			}
		}
		GameProfile playerProfile = player.getGameProfile();
		ArrayList<ItemStack> leafDrops = getLeafDrop(world, playerProfile, pos, saplingModifier, fortune);
		drops.set(leafDrops);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = drops.get();
		drops.remove();

		// leaves not harvested, get drops normally
		if (ret == null) {
			ret = getLeafDrop(world, null, pos, 1.0f, fortune);
		}

		return ret;
	}

	private static ArrayList<ItemStack> getLeafDrop(IBlockAccess world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		ArrayList<ItemStack> prod = new ArrayList<>();

		TileLeaves tile = getLeafTile(world, pos);
		if (tile == null || tile.getTree() == null || tile.isDecorative()) {
			return prod;
		}

		// Add saplings
		ITree[] saplings = tile.getTree().getSaplings((World) world, playerProfile, pos, saplingModifier);

		for (ITree sapling : saplings) {
			if (sapling != null) {
				prod.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING.ordinal()));
			}
		}

		// Add fruits
		if (tile.hasFruit()) {
			Collections.addAll(prod, tile.getTree().produceStacks((World) world, pos, tile.getRipeningTime()));
		}

		return prod;
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack itemStack = super.getPickBlock(target, world, pos, player);
		TileLeaves leaves = getLeafTile(world, pos);
		NBTTagCompound leavesNBT = new NBTTagCompound();
		leaves.writeToNBTDecorative(leavesNBT);
		itemStack.setTagCompound(leavesNBT);
		return itemStack;
	}
	
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		List<ItemStack> ret = new ArrayList(Arrays.asList(new ItemStack(this)));

		TileLeaves leaves = getLeafTile(world, pos);
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
	public void beginLeavesDecay(World world, BlockPos pos) {
		TileLeaves tile = getLeafTile(world, pos);
		if (tile == null || tile.isDecorative()) {
			return;
		}
		super.beginLeavesDecay(world, pos);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		TileLeaves tileLeaves = getLeafTile(world, pos);
		if (tileLeaves != null && TreeDefinition.Willow.getUID().equals(tileLeaves.getSpeciesUID())) {
			return null;
		}
		return super.getCollisionBoundingBox(world, pos, state);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entity) {
		entity.motionX *= 0.4D;
		entity.motionZ *= 0.4D;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		TileLeaves tileLeaves = getLeafTile(world, pos);
		if (tileLeaves == null || tileLeaves.isDecorative()) {
			return;
		}

		LeafDecayHelper.leafDecay(this, world, pos);

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
	
	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {

		TileLeaves leaves = getLeafTile(world,pos);
		if (leaves == null) {
			return super.colorMultiplier(world, pos);
		}

		int colour = leaves.getFoliageColour(Proxies.common.getClientInstance().thePlayer);
		if (colour == PluginArboriculture.proxy.getFoliageColorBasic()) {
			colour = super.colorMultiplier(world, pos);
		}

		return colour;
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	/* MODELS */
	@Override
	public void registerModel(Item item, IModelManager manager) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("forestry:leaves", "inventory"));
	}

	/* PROPERTIES */
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 60;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.DOWN) {
			return 20;
		} else if (face != EnumFacing.UP) {
			return 10;
		} else {
			return 5;
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		ItemStack heldItem = player.getHeldItem();
		TileEntity tile = world.getTileEntity(pos);
		IButterfly caterpillar = tile instanceof TileLeaves ? ((TileLeaves) tile).getCaterpillar() : null;
		if (heldItem != null && (heldItem.getItem() instanceof IToolScoop) && caterpillar != null) {
			ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.CATERPILLAR.ordinal());
			ItemStackUtil.dropItemStackAsEntity(butterfly, world, pos);
			((TileLeaves) tile).setCaterpillar(null);
			return true;
		}
		
		return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
	}

	/* IGrowable */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileLeaves leafTile = getLeafTile(world, pos);
		if (leafTile != null) {
			return leafTile.hasFruit() && leafTile.getRipeness() < 1.0f;
		}
		return false;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		TileLeaves leafTile = getLeafTile(world, pos);
		if (leafTile != null) {
			leafTile.addRipeness(1.0f);
		}
	}
}
