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

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumGermlingType;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IToolScoop;
import forestry.api.core.Tabs;
import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.EnumFlutterType;
import forestry.api.lepidopterology.IButterfly;
import forestry.arboriculture.LeafDecayHelper;
import forestry.arboriculture.PluginArboriculture;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TileLeaves;
import forestry.core.blocks.IColoredBlock;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.proxy.Proxies;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockForestryLeaves extends BlockLeaves implements ITileEntityProvider, IGrowable, IItemModelRegister, IColoredBlock {

	public BlockForestryLeaves() {
		setCreativeTab(Tabs.tabArboriculture);
        setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, false).withProperty(DECAYABLE, true));
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(UnlistedBlockPos.POS, pos)
				.withProperty(UnlistedBlockAccess.BLOCKACCESS, world);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[]{DECAYABLE, CHECK_DECAY}, new IUnlistedProperty[]{UnlistedBlockPos.POS, UnlistedBlockAccess.BLOCKACCESS});
	}
	
    @Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		if (!state.getValue(DECAYABLE)) {
			i |= 4;
		}

		if (state.getValue(CHECK_DECAY)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
	}

	/* TILE ENTITY */
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileLeaves();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {

	}

	/* DROP HANDLING */
	// Hack: 	When harvesting leaves we need to get the drops in onBlockHarvested,
	// 			because Mojang destroys the block and tile before calling getDrops.
	private final ThreadLocal<List<ItemStack>> drops = new ThreadLocal<>();

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leafTile == null) {
			return;
		}

		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack());
		float saplingModifier = 1.0f;

		if (!world.isRemote) {
			ItemStack held = player.inventory.getCurrentItem();
			if (held != null && held.getItem() instanceof IToolGrafter) {
				saplingModifier = ((IToolGrafter) held.getItem()).getSaplingModifier(held, world, player, pos);
				held.damageItem(1, player);
				if (held.stackSize <= 0) {
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, held, EnumHand.MAIN_HAND);
					player.setHeldItem(EnumHand.MAIN_HAND, null);
				}
			}
		}
		GameProfile playerProfile = player.getGameProfile();
		List<ItemStack> leafDrops = getLeafDrop(world, playerProfile, pos, saplingModifier, fortune);
		drops.set(leafDrops);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = drops.get();
		drops.remove();

		// leaves not harvested, get drops normally
		if (ret == null) {
			ret = getLeafDrop(world, null, pos, 1.0f, fortune);
		}

		return ret;
	}

	private static List<ItemStack> getLeafDrop(IBlockAccess world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune) {
		List<ItemStack> prod = new ArrayList<>();

		TileLeaves tile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (tile == null) {
			return prod;
		}

		ITree tree = tile.getTree();
		if (tree == null) {
			return prod;
		}

		// Add saplings
		ITree[] saplings = tree.getSaplings((World) world, playerProfile, pos, saplingModifier);

		for (ITree sapling : saplings) {
			if (sapling != null) {
				prod.add(TreeManager.treeRoot.getMemberStack(sapling, EnumGermlingType.SAPLING));
			}
		}

		// Add fruits
		if (tile.hasFruit()) {
			prod.addAll(tree.produceStacks((World) world, pos, tile.getRipeningTime()));
		}

		return prod;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves == null) {
			return null;
		}

		ITree tree = leaves.getTree();
		if (tree == null) {
			return null;
		}

		String speciesUid = tree.getGenome().getPrimary().getUID();
		return PluginArboriculture.blocks.getDecorativeLeaves(speciesUid);
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		TileLeaves leaves = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leaves == null) {
			return null;
		}

		ITree tree = leaves.getTree();
		if (tree == null) {
			return null;
		}

		String speciesUid = tree.getGenome().getPrimary().getUID();
		return Collections.singletonList(PluginArboriculture.blocks.getDecorativeLeaves(speciesUid));
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		TileLeaves tileLeaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
		if (tileLeaves != null && TreeDefinition.Willow.getUID().equals(tileLeaves.getSpeciesUID())) {
			return null;
		}
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
		entityIn.motionX *= 0.4D;
		entityIn.motionZ *= 0.4D;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		LeafDecayHelper.leafDecay(this, world, pos);

		TileLeaves tileLeaves = TileUtil.getTile(world, pos, TileLeaves.class);

		// check leaves tile because they might have decayed
		if (tileLeaves != null && !tileLeaves.isInvalid() && rand.nextFloat() <= 0.1) {
			tileLeaves.onBlockTick(world, pos, state, rand);
		}
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !Proxies.render.fancyGraphicsEnabled();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) && BlockUtil.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED; // fruit overlays require CUTOUT_MIPPED, even in Fast graphics
	}

	@Override
	public BlockPlanks.EnumType getWoodType(int meta) {
		return BlockPlanks.EnumType.OAK;
	}
	
	/* MODELS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, IModelManager manager) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("forestry:leaves", "inventory"));
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tile = worldIn.getTileEntity(pos);
		IButterfly caterpillar = tile instanceof TileLeaves ? ((TileLeaves) tile).getCaterpillar() : null;
		if (heldItem != null && heldItem.getItem() instanceof IToolScoop && caterpillar != null) {
			ItemStack butterfly = ButterflyManager.butterflyRoot.getMemberStack(caterpillar, EnumFlutterType.CATERPILLAR);
			ItemStackUtil.dropItemStackAsEntity(butterfly, worldIn, pos);
			((TileLeaves) tile).setCaterpillar(null);
			return true;
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	/* IGrowable */

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
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
		TileLeaves leafTile = TileUtil.getTile(world, pos, TileLeaves.class);
		if (leafTile != null) {
			leafTile.addRipeness(0.5f);
		}
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		TileLeaves leaves = TileUtil.getTile(worldIn, pos, TileLeaves.class);
		if (leaves == null) {
			return PluginArboriculture.proxy.getFoliageColorBasic();
		}

		if (tintIndex == 0) {
			EntityPlayer thePlayer = Proxies.common.getPlayer();
			return leaves.getFoliageColour(thePlayer);
		} else {
			return leaves.getFruitColour();
		}
	}
}
