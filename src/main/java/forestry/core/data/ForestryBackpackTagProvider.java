package forestry.core.data;

import javax.annotation.Nullable;

import forestry.apiculture.features.ApicultureBlocks;
import forestry.core.config.Constants;
import forestry.core.features.CoreItems;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

public class ForestryBackpackTagProvider extends ItemTagsProvider {

	public ForestryBackpackTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, blockTagsProvider, Constants.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(ForestryTags.Items.MINER_ALLOW).addTags(
				Tags.Items.OBSIDIAN,
				Tags.Items.ORES,
				Tags.Items.DUSTS,
				Tags.Items.GEMS,
				Tags.Items.INGOTS,
				Tags.Items.NUGGETS,
				ItemTags.COALS
		);
		tag(ForestryTags.Items.MINER_ALLOW).add(
				CoreItems.BRONZE_PICKAXE.item(),
				CoreItems.KIT_PICKAXE.item(),
				CoreItems.BROKEN_BRONZE_PICKAXE.item()
		);
		tag(ForestryTags.Items.MINER_REJECT);

		copy(Tags.Blocks.DIRT, ForestryTags.Items.DIGGER_ALLOW);
		tag(ForestryTags.Items.DIGGER_ALLOW).addTags(
				Tags.Items.COBBLESTONE,
				Tags.Items.GRAVEL,
				Tags.Items.NETHERRACK,
				Tags.Items.STONE,
				Tags.Items.SANDSTONE,
				Tags.Items.SAND
		);
		tag(ForestryTags.Items.DIGGER_ALLOW).add(
				Items.FLINT,
				Items.CLAY_BALL,
				Items.SNOWBALL,
				Items.SOUL_SAND,
				Items.CLAY,
				Items.SNOW,
				CoreItems.BRONZE_PICKAXE.item(),
				CoreItems.KIT_PICKAXE.item(),
				CoreItems.BROKEN_BRONZE_PICKAXE.item()
		);
		tag(ForestryTags.Items.DIGGER_REJECT);

		tag(ForestryTags.Items.FORESTER_ALLOW).addTags(
				ItemTags.LOGS,
				ItemTags.SAPLINGS,
				Tags.Items.CROPS,
				Tags.Items.SEEDS,
				ItemTags.SAPLINGS
		);
		tag(ForestryTags.Items.FORESTER_ALLOW).add(
				Items.STICK,
				Items.VINE,
				Items.SUGAR_CANE,
				Items.CACTUS,
				Items.RED_MUSHROOM,
				Items.BROWN_MUSHROOM,
				Items.POPPY, //TODO tag
				Items.GRASS, //TODO tag
				Items.SUNFLOWER, //TODO tag
				Items.PUMPKIN,
				Items.MELON,
				Items.GOLDEN_APPLE,
				Items.NETHER_WART,
				Items.WHEAT_SEEDS,
				Items.PUMPKIN_SEEDS,
				Items.MELON_SEEDS,
				Items.BEETROOT_SEEDS,
				Items.BEETROOT,
				Items.CHORUS_FRUIT,
				Items.CHORUS_PLANT,
				Items.APPLE
		);
		tag(ForestryTags.Items.FORESTER_REJECT);

		tag(ForestryTags.Items.HUNTER_ALLOW).addTags(
				Tags.Items.BONES,
				Tags.Items.EGGS,
				Tags.Items.ENDER_PEARLS,
				Tags.Items.FEATHERS,
				ItemTags.FISHES,
				Tags.Items.GUNPOWDER,
				Tags.Items.LEATHER,
				Tags.Items.SLIMEBALLS,
				Tags.Items.STRING
		);
		tag(ForestryTags.Items.HUNTER_ALLOW).add(
				Items.BLAZE_POWDER,
				Items.BLAZE_ROD,
				Items.ROTTEN_FLESH,
				Items.SKELETON_SKULL,
				Items.GHAST_TEAR,
				Items.GOLD_NUGGET,
				Items.ARROW,
				Items.SPECTRAL_ARROW,
				Items.TIPPED_ARROW,
				Items.PORKCHOP,
				Items.COOKED_PORKCHOP,
				Items.BEEF,
				Items.COOKED_BEEF,
				Items.CHICKEN,
				Items.COOKED_CHICKEN,
				Items.MUTTON,
				Items.COOKED_MUTTON,
				Items.RABBIT,
				Items.COOKED_RABBIT,
				Items.RABBIT_FOOT,
				Items.RABBIT_HIDE,
				Items.SPIDER_EYE,
				Items.FERMENTED_SPIDER_EYE,
				Items.BONE_MEAL, // TODO correct item?
				Items.HAY_BLOCK,
				Items.WHITE_WOOL, // TODO tag
				Items.ENDER_EYE,
				Items.MAGMA_CREAM,
				Items.GLISTERING_MELON_SLICE, // TODO right item?
				Items.COD, // TODO tag
				Items.COOKED_COD, // TODO tag
				Items.LEAD,
				Items.FISHING_ROD,
				Items.NAME_TAG,
				Items.SADDLE,
				Items.DIAMOND_HORSE_ARMOR,
				Items.GOLDEN_HORSE_ARMOR,
				Items.IRON_HORSE_ARMOR
		);
		tag(ForestryTags.Items.HUNTER_REJECT);

		copy(BlockTags.FENCE_GATES, ForestryTags.Items.BUILDER_ALLOW);
		tag(ForestryTags.Items.BUILDER_ALLOW).addTags(
				// TODO:
				//  "block[A-Z].*",
				//  "stainedClay[A-Z].*"
				Tags.Items.GLASS_PANES,
				ItemTags.WOODEN_SLABS,
				Tags.Items.STAINED_GLASS,
				Tags.Items.STONE,
				Tags.Items.SANDSTONE,
				ItemTags.PLANKS,
				ItemTags.WOODEN_STAIRS,
				ItemTags.WOODEN_SLABS,
				ItemTags.WOODEN_FENCES,
				ItemTags.WOODEN_TRAPDOORS,
				Tags.Items.GLASS,
				Tags.Items.CHESTS,
				ItemTags.WOODEN_DOORS
		);
		tag(ForestryTags.Items.BUILDER_ALLOW).add(
				Items.TORCH,
				Items.CRAFTING_TABLE,
				Items.REDSTONE_TORCH,
				Items.REDSTONE_LAMP,
				Items.SEA_LANTERN,
				Items.END_ROD,
				Items.STONE_BRICKS,
				Items.BRICKS,
				Items.CLAY,
				Items.TERRACOTTA,
				Items.WHITE_TERRACOTTA,
				Items.WHITE_GLAZED_TERRACOTTA,
				Items.PACKED_ICE,
				Items.NETHER_BRICKS,
				Items.NETHER_BRICK_FENCE,
				Items.CRAFTING_TABLE,
				Items.FURNACE,
				Items.LEVER,
				Items.DISPENSER,
				Items.DROPPER,
				Items.LADDER,
				Items.IRON_BARS,
				Items.QUARTZ_BLOCK,
				Items.QUARTZ_STAIRS,
				Items.SANDSTONE_STAIRS,
				Items.RED_SANDSTONE_STAIRS,
				Items.COBBLESTONE_WALL,
				Items.STONE_BUTTON,
				Items.OAK_BUTTON,
				Items.STONE_SLAB,
				Items.SANDSTONE_SLAB,
				Items.OAK_SLAB,
				Items.PURPUR_BLOCK,
				Items.PURPUR_PILLAR,
				Items.PURPUR_STAIRS,
				Items.PURPUR_SLAB,
				Items.END_STONE_BRICKS,
				Items.WHITE_CARPET,
				Items.IRON_TRAPDOOR,
				Items.STONE_PRESSURE_PLATE,
				Items.OAK_PRESSURE_PLATE,
				Items.LIGHT_WEIGHTED_PRESSURE_PLATE,
				Items.HEAVY_WEIGHTED_PRESSURE_PLATE,
				Items.OAK_SIGN,
				Items.ITEM_FRAME,
				Items.ACACIA_DOOR,
				Items.BIRCH_DOOR,
				Items.DARK_OAK_DOOR,
				Items.IRON_DOOR,
				Items.JUNGLE_DOOR,
				Items.OAK_DOOR,
				Items.SPRUCE_DOOR,
				ApicultureBlocks.CANDLE.item(), //TODO tag
				ApicultureBlocks.STUMP.item()
		);
		tag(ForestryTags.Items.BUILDER_REJECT);

		tag(ForestryTags.Items.ADVENTURER_ALLOW);
		tag(ForestryTags.Items.ADVENTURER_REJECT);

		super.addTags();
	}
}
