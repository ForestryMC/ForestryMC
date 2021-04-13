package forestry.core.data;

import java.util.Collection;
import java.util.Map;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.data.GlobalLootModifierProvider;

import forestry.core.config.Constants;
import forestry.core.loot.ConditionLootModifier;

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
	}
}
