package forestry.arboriculture.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

import forestry.arboriculture.blocks.BlockAsh;

public class CountBlockFunction extends LootFunction {
	public static LootFunctionType type;

	protected CountBlockFunction(ILootCondition[] conditions) {
		super(conditions);
	}

	public static LootFunction.Builder<?> builder() {
		return simpleBuilder(CountBlockFunction::new);
	}

	@Override
	public LootFunctionType getType() {
		return type;
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockState state = context.getParamOrNull(LootParameters.BLOCK_STATE);
		if (state == null || !state.hasProperty(BlockAsh.AMOUNT)) {
			return stack;
		}
		int amount = state.getValue(BlockAsh.AMOUNT);
		stack.setCount(amount);
		return stack;
	}

	@Override
	public Set<LootParameter<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootParameters.BLOCK_STATE);
	}

	public static class Serializer extends LootFunction.Serializer<CountBlockFunction> {

		@Override
		public void serialize(JsonObject object, CountBlockFunction function, JsonSerializationContext context) {
			super.serialize(object, function, context);
		}

		@Override
		public CountBlockFunction deserialize(JsonObject object, JsonDeserializationContext jsonDeserializationContext, ILootCondition[] conditions) {
			return new CountBlockFunction(conditions);
		}
	}
}
