package forestry.core.data;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import forestry.core.config.Constants;

public class ForestryTags {
	public static class Blocks {

		public static final Tag<Block> CHARCOAL = forgeTag("charcoal");

		public static final Tag<Block> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final Tag<Block> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final Tag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final Tag<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final Tag<Block> ORES_COPPER = tag("ores/copper");
		public static final Tag<Block> ORES_TIN = tag("ores/tin");
		public static final Tag<Block> ORES_APATITE = tag("ores/apatite");

		private static Tag<Block> tag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private static Tag<Block> forgeTag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation("forge", name));
		}

		private static Tag<Block> vanillaTag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation(name));
		}

		private Blocks() {
		}
	}

	public static class Items {

		public static final Tag<Item> CHARCOAL = forgeTag("charcoal");

		public static final Tag<Item> BEE_COMBS = tag("combs");
		public static final Tag<Item> PROPOLIS = tag("propolis");
		public static final Tag<Item> DROP_HONEY = tag("drop_honey");

		public static final Tag<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
		public static final Tag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
		public static final Tag<Item> INGOTS_TIN = forgeTag("ingots/tin");

		public static final Tag<Item> GEARS = forgeTag("gears");
		public static final Tag<Item> GEARS_BRONZE = forgeTag("gears/bronze");
		public static final Tag<Item> GEARS_COPPER = forgeTag("gears/copper");
		public static final Tag<Item> GEARS_TIN = forgeTag("gears/tin");
		public static final Tag<Item> GEARS_STONE = forgeTag("gears/stone");

		public static final Tag<Item> DUSTS_ASH = tag("dusts/ash");

		public static final Tag<Item> GEMS_APATITE = tag("gems/apatite");

		public static final Tag<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
		public static final Tag<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
		public static final Tag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
		public static final Tag<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

		public static final Tag<Item> ORES = tag("ores");
		public static final Tag<Item> ORES_COPPER = tag("ores/copper");
		public static final Tag<Item> ORES_TIN = tag("ores/tin");
		public static final Tag<Item> ORES_APATITE = tag("ores/apatite");


		public static final Tag<Item> STAMPS = tag("stamps");

		public static final Tag<Item> FRUITS = tag("forestry_fruits");

		private static Tag<Item> tag(String name) {
			return new ItemTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private static Tag<Item> forgeTag(String name) {
			return new ItemTags.Wrapper(new ResourceLocation("forge", name));
		}

		private static Tag<Item> vanillaTag(String name) {
			return new ItemTags.Wrapper(new ResourceLocation(name));
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
