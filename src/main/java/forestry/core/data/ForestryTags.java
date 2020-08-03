package forestry.core.data;

import forestry.core.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

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

        private static ITag.INamedTag<Block> tag(String name) {
            return BlockTags.makeWrapperTag(Constants.MOD_ID + ":" + name);
        }

        private static ITag.INamedTag<Block> forgeTag(String name) {
            return BlockTags.makeWrapperTag("forge:" + name);
        }

        private static ITag.INamedTag<Block> vanillaTag(String name) {
            return BlockTags.makeWrapperTag(name);
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

        public static final ITag.INamedTag<Item> DUSTS_ASH = tag("dusts/ash");

        public static final ITag.INamedTag<Item> GEMS_APATITE = tag("gems/apatite");

        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_APATITE = forgeTag("storage_blocks/apatite");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_TIN = forgeTag("storage_blocks/tin");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");

        public static final ITag.INamedTag<Item> ORES_COPPER = forgeTag("ores/copper");
        public static final ITag.INamedTag<Item> ORES_TIN = forgeTag("ores/tin");
        public static final ITag.INamedTag<Item> ORES_APATITE = forgeTag("ores/apatite");


        public static final ITag.INamedTag<Item> STAMPS = tag("stamps");

        public static final ITag.INamedTag<Item> FRUITS = tag("forestry_fruits");

        private static ITag.INamedTag<Item> tag(String name) {
            return ItemTags.makeWrapperTag(Constants.MOD_ID + ":" + name);
        }

        private static ITag.INamedTag<Item> forgeTag(String name) {
            return ItemTags.makeWrapperTag("forge:" + name);
        }

        private static ITag.INamedTag<Item> vanillaTag(String name) {
            return ItemTags.makeWrapperTag(name);
        }

        private Items() {
        }
    }

    private ForestryTags() {
    }
}
