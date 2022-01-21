package forestry.arboriculture.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Set;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import forestry.arboriculture.blocks.BlockAsh;

public class CountBlockFunction extends LootItemConditionalFunction {
	public static LootItemFunctionType type;

	protected CountBlockFunction(LootItemCondition[] conditions) {
		super(conditions);
	}

	public static LootItemConditionalFunction.Builder<?> builder() {
		return simpleBuilder(CountBlockFunction::new);
	}

	@Override
	public LootItemFunctionType getType() {
		return type;
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if (state == null || !state.hasProperty(BlockAsh.AMOUNT)) {
			return stack;
		}
		int amount = state.getValue(BlockAsh.AMOUNT);
		stack.setCount(amount);
		return stack;
	}

	@Override
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.BLOCK_STATE);
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<CountBlockFunction> {

		@Override
		public void serialize(JsonObject object, CountBlockFunction function, JsonSerializationContext context) {
			super.serialize(object, function, context);
		}

		@Override
		public CountBlockFunction deserialize(JsonObject object, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] conditions) {
			return new CountBlockFunction(conditions);
		}
	}
}
