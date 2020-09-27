package forestry.arboriculture.blocks;

import com.mojang.authlib.GameProfile;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.blocks.IColoredBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
    public final void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> list) {
        // creative menu shows BlockDecorativeLeaves instead of these
    }

    @Override
    public ItemStack getPickBlock(
            BlockState state,
            RayTraceResult target,
            IBlockReader world,
            BlockPos pos,
            PlayerEntity player
    ) {
        ITree tree = getTree(world, pos);
        if (tree == null) {
            return ItemStack.EMPTY;
        }

        return tree.getGenome().getActiveAllele(TreeChromosomes.SPECIES).getLeafProvider().getDecorativeLeaves();
    }

    //TODO since loot done in loot table I don't know if this works
    @Nonnull
    @Override
    public List<ItemStack> onSheared(
            @Nullable PlayerEntity player,
            @Nonnull ItemStack item,
            World world,
            BlockPos pos,
            int fortune
    ) {
        ITree tree = getTree(world, pos);
        if (tree == null) {
            tree = TreeDefinition.Oak.createIndividual();
        }

        ItemStack decorativeLeaves = tree.getGenome()
                                         .getActiveAllele(TreeChromosomes.SPECIES)
                                         .getLeafProvider()
                                         .getDecorativeLeaves();
        if (decorativeLeaves.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(decorativeLeaves);
        }
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            IBlockReader worldIn,
            BlockPos pos,
            ISelectionContext context
    ) {
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
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        Vector3d motion = entityIn.getMotion();
        entityIn.setMotion(motion.getX() * 0.4D, motion.getY(), motion.getZ() * 0.4D);
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

    /* DROP HANDLING */
    // Hack: 	When harvesting leaves we need to get the drops in onBlockHarvested,
    // 			because there is no Player parameter in getDrops()
    //          and Mojang destroys the block and tile before calling getDrops.
    // TODO in 1.13 - use new getDrops() (https://github.com/MinecraftForge/MinecraftForge/pull/4727)
    //TODO in 1.14 - I think this can be done in loot tables by match_tool?
    private final ThreadLocal<NonNullList<ItemStack>> drops = new ThreadLocal<>();

    /**
     * {@link IToolGrafter}'s drop bonus handling is done here.
     */
    //TODO leaf drop
    //	@Override
    //	public final void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    //		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, player.getActiveItemStack());
    //		float saplingModifier = 1.0f;
    //
    //		ItemStack heldStack = player.inventory.getCurrentItem();
    //		Item heldItem = heldStack.getItem();
    //		if (heldItem instanceof IToolGrafter) {
    //			IToolGrafter grafter = (IToolGrafter) heldItem;
    //			saplingModifier = grafter.getSaplingModifier(heldStack, world, player, pos);
    //			heldStack.damageItem(1, player, p -> {});
    //			if (heldStack.isEmpty()) {
    //				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, heldStack, Hand.MAIN_HAND);
    //			}
    //		}
    //
    //		GameProfile playerProfile = player.getGameProfile();
    //		NonNullList<ItemStack> drops = NonNullList.create();
    //		getLeafDrop(drops, world, playerProfile, pos, saplingModifier, fortune);
    //		this.drops.set(drops);
    //	}
    //
    //	//TODO temp for now, should be done in loot table I think. Probably wrong
    //	@Override
    //	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    //		List<ItemStack> ret = super.getDrops(state, builder);
    //		World world = builder.getWorld();
    //		ItemStack tool = builder.get(LootParameters.TOOL);
    //		Item toolItem = tool.getItem();	//TODO null
    //		if(toolItem instanceof IToolGrafter) {
    //			IToolGrafter grafter = (IToolGrafter) toolItem;
    //		}
    //		List<ItemStack> ret = this.drops.get();
    //		this.drops.remove();
    //		if (ret != null) {
    //			drops.addAll(ret);
    //		} else {
    //			if (!(world instanceof World)) {
    //				return;
    //			}
    //			// leaves not harvested, get drops normally
    //			getLeafDrop(drops, (World) world, null, pos, 1.0f, fortune);
    //		}
    //	}
    protected abstract void getLeafDrop(
            NonNullList<ItemStack> drops,
            World world,
            @Nullable GameProfile playerProfile,
            BlockPos pos,
            float saplingModifier,
            int fortune
    );
}
