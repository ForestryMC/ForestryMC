package forestry.core.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;

import forestry.core.config.Constants;

public class ConditionLootModifier extends LootModifier {
	public static final Serializer SERIALIZER = new Serializer();

	private final ResourceLocation tableLocation;
	private final String[] extensions;

	public ConditionLootModifier(ResourceLocation location, String... extensions) {
		super(new ILootCondition[]{
				LootTableIdCondition.builder(location).build()
		});
		this.tableLocation = location;
		this.extensions = extensions;
	}

	private static ILootCondition[] merge(ILootCondition[] conditions, ILootCondition condition) {
		ILootCondition[] newArray = Arrays.copyOf(conditions, conditions.length + 1);
		newArray[conditions.length] = condition;
		return newArray;
	}

	private ConditionLootModifier(ILootCondition[] conditions, ResourceLocation location, String... extensions) {
		super(merge(conditions, LootTableIdCondition.builder(location).build()));
		this.tableLocation = location;
		this.extensions = extensions;
	}

	/**
	 * Helper field to prevent an endless method loop caused by forge.
	 */
	private boolean operates = false;

	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		if (operates) {
			return generatedLoot;
		}
		operates = true;
		//context.getLootTable().getRandomItems(context)
		for (String extension : extensions) {
			ResourceLocation location = new ResourceLocation(Constants.MOD_ID, tableLocation.getPath() + "/" + extension);
			LootTable table = context.getLootTable(location);
			if (table != null) {
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
		public ConditionLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
			String table = JSONUtils.getAsString(object, "table");
			JsonArray array = JSONUtils.getAsJsonArray(object, "extensions");
			String[] extensions = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				extensions[i] = array.get(i).getAsString();
			}
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
