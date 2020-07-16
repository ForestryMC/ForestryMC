package forestry.core.data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import forestry.core.config.Constants;

public class ForestryTags {
	public static class Blocks {

		public static final ITag<Block> CHARCOAL = forgeTag("charcoal");

		public static final ITag<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final ITag<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final ITag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final ITag<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final ITag<Block> ORES_COPPER = tag("ores/copper");
		public static final ITag<Block> ORES_TIN = tag("ores/tin");
		public static final ITag<Block> ORES_APATITE = tag("ores/apatite");

		private static ITag<Block> tag(String name) {
			return BlockTags.makeWrapperTag(Constants.MOD_ID + ":" + name);
		}

		private static ITag<Block> forgeTag(String name) {
			return BlockTags.makeWrapperTag("forge:" + name);
		}

		private static ITag<Block> vanillaTag(String name) {
			return BlockTags.makeWrapperTag(name);
		}

		private Blocks() {
		}
	}

	public static class Items {

		public static final ITag<Item> CHARCOAL = forgeTag("charcoal");

		public static final ITag<Item> BEE_COMBS = tag("combs");
		public static final ITag<Item> PROPOLIS = tag("propolis");
		public static final ITag<Item> DROP_HONEY = tag("drop_honey");

		public static final ITag<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
		public static final ITag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
		public static final ITag<Item> INGOTS_TIN = forgeTag("ingots/tin");

		public static final ITag<Item> GEARS = forgeTag("gears");
		public static final ITag<Item> GEARS_BRONZE = forgeTag("gears/bronze");
		public static final ITag<Item> GEARS_COPPER = forgeTag("gears/copper");
		public static final ITag<Item> GEARS_TIN = forgeTag("gears/tin");
		public static final ITag<Item> GEARS_STONE = forgeTag("gears/stone");

		public static final ITag<Item> DUSTS_ASH = tag("dusts/ash");

		public static final ITag<Item> GEMS_APATITE = tag("gems/apatite");

		public static final ITag<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final ITag<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final ITag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final ITag<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final ITag<Item> ORES = tag("ores");
		public static final ITag<Item> ORES_COPPER = tag("ores/copper");
		public static final ITag<Item> ORES_TIN = tag("ores/tin");
		public static final ITag<Item> ORES_APATITE = tag("ores/apatite");


		public static final ITag<Item> STAMPS = tag("stamps");

		public static final ITag<Item> FRUITS = tag("forestry_fruits");

		private static ITag<Item> tag(String name) {
			return ItemTags.makeWrapperTag(Constants.MOD_ID + ":" + name);
		}

		private static ITag<Item> forgeTag(String name) {
			return ItemTags.makeWrapperTag("forge:" + name);
		}

		private static ITag<Item> vanillaTag(String name) {
			return ItemTags.makeWrapperTag(name);
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
