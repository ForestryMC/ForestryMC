package forestry.core.data;

import java.util.function.BiConsumer;

import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.item.Item;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.StandaloneLootEntry;
import net.minecraft.util.ResourceLocation;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.core.loot.OrganismFunction;

public class ForestryChestLootTables extends ChestLootTables {

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), LootTable.lootTable());
		for (LootTableHelper.Entry entry : LootTableHelper.getInstance().entries.values()) {
			consumer.accept(entry.getLocation(), entry.builder);
		}
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/abandoned_mineshaft/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/abandoned_mineshaft/factory"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_factory_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_PICKAXE.item()).setWeight(30))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_SHOVEL.item()).setWeight(10))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_PICKAXE.item()).setWeight(10))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_SHOVEL.item()).setWeight(5))
		//						.add(EmptyLootEntry.emptyItem().setWeight(50))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/desert_pyramid/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.STEADFAST).setWeight(3)
		//								.apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(6))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/desert_pyramid/arboriculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(saplingLoot(TreeDefinition.Acacia))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/desert_pyramid/factory"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_factory_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.ASH.item())
		//								.apply(SetCount.setCount(RandomValueRange.between(7, 12))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(1))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/end_city_treasure/apiculture"),
		//				LootTable.lootTable()/*.withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.A.item())
		//								.apply(SetCount.setCount(RandomValueRange.between(2, 5))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				)*/.withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees").setRolls(ConstantRange.exactly(2))
		//				.add(beeLoot(BeeDefinition.STEADFAST)
		//						.apply(SetCount.setCount(RandomValueRange.between(1, 2)))
		//						.setWeight(20)
		//				)
		//								.add(beeLoot(BeeDefinition.ENDED)
		//										.apply(SetCount.setCount(RandomValueRange.between(1, 3)))
		//										.setWeight(20)
		//								)
		//						.add(EmptyLootEntry.emptyItem().setWeight(60))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/end_city_treasure/arboriculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
		//						.add(EmptyLootEntry.emptyItem().setWeight(1))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/igloo_chest/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.WINTRY).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(2))
		//						.add(EmptyLootEntry.emptyItem().setWeight(7))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/jungle_temple/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.TROPICAL).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(3))
		//						.add(EmptyLootEntry.emptyItem().setWeight(6))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/jungle_temple/arboriculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(saplingLoot(TreeDefinition.Sipiri))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/nether_bridge/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.SINISTER))
		//						.add(EmptyLootEntry.emptyItem().setWeight(8))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/simple_dungeon/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/spawn_bonus_chest/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.SCOOP.item()))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/stronghold_corridor/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/stronghold_crossing/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/stronghold_library/apiculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.MONASTIC).setWeight(6))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist/arboriculture"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(3))
		//						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
		//						.add(saplingLoot(TreeDefinition.Lime).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Cherry).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Larch).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Teak))
		//						.add(saplingLoot(TreeDefinition.Padauk))
		//				))
		//		consumer.accept(LootTables.ABANDONED_MINESHAFT,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_factory_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_PICKAXE.item()).setWeight(30))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_SHOVEL.item()).setWeight(10))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_PICKAXE.item()).setWeight(10))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_SHOVEL.item()).setWeight(5))
		//						.add(EmptyLootEntry.emptyItem().setWeight(50))
		//				));
		//		consumer.accept(LootTables.DESERT_PYRAMID,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.STEADFAST).setWeight(3)
		//								.apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(6))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(saplingLoot(TreeDefinition.Acacia))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_factory_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(CoreItems.ASH.item())
		//								.apply(SetCount.setCount(RandomValueRange.between(7, 12))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(1))
		//				));
		//		consumer.accept(LootTables.END_CITY_TREASURE,
		//				LootTable.lootTable()/*.withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.A.item())
		//								.apply(SetCount.setCount(RandomValueRange.between(2, 5))))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				)*/.withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees").setRolls(ConstantRange.exactly(2))
		//						.add(beeLoot(BeeDefinition.STEADFAST)
		//								.apply(SetCount.setCount(RandomValueRange.between(1, 2)))
		//								.setWeight(20)
		//						)
		//						.add(beeLoot(BeeDefinition.ENDED)
		//								.apply(SetCount.setCount(RandomValueRange.between(1, 3)))
		//								.setWeight(20)
		//						)
		//						.add(EmptyLootEntry.emptyItem().setWeight(60))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
		//						.add(EmptyLootEntry.emptyItem().setWeight(1))
		//				));
		//		consumer.accept(LootTables.IGLOO_CHEST,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.WINTRY).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(2))
		//						.add(EmptyLootEntry.emptyItem().setWeight(7))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.TROPICAL).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(3))
		//						.add(EmptyLootEntry.emptyItem().setWeight(6))
		//				));
		//		consumer.accept(LootTables.JUNGLE_TEMPLE,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(saplingLoot(TreeDefinition.Sipiri))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(LootTables.NETHER_BRIDGE,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.SINISTER))
		//						.add(EmptyLootEntry.emptyItem().setWeight(8))
		//				));
		//		consumer.accept(LootTables.SIMPLE_DUNGEON,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(LootTables.SPAWN_BONUS_CHEST,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.SCOOP.item()))
		//				));
		//		consumer.accept(LootTables.STRONGHOLD_CORRIDOR,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(LootTables.STRONGHOLD_CROSSING,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(EmptyLootEntry.emptyItem().setWeight(9))
		//				));
		//		consumer.accept(LootTables.STRONGHOLD_LIBRARY,
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(1))
		//						.add(beeLoot(BeeDefinition.STEADFAST))
		//						.add(beeLoot(BeeDefinition.MONASTIC).setWeight(6))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				));
		//		consumer.accept(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"),
		//				LootTable.lootTable().withPool(LootPool.lootPool()
		//						.name("forestry_arboriculture_items")
		//						.setRolls(ConstantRange.exactly(3))
		//						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
		//						.add(saplingLoot(TreeDefinition.Lime).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Cherry).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Larch).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
		//						.add(saplingLoot(TreeDefinition.Teak))
		//						.add(saplingLoot(TreeDefinition.Padauk))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_items")
		//						.setRolls(ConstantRange.exactly(4))
		//						.add(ItemLootEntry.lootTableItem(ApicultureBlocks.CANDLE.block()).apply(SetCount.setCount(RandomValueRange.between(7, 12))).setWeight(10))
		//						.add(TagLootEntry.expandTag(ForestryTags.Items.BEE_COMBS).apply(SetCount.setCount(RandomValueRange.between(1, 4))))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.SCOOP.item()).setWeight(5))
		//						.add(ItemLootEntry.lootTableItem(ApicultureItems.SMOKER))
		//				).withPool(LootPool.lootPool()
		//						.name("forestry_apiculture_bees")
		//						.setRolls(ConstantRange.exactly(3))
		//						.add(beeLoot(BeeDefinition.COMMON).setWeight(6))
		//						.add(beeLoot(BeeDefinition.MEADOWS).setWeight(6))
		//						.add(EmptyLootEntry.emptyItem().setWeight(3))
		//				)
		//		);*/
	}

	private StandaloneLootEntry.Builder<?> saplingLoot(TreeDefinition definition) {
		return saplingLoot(EnumGermlingType.SAPLING, definition);
	}

	private StandaloneLootEntry.Builder<?> saplingLoot(EnumGermlingType type, TreeDefinition definition) {
		return ItemLootEntry.lootTableItem(saplingItem(type))
				.apply(OrganismFunction.fromDefinition(definition));
	}

	private StandaloneLootEntry.Builder<?> beeLoot(BeeDefinition definition) {
		return beeLoot(EnumBeeType.DRONE, definition);
	}

	private StandaloneLootEntry.Builder<?> beeLoot(EnumBeeType type, BeeDefinition definition) {
		return ItemLootEntry.lootTableItem(beeItem(type))
				.apply(OrganismFunction.fromDefinition(definition));
	}

	private Item saplingItem(EnumGermlingType type) {
		switch (type) {
			case POLLEN:
				return ArboricultureItems.POLLEN_FERTILE.item();
			default:
			case SAPLING:
				return ArboricultureItems.SAPLING.item();
		}
	}

	private Item beeItem(EnumBeeType type) {
		switch (type) {
			case QUEEN:
				return ApicultureItems.BEE_QUEEN.item();
			case LARVAE:
				return ApicultureItems.BEE_LARVAE.item();
			case PRINCESS:
				return ApicultureItems.BEE_PRINCESS.item();
			default:
			case DRONE:
				return ApicultureItems.BEE_DRONE.item();
		}
	}

}
