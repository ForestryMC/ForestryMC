package forestry.arboriculture.loot;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.ForgeEventFactory;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeRoot;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IFruitBearer;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.core.config.Constants;

import genetics.api.individual.IGenome;

public class GrafterLootModifier extends LootModifier {
	public static final Serializer SERIALIZER = new Serializer();

	public GrafterLootModifier(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	//TODO: Clean this up and move into interface?
	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if (state == null || !state.is(BlockTags.LEAVES)) {
			return generatedLoot;
		}
		ItemStack harvestingTool = context.getParamOrNull(LootContextParams.TOOL);
		if (harvestingTool == null || harvestingTool.isEmpty()) {
			return generatedLoot;
		}
		Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
		if (!(entity instanceof Player)) {
			return generatedLoot;
		}
		Player player = (Player) entity;
		if (generatedLoot.stream().noneMatch((stack) -> stack.getItem().is(ItemTags.SAPLINGS))) {
			handleLoot(generatedLoot, player, harvestingTool, state, context);
		}
		harvestingTool.hurt(1, context.getRandom(), (ServerPlayer) player);
		if (harvestingTool.isEmpty()) {
			ForgeEventFactory.onPlayerDestroyItem(player, harvestingTool, InteractionHand.MAIN_HAND);
		}
		return generatedLoot;
	}

	public void handleLoot(List<ItemStack> generatedLoot, Player player, ItemStack harvestingTool, BlockState state, LootContext context) {
		Level world = player.level;
		BlockEntity tileEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		ITree tree = getTree(state, tileEntity);
		if (tree == null) {
			return;
		}
		Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
		if (origin == null) {
			return;
		}
		BlockPos pos = new BlockPos(origin);
		Item item = harvestingTool.getItem();
		float saplingModifier = 1.0f;
		if (item instanceof IToolGrafter) {
			saplingModifier = ((IToolGrafter) item).getSaplingModifier(harvestingTool, world, player, pos);
		}
		List<ITree> saplings = tree.getSaplings(world, player.getGameProfile(), pos, saplingModifier);
		for (ITree sapling : saplings) {
			if (sapling != null) {
				generatedLoot.add(TreeManager.treeRoot.getTypes().createStack(sapling, EnumGermlingType.SAPLING));
			}
		}
		if (tileEntity instanceof IFruitBearer) {
			IFruitBearer bearer = (IFruitBearer) tileEntity;
			generatedLoot.addAll(bearer.pickFruit(harvestingTool));
		}
		if (state.getBlock() instanceof BlockDefaultLeavesFruit) {
			IGenome genome = tree.getGenome();
			IFruitProvider fruitProvider = genome.getActiveAllele(TreeChromosomes.FRUITS).getProvider();
			if (fruitProvider.isFruitLeaf(genome, world, pos)) {
				generatedLoot.addAll(tree.produceStacks(world, pos, Integer.MAX_VALUE));
			}
		}
	}

	@Nullable
	private ITree getTree(BlockState state, @Nullable BlockEntity entity) {
		ITreeRoot root = TreeHelper.getRoot();
		ITree tree = root.translateMember(state).orElse(null);
		if (tree != null || entity == null) {
			return tree;
		}
		return TreeHelper.getRoot().getTree(entity);
	}

	private static class Serializer extends GlobalLootModifierSerializer<GrafterLootModifier> {

		public Serializer() {
			setRegistryName(Constants.MOD_ID, "grafter_modifier");
		}

		@Override
		public GrafterLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
			return new GrafterLootModifier(conditions);
		}

		@Override
		public JsonObject write(GrafterLootModifier instance) {
			return makeConditions(instance.conditions);
		}
	}
}
