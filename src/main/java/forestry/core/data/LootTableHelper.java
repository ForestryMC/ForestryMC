package forestry.core.data;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.loot.OrganismFunction;
import forestry.storage.features.BackpackItems;

/**
 * Helper class to handle chest loot.
 * <p>
 * <p>
 * Used by {@link ForestryChestLootTables} and {@link ForestryLootModifierProvider}
 */
public class LootTableHelper {
	@Nullable
	public static LootTableHelper instance;

	public static LootTableHelper getInstance() {
		if (instance == null) {
			instance = new LootTableHelper();
		}
		return instance;
	}

	protected final Multimap<ResourceLocation, Entry> entries = LinkedHashMultimap.create();

	public LootTableHelper() {
		add(BuiltInLootTables.ABANDONED_MINESHAFT, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootItem.emptyItem().setWeight(9))
				));
		add(BuiltInLootTables.ABANDONED_MINESHAFT, "factory",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_factory_items")
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(CoreItems.BROKEN_BRONZE_PICKAXE.item()).setWeight(30))
						.add(LootItem.lootTableItem(CoreItems.BROKEN_BRONZE_SHOVEL.item()).setWeight(10))
						.add(LootItem.lootTableItem(CoreItems.KIT_PICKAXE.item()).setWeight(10))
						.add(LootItem.lootTableItem(CoreItems.KIT_SHOVEL.item()).setWeight(5))
						.add(EmptyLootItem.emptyItem().setWeight(50))
				));
		add(BuiltInLootTables.ABANDONED_MINESHAFT, "storage",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_storage_items")
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(BackpackItems.MINER_BACKPACK.item()))
						.add(EmptyLootItem.emptyItem().setWeight(20))));
		add(BuiltInLootTables.DESERT_PYRAMID, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.STEADFAST).setWeight(3)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))))
						.add(EmptyLootItem.emptyItem().setWeight(6))
				));
		add(BuiltInLootTables.DESERT_PYRAMID, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantValue.exactly(1))
						.add(saplingLoot(TreeDefinition.Acacia))
						.add(EmptyLootItem.emptyItem().setWeight(3))
				));
		add(BuiltInLootTables.DESERT_PYRAMID, "factory",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_factory_items")
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(CoreItems.ASH.item())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(7, 12))))
						.add(EmptyLootItem.emptyItem().setWeight(1))
				));
		add(BuiltInLootTables.END_CITY_TREASURE, "apiculture",
				LootTable.lootTable()/*.withPool(LootPool.lootPool()
						.name("forestry_apiculture_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ApicultureItems.A.item())
								.apply(SetCount.setCount(RandomValueRange.between(2, 5))))
						.add(EmptyLootEntry.emptyItem().setWeight(3))
				)*/.withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees").setRolls(ConstantValue.exactly(2))
						.add(beeLoot(BeeDefinition.STEADFAST)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
								.setWeight(20)
						)
						.add(beeLoot(BeeDefinition.ENDED)
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
								.setWeight(20)
						)
						.add(EmptyLootItem.emptyItem().setWeight(60))
				));
		add(BuiltInLootTables.END_CITY_TREASURE, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ArboricultureItems.GRAFTER.item()))
						.add(EmptyLootItem.emptyItem().setWeight(1))
				));
		add(BuiltInLootTables.IGLOO_CHEST, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.WINTRY).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(2))
						.add(EmptyLootItem.emptyItem().setWeight(7))
				));
		add(BuiltInLootTables.JUNGLE_TEMPLE, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.TROPICAL).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(3))
						.add(EmptyLootItem.emptyItem().setWeight(6))
				));
		add(BuiltInLootTables.JUNGLE_TEMPLE, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantValue.exactly(1))
						.add(saplingLoot(TreeDefinition.Sipiri))
						.add(EmptyLootItem.emptyItem().setWeight(9))
				));
		add(BuiltInLootTables.NETHER_BRIDGE, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.SINISTER))
						.add(EmptyLootItem.emptyItem().setWeight(8))
				));
		add(BuiltInLootTables.SIMPLE_DUNGEON, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootItem.emptyItem().setWeight(9))
				));
		add(BuiltInLootTables.SPAWN_BONUS_CHEST, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(ApicultureItems.SCOOP.item()))
				));
		add(BuiltInLootTables.STRONGHOLD_CORRIDOR, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootItem.emptyItem().setWeight(9))
				));
		add(BuiltInLootTables.STRONGHOLD_CROSSING, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootItem.emptyItem().setWeight(9))
				));
		add(BuiltInLootTables.STRONGHOLD_LIBRARY, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.MONASTIC).setWeight(6))
						.add(EmptyLootItem.emptyItem().setWeight(3))
				));
		add(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantValue.exactly(3))
						.add(LootItem.lootTableItem(ArboricultureItems.GRAFTER.item()))
						.add(saplingLoot(TreeDefinition.Lime).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Cherry).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Larch).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Teak))
						.add(saplingLoot(TreeDefinition.Padauk))
				));
		add(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_items")
						.setRolls(ConstantValue.exactly(4))
						.add(TagEntry.expandTag(ForestryTags.Items.BEE_COMBS).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
						.add(LootItem.lootTableItem(ApicultureItems.SCOOP.item()).setWeight(5))
						.add(LootItem.lootTableItem(ApicultureItems.SMOKER))
				).withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantValue.exactly(3))
						.add(beeLoot(BeeDefinition.COMMON).setWeight(6))
						.add(beeLoot(BeeDefinition.MEADOWS).setWeight(6))
						.add(EmptyLootItem.emptyItem().setWeight(3))
				)
		);
	}

	private LootPoolSingletonContainer.Builder<?> saplingLoot(TreeDefinition definition) {
		return saplingLoot(EnumGermlingType.SAPLING, definition);
	}

	private LootPoolSingletonContainer.Builder<?> saplingLoot(EnumGermlingType type, TreeDefinition definition) {
		return LootItem.lootTableItem(saplingItem(type))
				.apply(OrganismFunction.fromDefinition(definition));
	}

	private LootPoolSingletonContainer.Builder<?> beeLoot(BeeDefinition definition) {
		return beeLoot(EnumBeeType.DRONE, definition);
	}

	private LootPoolSingletonContainer.Builder<?> beeLoot(EnumBeeType type, BeeDefinition definition) {
		return LootItem.lootTableItem(beeItem(type))
				.apply(OrganismFunction.fromDefinition(definition));
	}

	private Item saplingItem(EnumGermlingType type) {
		return switch (type) {
			case POLLEN -> ArboricultureItems.POLLEN_FERTILE.item();
			case SAPLING -> ArboricultureItems.SAPLING.item();
		};
	}

	private Item beeItem(EnumBeeType type) {
		return switch (type) {
			case QUEEN -> ApicultureItems.BEE_QUEEN.item();
			case LARVAE -> ApicultureItems.BEE_LARVAE.item();
			case PRINCESS -> ApicultureItems.BEE_PRINCESS.item();
			case DRONE -> ApicultureItems.BEE_DRONE.item();
		};
	}

	protected void add(ResourceLocation location, String extension, LootTable.Builder builder) {
		entries.put(location, new Entry(location, extension, builder));
	}

	public static class Entry {
		public final ResourceLocation defaultLocation;
		public final String extension;
		public final LootTable.Builder builder;


		public Entry(ResourceLocation defaultLocation, String extension, LootTable.Builder builder) {
			this.defaultLocation = defaultLocation;
			this.extension = extension;
			this.builder = builder;
		}

		public ResourceLocation getLocation() {
			return new ResourceLocation(Constants.MOD_ID, defaultLocation.getPath() + "/" + extension);
		}
	}
}
