package forestry.arboriculture.loot;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.ForgeEventFactory;

import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IToolGrafter;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.TreeChromosomes;
import forestry.api.genetics.IFruitBearer;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.genetics.TreeHelper;
import forestry.core.config.Constants;

import genetics.api.individual.IGenome;
import genetics.api.root.components.ComponentKeys;
import genetics.api.root.translator.IIndividualTranslator;

public class GrafterLootModifier extends LootModifier {
	public static final Serializer SERIALIZER = new Serializer();

	public GrafterLootModifier(ILootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	//TODO: Clean this up and move into interface?
	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
		if (state == null || !state.is(BlockTags.LEAVES)) {
			return generatedLoot;
		}
		ItemStack harvestingTool = context.getParamOrNull(LootParameters.TOOL);
		if (harvestingTool == null || harvestingTool.isEmpty()) {
			return generatedLoot;
		}
		Entity entity = context.getParamOrNull(LootParameters.THIS_ENTITY);
		if (!(entity instanceof PlayerEntity)) {
			return generatedLoot;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if (generatedLoot.stream().noneMatch((stack) -> stack.getItem().is(ItemTags.SAPLINGS))) {
			handleLoot(generatedLoot, player, harvestingTool, state, context);
		}
		harvestingTool.hurt(1, context.getRandom(), (ServerPlayerEntity) player);
		if (harvestingTool.isEmpty()) {
			ForgeEventFactory.onPlayerDestroyItem(player, harvestingTool, Hand.MAIN_HAND);
		}
		return generatedLoot;
	}

	public void handleLoot(List<ItemStack> generatedLoot, PlayerEntity player, ItemStack harvestingTool, BlockState state, LootContext context) {
		World world = player.level;
		TileEntity tileEntity = context.getParamOrNull(LootParameters.BLOCK_ENTITY);
		ITree tree = getTree(state, tileEntity);
		if (tree == null) {
			return;
		}
		Vector3d origin = context.getParamOrNull(LootParameters.ORIGIN);
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
	private ITree getTree(BlockState state, @Nullable TileEntity entity) {
		IIndividualTranslator<ITree> treeTranslator = TreeHelper.getRoot().getComponent(ComponentKeys.TRANSLATORS);
		ITree tree = treeTranslator.getTranslator(state.getBlock()).map((value) -> value.getIndividualFromObject(state)).orElse(null);
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
		public GrafterLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
			return new GrafterLootModifier(conditions);
		}

		@Override
		public JsonObject write(GrafterLootModifier instance) {
			return makeConditions(instance.conditions);
		}
	}
}
