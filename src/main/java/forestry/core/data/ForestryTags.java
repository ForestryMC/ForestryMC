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

		public static final Tag<Block> CHARCOAL = tag("charcoal");

		public static final Tag<Block> ORE_COPPER = tag("ores/copper");
		public static final Tag<Block> ORE_TIN = tag("ores/tin");
		public static final Tag<Block> ORE_APATITE = tag("ores/apatite");

		public static final Tag<Block> STORAGE_BLOCK_COPPER = tag("storage_blocks/copper");
		public static final Tag<Block> STORAGE_BLOCK_TIN = tag("storage_blocks/tin");
		public static final Tag<Block> STORAGE_BLOCK_BRONZE = tag("storage_blocks/bronze");
		public static final Tag<Block> STORAGE_BLOCK_APATITE = tag("storage_blocks/apatite");

		private static Tag<Block> tag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private Blocks() {
		}
	}

	public static class Items {

		public static final Tag<Item> CHARCOAL = tag("charcoal");
		public static final Tag<Item> BEE_COMBS = tag("comb");
		public static final Tag<Item> PROPOLIS = tag("propolis");
		public static final Tag<Item> ASH = tag("dusts/ash");
		public static final Tag<Item> DROP_HONEY = tag("drop_honey");

		public static final Tag<Item> INGOT_BRONZE = tag("ingots/bronze");
		public static final Tag<Item> INGOT_COPPER = tag("ingots/copper");
		public static final Tag<Item> INGOT_TIN = tag("ingots/tin");
		public static final Tag<Item> GEM_APATITE = tag("gems/apatite");

		public static final Tag<Item> ORE_COPPER = tag("ores/copper");
		public static final Tag<Item> ORE_TIN = tag("ores/tin");
		public static final Tag<Item> ORE_APATITE = tag("ores/apatite");

		public static final Tag<Item> STORAGE_BLOCK_COPPER = tag("storage_blocks/copper");
		public static final Tag<Item> STORAGE_BLOCK_TIN = tag("storage_blocks/tin");
		public static final Tag<Item> STORAGE_BLOCK_BRONZE = tag("storage_blocks/bronze");
		public static final Tag<Item> STORAGE_BLOCK_APATITE = tag("storage_blocks/apatite");

		public static final Tag<Item> GEARS = tag("gears");
		public static final Tag<Item> GEAR_BRONZE = tag("gears/bronze");
		public static final Tag<Item> GEAR_COPPER = tag("gears/copper");
		public static final Tag<Item> GEAR_TIN = tag("gears/tin");
		public static final Tag<Item> GEAR_STONE = tag("gears/stone");

		public static final Tag<Item> STAMPS = tag("stamps");

		public static final Tag<Item> FRUITS = tag("forestry_fruits");

		private static Tag<Item> tag(String name) {
			//TODO not sure we want mod id here? maybe forge instead?
			return new ItemTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
