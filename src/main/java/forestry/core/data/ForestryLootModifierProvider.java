package forestry.core.data;

import java.util.Collection;
import java.util.Map;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.loot.conditions.Alternative;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.data.GlobalLootModifierProvider;

import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.loot.GrafterLootModifier;
import forestry.core.config.Constants;
import forestry.core.loot.ConditionLootModifier;

/**
 * Data provider for the generation of global loot modifiers.
 * <p>
 * Currently the only modifier is the {@link ConditionLootModifier}
 */
public class ForestryLootModifierProvider extends GlobalLootModifierProvider {
	public ForestryLootModifierProvider(DataGenerator gen) {
		super(gen, Constants.MOD_ID);
	}

	@Override
	protected void start() {
		for (Map.Entry<ResourceLocation, Collection<LootTableHelper.Entry>> mapEntry : LootTableHelper.getInstance().entries.asMap().entrySet()) {
			String[] extensions = mapEntry.getValue().stream().map(entry -> entry.extension).toArray(String[]::new);
			add(mapEntry.getKey().getPath(), ConditionLootModifier.SERIALIZER, new ConditionLootModifier(mapEntry.getKey(), extensions));
		}
		add("grafter", GrafterLootModifier.SERIALIZER, new GrafterLootModifier(new ILootCondition[]{
				Alternative.alternative(
						MatchTool.toolMatches(ItemPredicate.Builder.item().of(ArboricultureItems.GRAFTER.item())),
						MatchTool.toolMatches(ItemPredicate.Builder.item().of(ArboricultureItems.GRAFTER_PROVEN.item()))
				).build()
		}));
	}
}
