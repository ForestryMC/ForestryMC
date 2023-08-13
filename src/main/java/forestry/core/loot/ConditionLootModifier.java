package forestry.core.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;

import forestry.core.config.Constants;

/**
 * A global loot modifier used by forestry to inject the additional chest loot to the vanilla loot tables.
 */
public class ConditionLootModifier extends LootModifier {
	public static final GlobalLootModifierSerializer<ConditionLootModifier> SERIALIZER = new Serializer();

	private final ResourceLocation tableLocation;
	private final String[] extensions;

	public ConditionLootModifier(ResourceLocation location, String... extensions) {
		super(new LootItemCondition[]{
				LootTableIdCondition.builder(location).build()
		});
		this.tableLocation = location;
		this.extensions = extensions;
	}

	private static LootItemCondition[] merge(LootItemCondition[] conditions, LootItemCondition condition) {
		LootItemCondition[] newArray = Arrays.copyOf(conditions, conditions.length + 1);
		newArray[conditions.length] = condition;
		return newArray;
	}

	private ConditionLootModifier(LootItemCondition[] conditions, ResourceLocation location, String... extensions) {
		super(merge(conditions, LootTableIdCondition.builder(location).build()));
		this.tableLocation = location;
		this.extensions = extensions;
	}

	/**
	 * Helper field to prevent an endless method loop caused by forge in {@link LootTable#getRandomItems(LootContext)}
	 * which calls this method again, since it keeps the {@link LootContext#getQueriedLootTableId()} value, which causes
	 * "getRandomItems" to calling this method again, because the conditions still met even that it is an other loot
	 * table.
	 */
	private boolean operates = false;

	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (operates) {
			return generatedLoot;
		}
		operates = true;
		for (String extension : extensions) {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, tableLocation.getPath() + "/" + extension);
			LootTable table = context.getLootTable(location);
			if (table != LootTable.EMPTY) {
				generatedLoot.addAll(table.getRandomItems(context));
			}
		}
		operates = false;
		return generatedLoot;
	}

	private static class Serializer extends GlobalLootModifierSerializer<ConditionLootModifier> {

		public Serializer() {
			setRegistryName(Constants.MOD_ID, "condition_modifier");
		}

		@Override
		public ConditionLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
			String table = GsonHelper.getAsString(object, "table");
			JsonArray array = GsonHelper.getAsJsonArray(object, "extensions");
			String[] extensions = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				extensions[i] = array.get(i).getAsString();
			}
			//We don't add the conditions back to the json at #write, so the conditions fields ends up with an null value
			if (conditions == null) {
				return new ConditionLootModifier(new ResourceLocation(table), extensions);
			}
			return new ConditionLootModifier(conditions, new ResourceLocation(table), extensions);
		}

		@Override
		public JsonObject write(ConditionLootModifier instance) {
			JsonObject obj = new JsonObject();
			obj.addProperty("table", instance.tableLocation.toString());
			JsonArray extensions = new JsonArray();
			for (String value : instance.extensions) {
				extensions.add(value);
			}
			obj.add("extensions", extensions);
			return obj;
		}
	}
}
