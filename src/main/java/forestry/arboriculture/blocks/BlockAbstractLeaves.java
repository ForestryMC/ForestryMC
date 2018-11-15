package forestry.arboriculture.blocks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.ITree;
import forestry.api.core.IItemModelRegister;
import forestry.arboriculture.LeafDecayHelper;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import forestry.core.proxy.Proxies;
import forestry.core.utils.BlockUtil;

/**
 * Parent class for shared behavior between {@link BlockDefaultLeaves} and {@link BlockForestryLeaves}
 */
public abstract class BlockAbstractLeaves extends BlockLeaves implements IItemModelRegister, IColoredBlock {
	public static final int FOLIAGE_COLOR_INDEX = 0;
	public static final int FRUIT_COLOR_INDEX = 2;

	public static final int DECAYABLE_FLAG = 4;
	public static final int CHECK_DECAY_FLAG = 8;

	@Nullable
	protected abstract ITree getTree(IBlockAccess world, BlockPos pos);

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		LeafDecayHelper.leafDecay(this, world, pos);
	}

	@Override
	public abstract int getMetaFromState(IBlockState state);

	@Override
	public abstract IBlockState getStateFromMeta(int meta);

	@Override
	public final void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		// creative menu shows BlockDecorativeLeaves instead of these
	}

	@Override
	public final ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return ItemStack.EMPTY;
		}

		return tree.getGenome().getDecorativeLeaves();
	}

	@Override
	public final List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			tree = TreeDefinition.Oak.getIndividual();
		}

		ItemStack decorativeLeaves = tree.getGenome().getDecorativeLeaves();
		if (decorativeLeaves.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.singletonList(decorativeLeaves);
		}
	}

	@Nullable
	@Override
	public final AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		ITree tree = getTree(worldIn, pos);
		if (tree != null && TreeDefinition.Willow.getUID().equals(tree.getIdent())) {
			return null;
		}
		return super.getCollisionBoundingBox(blockState, worldIn, pos);
	}

	/**
	 * Used for walking through willow leaves.
	 */
	@Override
	public final void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		super.onEntityCollision(worldIn, pos, state, entityIn);
		entityIn.motionX *= 0.4D;
		entityIn.motionZ *= 0.4D;
	}

	/* RENDERING */
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return !Proxies.render.fancyGraphicsEnabled();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public final boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) &&
			BlockUtil.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED; // fruit overlays require CUTOUT_MIPPED, even in Fast graphics
	}

	/**
	 * unused, just here to satisfy BlockLeaves
	 */
	@Override
	public final BlockPlanks.EnumType getWoodType(int meta) {
		return BlockPlanks.EnumType.OAK;
	}

	/* PROPERTIES */
	@Override
	public final int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 60;
	}

	@Override
	public final boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return true;
	}

	@Override
	public final int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (face == EnumFacing.DOWN) {
			return 20;
		} else if (face != EnumFacing.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	/* DROP HANDLING */
	// Hack: 	When harvesting leaves we need to get the drops in onBlockHarvested,
	// 			because there is no Player parameter in getDrops()
	//          and Mojang destroys the block and tile before calling getDrops.
	// TODO in 1.13 - use new getDrops() (https://github.com/MinecraftForge/MinecraftForge/pull/4727)
	private final ThreadLocal<NonNullList<ItemStack>> drops = new ThreadLocal<>();

	/**
	 * {@link IToolGrafter}'s drop bonus handling is done here.
	 */
	@Override
	public final void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack());
		float saplingModifier = 1.0f;

		ItemStack heldStack = player.inventory.getCurrentItem();
		Item heldItem = heldStack.getItem();
		if (heldItem instanceof IToolGrafter) {
			IToolGrafter grafter = (IToolGrafter) heldItem;
			saplingModifier = grafter.getSaplingModifier(heldStack, world, player, pos);
			heldStack.damageItem(1, player);
			if (heldStack.isEmpty()) {
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, heldStack, EnumHand.MAIN_HAND);
			}
		}

		GameProfile playerProfile = player.getGameProfile();
		NonNullList<ItemStack> drops = NonNullList.create();
		getLeafDrop(drops, world, playerProfile, pos, saplingModifier, fortune);
		this.drops.set(drops);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = this.drops.get();
		this.drops.remove();
		if (ret != null) {
			drops.addAll(ret);
		} else {
			if (!(world instanceof World)) {
				return;
			}
			// leaves not harvested, get drops normally
			getLeafDrop(drops, (World) world, null, pos, 1.0f, fortune);
		}
	}

	protected abstract void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune);
}
