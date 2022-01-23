package forestry.core.data;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import forestry.core.config.Constants;

public class ForestryTags {
	public static class Blocks {

		public static final Tag.Named<Block> MINEABLE_SCOOP = tag("scoop");
		public static final Tag.Named<Block> MINEABLE_GRAFTER = tag("grafter");

		public static final Tag.Named<Block> CHARCOAL = forgeTag("charcoal");

		public static final Tag.Named<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final Tag.Named<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final Tag.Named<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final Tag.Named<Block> ORES_TIN = forgeTag("ores/tin");
		public static final Tag.Named<Block> ORES_APATITE = forgeTag("ores/apatite");
		public static final Tag.Named<Block> PALM_LOGS = tag("palm_logs");
		public static final Tag.Named<Block> PAPAYA_LOGS = tag("papaya_logs");

		private static Tag.Named<Block> tag(String name) {
			return BlockTags.bind(Constants.MOD_ID + ":" + name);
		}

		private static Tag.Named<Block> forgeTag(String name) {
			return BlockTags.bind("forge:" + name);
		}

		private static Tag.Named<Block> vanillaTag(String name) {
			return BlockTags.bind(name);
		}

		private Blocks() {

		}
	}

	public static class Items {

		public static final Tag.Named<Item> CHARCOAL = forgeTag("charcoal");

		public static final Tag.Named<Item> BEE_COMBS = tag("combs");
		public static final Tag.Named<Item> PROPOLIS = tag("propolis");
		public static final Tag.Named<Item> DROP_HONEY = tag("drop_honey");

		public static final Tag.Named<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
		public static final Tag.Named<Item> INGOTS_COPPER = forgeTag("ingots/copper");
		public static final Tag.Named<Item> INGOTS_TIN = forgeTag("ingots/tin");

		public static final Tag.Named<Item> GEARS = forgeTag("gears");
		public static final Tag.Named<Item> GEARS_BRONZE = forgeTag("gears/bronze");
		public static final Tag.Named<Item> GEARS_COPPER = forgeTag("gears/copper");
		public static final Tag.Named<Item> GEARS_TIN = forgeTag("gears/tin");
		public static final Tag.Named<Item> GEARS_STONE = forgeTag("gears/stone");

		public static final Tag.Named<Item> DUSTS_ASH = forgeTag("dusts/ash");
		public static final Tag.Named<Item> SAWDUST = forgeTag("sawdust");

		public static final Tag.Named<Item> GEMS_APATITE = forgeTag("gems/apatite");

		public static final Tag.Named<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final Tag.Named<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final Tag.Named<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final Tag.Named<Item> ORES_TIN = forgeTag("ores/tin");
		public static final Tag.Named<Item> RAW_TIN = forgeTag("raw_materials/tin");
		public static final Tag.Named<Item> ORES_APATITE = forgeTag("ores/apatite");

		public static final Tag.Named<Item> STAMPS = tag("stamps");

		public static final Tag.Named<Item> FRUITS = tag("forestry_fruits");

		public static final Tag.Named<Item> MINER_ALLOW = tag("backpack/allow/miner");
		public static final Tag.Named<Item> MINER_REJECT = tag("backpack/reject/miner");

		public static final Tag.Named<Item> DIGGER_ALLOW = tag("backpack/allow/digger");
		public static final Tag.Named<Item> DIGGER_REJECT = tag("backpack/reject/digger");

		public static final Tag.Named<Item> FORESTER_ALLOW = tag("backpack/allow/forester");
		public static final Tag.Named<Item> FORESTER_REJECT = tag("backpack/reject/forester");

		public static final Tag.Named<Item> ADVENTURER_ALLOW = tag("backpack/allow/adventurer");
		public static final Tag.Named<Item> ADVENTURER_REJECT = tag("backpack/reject/adventurer");

		public static final Tag.Named<Item> BUILDER_ALLOW = tag("backpack/allow/builder");
		public static final Tag.Named<Item> BUILDER_REJECT = tag("backpack/reject/builder");

		public static final Tag.Named<Item> HUNTER_ALLOW = tag("backpack/allow/hunter");
		public static final Tag.Named<Item> HUNTER_REJECT = tag("backpack/reject/hunter");

		private static Tag.Named<Item> tag(String name) {
			return ItemTags.bind(Constants.MOD_ID + ":" + name);
		}

		private static Tag.Named<Item> forgeTag(String name) {
			return ItemTags.bind("forge:" + name);
		}

		private static Tag.Named<Item> vanillaTag(String name) {
			return ItemTags.bind(name);
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
