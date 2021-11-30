package forestry.core.data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import forestry.core.config.Constants;

public class ForestryTags {
	public static class Blocks {

		public static final ITag.INamedTag<Block> CHARCOAL = forgeTag("charcoal");

		public static final ITag.INamedTag<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final ITag.INamedTag<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final ITag.INamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final ITag.INamedTag<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final ITag.INamedTag<Block> ORES_COPPER = forgeTag("ores/copper");
		public static final ITag.INamedTag<Block> ORES_TIN = forgeTag("ores/tin");
		public static final ITag.INamedTag<Block> ORES_APATITE = forgeTag("ores/apatite");
		public static final ITag.INamedTag<Block> PALM_LOGS = tag("palm_logs");
		public static final ITag.INamedTag<Block> PAPAYA_LOGS = tag("papaya_logs");

		private static ITag.INamedTag<Block> tag(String name) {
			return BlockTags.bind(Constants.MOD_ID + ":" + name);
		}

		private static ITag.INamedTag<Block> forgeTag(String name) {
			return BlockTags.bind("forge:" + name);
		}

		private static ITag.INamedTag<Block> vanillaTag(String name) {
			return BlockTags.bind(name);
		}

		private Blocks() {

		}
	}

	public static class Items {

		public static final ITag.INamedTag<Item> CHARCOAL = forgeTag("charcoal");

		public static final ITag.INamedTag<Item> BEE_COMBS = tag("combs");
		public static final ITag.INamedTag<Item> PROPOLIS = tag("propolis");
		public static final ITag.INamedTag<Item> DROP_HONEY = tag("drop_honey");

		public static final ITag.INamedTag<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
		public static final ITag.INamedTag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
		public static final ITag.INamedTag<Item> INGOTS_TIN = forgeTag("ingots/tin");

		public static final ITag.INamedTag<Item> GEARS = forgeTag("gears");
		public static final ITag.INamedTag<Item> GEARS_BRONZE = forgeTag("gears/bronze");
		public static final ITag.INamedTag<Item> GEARS_COPPER = forgeTag("gears/copper");
		public static final ITag.INamedTag<Item> GEARS_TIN = forgeTag("gears/tin");
		public static final ITag.INamedTag<Item> GEARS_STONE = forgeTag("gears/stone");

		public static final ITag.INamedTag<Item> DUSTS_ASH = forgeTag("dusts/ash");
		public static final ITag.INamedTag<Item> SAWDUST = forgeTag("sawdust");

		public static final ITag.INamedTag<Item> GEMS_APATITE = forgeTag("gems/apatite");

		public static final ITag.INamedTag<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final ITag.INamedTag<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final ITag.INamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final ITag.INamedTag<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final ITag.INamedTag<Item> ORES_COPPER = forgeTag("ores/copper");
		public static final ITag.INamedTag<Item> ORES_TIN = forgeTag("ores/tin");
		public static final ITag.INamedTag<Item> ORES_APATITE = forgeTag("ores/apatite");

		public static final ITag.INamedTag<Item> STAMPS = tag("stamps");

		public static final ITag.INamedTag<Item> FRUITS = tag("forestry_fruits");

		public static final ITag.INamedTag<Item> MINER_ALLOW = tag("backpack/allow/miner");
		public static final ITag.INamedTag<Item> MINER_REJECT = tag("backpack/reject/miner");

		public static final ITag.INamedTag<Item> DIGGER_ALLOW = tag("backpack/allow/digger");
		public static final ITag.INamedTag<Item> DIGGER_REJECT = tag("backpack/reject/digger");

		public static final ITag.INamedTag<Item> FORESTER_ALLOW = tag("backpack/allow/forester");
		public static final ITag.INamedTag<Item> FORESTER_REJECT = tag("backpack/reject/forester");

		public static final ITag.INamedTag<Item> ADVENTURER_ALLOW = tag("backpack/allow/adventurer");
		public static final ITag.INamedTag<Item> ADVENTURER_REJECT = tag("backpack/reject/adventurer");

		public static final ITag.INamedTag<Item> BUILDER_ALLOW = tag("backpack/allow/builder");
		public static final ITag.INamedTag<Item> BUILDER_REJECT = tag("backpack/reject/builder");

		public static final ITag.INamedTag<Item> HUNTER_ALLOW = tag("backpack/allow/hunter");
		public static final ITag.INamedTag<Item> HUNTER_REJECT = tag("backpack/reject/hunter");

		private static ITag.INamedTag<Item> tag(String name) {
			return ItemTags.bind(Constants.MOD_ID + ":" + name);
		}

		private static ITag.INamedTag<Item> forgeTag(String name) {
			return ItemTags.bind("forge:" + name);
		}

		private static ITag.INamedTag<Item> vanillaTag(String name) {
			return ItemTags.bind(name);
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
