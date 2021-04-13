package forestry.core.data;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.EmptyLootEntry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.RandomValueRange;
import net.minecraft.loot.StandaloneLootEntry;
import net.minecraft.loot.TagLootEntry;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;

import forestry.api.apiculture.genetics.EnumBeeType;
import forestry.api.arboriculture.genetics.EnumGermlingType;
import forestry.apiculture.features.ApicultureBlocks;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.genetics.BeeDefinition;
import forestry.arboriculture.features.ArboricultureItems;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import forestry.core.loot.OrganismFunction;
import forestry.storage.features.BackpackItems;

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
		add(LootTables.ABANDONED_MINESHAFT, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootEntry.emptyItem().setWeight(9))
				));
		add(LootTables.ABANDONED_MINESHAFT, "factory",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_factory_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_PICKAXE.item()).setWeight(30))
						.add(ItemLootEntry.lootTableItem(CoreItems.BROKEN_BRONZE_SHOVEL.item()).setWeight(10))
						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_PICKAXE.item()).setWeight(10))
						.add(ItemLootEntry.lootTableItem(CoreItems.KIT_SHOVEL.item()).setWeight(5))
						.add(EmptyLootEntry.emptyItem().setWeight(50))
				));
		add(LootTables.ABANDONED_MINESHAFT, "storage",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_storage_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(BackpackItems.MINER_BACKPACK.item()))
						.add(EmptyLootEntry.emptyItem().setWeight(20))));
		add(LootTables.DESERT_PYRAMID, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.STEADFAST).setWeight(3)
								.apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F))))
						.add(EmptyLootEntry.emptyItem().setWeight(6))
				));
		add(LootTables.DESERT_PYRAMID, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantRange.exactly(1))
						.add(saplingLoot(TreeDefinition.Acacia))
						.add(EmptyLootEntry.emptyItem().setWeight(3))
				));
		add(LootTables.DESERT_PYRAMID, "factory",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_factory_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(CoreItems.ASH.item())
								.apply(SetCount.setCount(RandomValueRange.between(7, 12))))
						.add(EmptyLootEntry.emptyItem().setWeight(1))
				));
		add(LootTables.END_CITY_TREASURE, "apiculture",
				LootTable.lootTable()/*.withPool(LootPool.lootPool()
						.name("forestry_apiculture_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ApicultureItems.A.item())
								.apply(SetCount.setCount(RandomValueRange.between(2, 5))))
						.add(EmptyLootEntry.emptyItem().setWeight(3))
				)*/.withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees").setRolls(ConstantRange.exactly(2))
						.add(beeLoot(BeeDefinition.STEADFAST)
								.apply(SetCount.setCount(RandomValueRange.between(1, 2)))
								.setWeight(20)
						)
						.add(beeLoot(BeeDefinition.ENDED)
								.apply(SetCount.setCount(RandomValueRange.between(1, 3)))
								.setWeight(20)
						)
						.add(EmptyLootEntry.emptyItem().setWeight(60))
				));
		add(LootTables.END_CITY_TREASURE, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
						.add(EmptyLootEntry.emptyItem().setWeight(1))
				));
		add(LootTables.IGLOO_CHEST, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.WINTRY).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(2))
						.add(EmptyLootEntry.emptyItem().setWeight(7))
				));
		add(LootTables.JUNGLE_TEMPLE, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.TROPICAL).apply(SetCount.setCount(RandomValueRange.between(1, 3))).setWeight(3))
						.add(EmptyLootEntry.emptyItem().setWeight(6))
				));
		add(LootTables.JUNGLE_TEMPLE, "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantRange.exactly(1))
						.add(saplingLoot(TreeDefinition.Sipiri))
						.add(EmptyLootEntry.emptyItem().setWeight(9))
				));
		add(LootTables.NETHER_BRIDGE, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.SINISTER))
						.add(EmptyLootEntry.emptyItem().setWeight(8))
				));
		add(LootTables.SIMPLE_DUNGEON, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootEntry.emptyItem().setWeight(9))
				));
		add(LootTables.SPAWN_BONUS_CHEST, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(ItemLootEntry.lootTableItem(ApicultureItems.SCOOP.item()))
				));
		add(LootTables.STRONGHOLD_CORRIDOR, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootEntry.emptyItem().setWeight(9))
				));
		add(LootTables.STRONGHOLD_CROSSING, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(EmptyLootEntry.emptyItem().setWeight(9))
				));
		add(LootTables.STRONGHOLD_LIBRARY, "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(1))
						.add(beeLoot(BeeDefinition.STEADFAST))
						.add(beeLoot(BeeDefinition.MONASTIC).setWeight(6))
						.add(EmptyLootEntry.emptyItem().setWeight(3))
				));
		add(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), "arboriculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_arboriculture_items")
						.setRolls(ConstantRange.exactly(3))
						.add(ItemLootEntry.lootTableItem(ArboricultureItems.GRAFTER.item()))
						.add(saplingLoot(TreeDefinition.Lime).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Cherry).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Larch).apply(SetCount.setCount(RandomValueRange.between(1, 3))))
						.add(saplingLoot(TreeDefinition.Teak))
						.add(saplingLoot(TreeDefinition.Padauk))
				));
		add(new ResourceLocation(Constants.MOD_ID, "chests/village_naturalist"), "apiculture",
				LootTable.lootTable().withPool(LootPool.lootPool()
						.name("forestry_apiculture_items")
						.setRolls(ConstantRange.exactly(4))
						.add(ItemLootEntry.lootTableItem(ApicultureBlocks.CANDLE.block()).apply(SetCount.setCount(RandomValueRange.between(7, 12))).setWeight(10))
						.add(TagLootEntry.expandTag(ForestryTags.Items.BEE_COMBS).apply(SetCount.setCount(RandomValueRange.between(1, 4))))
						.add(ItemLootEntry.lootTableItem(ApicultureItems.SCOOP.item()).setWeight(5))
						.add(ItemLootEntry.lootTableItem(ApicultureItems.SMOKER))
				).withPool(LootPool.lootPool()
						.name("forestry_apiculture_bees")
						.setRolls(ConstantRange.exactly(3))
						.add(beeLoot(BeeDefinition.COMMON).setWeight(6))
						.add(beeLoot(BeeDefinition.MEADOWS).setWeight(6))
						.add(EmptyLootEntry.emptyItem().setWeight(3))
				)
		);
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
