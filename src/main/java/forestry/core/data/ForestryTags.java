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

		private static Tag<Block> tag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private Blocks() {
		}
	}

	public static class Items {

		public static final Tag<Item> CHARCOAL = tag("charcoal");

		private static Tag<Item> tag(String name) {
			return new ItemTags.Wrapper(new ResourceLocation(Constants.MOD_ID, name));
		}

		private Items() {
		}
	}

	private ForestryTags() {
	}
}
