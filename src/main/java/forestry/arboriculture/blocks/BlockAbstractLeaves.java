package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;

/**
 * Parent class for shared behavior between {@link BlockDefaultLeaves} and {@link BlockForestryLeaves}
 */
//TODO merge some leaf blocks, do we really need 4 block classes ?
public abstract class BlockAbstractLeaves extends LeavesBlock implements IColoredBlock {
	public static final int FOLIAGE_COLOR_INDEX = 0;
	public static final int FRUIT_COLOR_INDEX = 2;

	public BlockAbstractLeaves(Properties properties) {
		super(properties);
	}

	@Nullable
	protected abstract ITree getTree(IBlockReader world, BlockPos pos);

	@Override
	public final void fillItemCategory(ItemGroup tab, NonNullList<ItemStack> list) {
		// creative menu shows BlockDecorativeLeaves instead of these
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			return ItemStack.EMPTY;
		}
		IAlleleTreeSpecies species = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
		return species.getLeafProvider().getDecorativeLeaves();
	}

	//TODO since loot done in loot table I don't know if this works
	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @Nonnull ItemStack item, World world, BlockPos pos, int fortune) {
		ITree tree = getTree(world, pos);
		if (tree == null) {
			tree = TreeDefinition.Oak.createIndividual();
		}
		IAlleleTreeSpecies species = tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES);
		ItemStack decorativeLeaves = species.getLeafProvider().getDecorativeLeaves();
		if (decorativeLeaves.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Collections.singletonList(decorativeLeaves);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		ITree tree = getTree(worldIn, pos);
		if (tree != null && TreeDefinition.Willow.getUID().equals(tree.getIdentifier())) {
			return VoxelShapes.empty();
		}
		return super.getCollisionShape(state, worldIn, pos, context);
	}

	/**
	 * Used for walking through willow leaves.
	 */
	@Override
	public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		super.entityInside(state, worldIn, pos, entityIn);
		Vector3d motion = entityIn.getDeltaMovement();
		entityIn.setDeltaMovement(motion.x() * 0.4D, motion.y(), motion.z() * 0.4D);
	}

	/* RENDERING */
	//	@Override	//TODO is final method in block now
	//	public boolean isOpaqueCube(BlockState state) {
	//		return !Proxies.render.fancyGraphicsEnabled();
	//	}

	//TODO more hitbox stuff
	//	@OnlyIn(Dist.CLIENT)
	//	@Override
	//	public final boolean shouldSideBeRendered(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
	//		return (Proxies.render.fancyGraphicsEnabled() || blockAccess.getBlockState(pos.offset(side)).getBlock() != this) &&
	//				BlockUtil.shouldSideBeRendered(blockState, blockAccess, pos, side);
	//	}

	/* PROPERTIES */
	@Override
	public final int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public final boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return true;
	}

	@Override
	public final int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		if (face == Direction.DOWN) {
			return 20;
		} else if (face != Direction.UP) {
			return 10;
		} else {
			return 5;
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> ret = super.getDrops(state, builder);
		Entity entity = builder.getOptionalParameter(LootParameters.THIS_ENTITY);
		PlayerEntity player = null;
		GameProfile profile = null;
		if (entity instanceof PlayerEntity) {
			player = (PlayerEntity) entity;
			profile = player.getGameProfile();
		}
		World world = builder.getLevel();
		BlockPos pos = new BlockPos(builder.getParameter(LootParameters.ORIGIN));
		ItemStack tool = builder.getParameter(LootParameters.TOOL);
		int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
		float saplingModifier = 1.0f;
		Item toolItem = tool.getItem();
		if (toolItem instanceof IToolGrafter && player != null) {
			IToolGrafter grafter = (IToolGrafter) toolItem;
			saplingModifier = grafter.getSaplingModifier(tool, world, player, pos);
			//tool.damageItem(1, player, p -> {});
			//tool.
			if (tool.isEmpty()) {
				//ForgeEventFactory.onPlayerDestroyItem(player, tool, Hand.MAIN_HAND);
			}
		}
		NonNullList<ItemStack> drops = NonNullList.create();
		// leaves not harvested, get drops normally
		getLeafDrop(drops, world, profile, pos, saplingModifier, fortune, builder);
		ret.addAll(drops);
		return ret;
	}

	protected abstract void getLeafDrop(NonNullList<ItemStack> drops, World world, @Nullable GameProfile playerProfile, BlockPos pos, float saplingModifier, int fortune, LootContext.Builder builder);
}
