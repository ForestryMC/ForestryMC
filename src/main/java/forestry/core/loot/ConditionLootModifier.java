package forestry.core.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.core.config.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * A global loot modifier used by forestry to inject the additional chest loot to the vanilla loot tables.
 */
public class ConditionLootModifier extends LootModifier {

	public static final Codec<ConditionLootModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
			ResourceLocation.CODEC.fieldOf("table").forGetter(lm -> lm.tableLocation),
			Codec.list(Codec.STRING).fieldOf("extensions").forGetter(o -> o.extensions)
	).apply(instance, ConditionLootModifier::new));

	private final ResourceLocation tableLocation;
	private final List<String> extensions;

	public ConditionLootModifier(ResourceLocation location, List<String> extensions) {
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

	private ConditionLootModifier(LootItemCondition[] conditions, ResourceLocation location, List<String> extensions) {
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

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
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

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
