package forestry.storage.features;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.items.EnumPropolis;
import forestry.core.features.CoreBlocks;
import forestry.core.features.CoreItems;
import forestry.modules.features.FeatureItem;
import forestry.modules.features.FeatureItemGroup;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;
import forestry.storage.ModuleCrates;
import forestry.storage.items.ItemCrated;

@FeatureProvider
public class CrateItems {

	private static final List<FeatureItem<ItemCrated>> CRATES = new ArrayList<>();
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ModuleCrates.class);

	// TODO map of item to crate or similar?
	public static final FeatureItem<ItemCrated> CRATE = REGISTRY.item(() -> new ItemCrated(ItemStack.EMPTY), "crate");

	// Core
	public static final FeatureItem<ItemCrated> CRATED_PEAT = register(CoreItems.PEAT, "crated_peat");
	public static final FeatureItem<ItemCrated> CRATED_APATITE = register(CoreItems.APATITE, "crated_apatite");
	public static final FeatureItem<ItemCrated> CRATED_FERTILIZER_COMPOUND = register(CoreItems.FERTILIZER_COMPOUND, "crated_fertilizer_compound");
	public static final FeatureItem<ItemCrated> CRATED_MULCH = register(CoreItems.MULCH, "crated_mulch");
	public static final FeatureItem<ItemCrated> CRATED_PHOSPHOR = register(CoreItems.PHOSPHOR, "crated_phosphor");
	public static final FeatureItem<ItemCrated> CRATED_ASH = register(CoreItems.ASH, "crated_ash");
	public static final FeatureItem<ItemCrated> CRATED_TIN = register(CoreItems.INGOT_TIN, "crated_tin");
	public static final FeatureItem<ItemCrated> CRATED_COPPER = register(Items.COPPER_INGOT, "crated_copper");
	public static final FeatureItem<ItemCrated> CRATED_BRONZE = register(CoreItems.INGOT_BRONZE, "crated_bronze");

	public static final FeatureItem<ItemCrated> CRATED_HUMUS = register(CoreBlocks.HUMUS, "crated_humus");
	public static final FeatureItem<ItemCrated> CRATED_BOG_EARTH = register(CoreBlocks.BOG_EARTH, "crated_bog_earth");

	public static final FeatureItem<ItemCrated> CRATED_WHEAT = register(Items.WHEAT, "crated_wheat");
	public static final FeatureItem<ItemCrated> CRATED_COOKIE = register(Items.COOKIE, "crated_cookie");
	public static final FeatureItem<ItemCrated> CRATED_REDSTONE = register(Items.REDSTONE, "crated_redstone");
	public static final FeatureItem<ItemCrated> CRATED_LAPIS = register(Items.LAPIS_LAZULI, "crated_lapis");
	public static final FeatureItem<ItemCrated> CRATED_SUGAR_CANE = register(Items.SUGAR_CANE, "crated_sugar_cane");
	public static final FeatureItem<ItemCrated> CRATED_CLAY_BALL = register(Items.CLAY_BALL, "crated_clay_ball");
	public static final FeatureItem<ItemCrated> CRATED_GLOWSTONE = register(Items.GLOWSTONE_DUST, "crated_glowstone");
	public static final FeatureItem<ItemCrated> CRATED_APPLE = register(Items.APPLE, "crated_apple");
	public static final FeatureItem<ItemCrated> CRATED_COAL = register(Items.COAL, "crated_coal");
	public static final FeatureItem<ItemCrated> CRATED_CHARCOAL = register(Items.CHARCOAL, "crated_charcoal");
	public static final FeatureItem<ItemCrated> CRATED_SEEDS = register(Items.WHEAT_SEEDS, "crated_seeds");
	public static final FeatureItem<ItemCrated> CRATED_POTATO = register(Items.POTATO, "crated_potato");
	public static final FeatureItem<ItemCrated> CRATED_CARROT = register(Items.CARROT, "crated_carrot");
	public static final FeatureItem<ItemCrated> CRATED_BEETROOT = register(Items.BEETROOT, "crated_beetroot");
	public static final FeatureItem<ItemCrated> CRATED_NETHER_WART = register(Items.NETHER_WART, "crated_nether_wart");

	public static final FeatureItem<ItemCrated> CRATED_OAK_LOG = register(Items.OAK_LOG, "crated_oak_log");
	public static final FeatureItem<ItemCrated> CRATED_BIRCH_LOG = register(Items.BIRCH_LOG, "crated_birch_log");
	public static final FeatureItem<ItemCrated> CRATED_JUNGLE_LOG = register(Items.JUNGLE_LOG, "crated_jungle_log");
	public static final FeatureItem<ItemCrated> CRATED_SPRUCE_LOG = register(Items.SPRUCE_LOG, "crated_spruce_log");
	public static final FeatureItem<ItemCrated> CRATED_ACACIA_LOG = register(Items.ACACIA_LOG, "crated_acacia_log");
	public static final FeatureItem<ItemCrated> CRATED_DARK_OAK_LOG = register(Items.DARK_OAK_LOG, "crated_dark_oak_log");
	public static final FeatureItem<ItemCrated> CRATED_COBBLESTONE = register(Items.COBBLESTONE, "crated_cobblestone");
	public static final FeatureItem<ItemCrated> CRATED_DIRT = register(Items.DIRT, "crated_dirt");
	public static final FeatureItem<ItemCrated> CRATED_GRASS_BLOCK = register(Items.GRASS_BLOCK, "crated_grass_block");
	public static final FeatureItem<ItemCrated> CRATED_STONE = register(Items.STONE, "crated_stone");
	public static final FeatureItem<ItemCrated> CRATED_GRANITE = register(Items.GRANITE, "crated_granite");
	public static final FeatureItem<ItemCrated> CRATED_DIORITE = register(Items.DIORITE, "crated_diorite");
	public static final FeatureItem<ItemCrated> CRATED_ANDESITE = register(Items.ANDESITE, "crated_andesite");
	public static final FeatureItem<ItemCrated> CRATED_PRISMARINE = register(Items.PRISMARINE, "crated_prismarine");
	public static final FeatureItem<ItemCrated> CRATED_PRISMARINE_BRICKS = register(Items.PRISMARINE_BRICKS, "crated_prismarine_bricks");
	public static final FeatureItem<ItemCrated> CRATED_DARK_PRISMARINE = register(Items.DARK_PRISMARINE, "crated_dark_prismarine");
	public static final FeatureItem<ItemCrated> CRATED_BRICKS = register(Items.BRICKS, "crated_bricks");
	public static final FeatureItem<ItemCrated> CRATED_CACTUS = register(Items.CACTUS, "crated_cactus");
	public static final FeatureItem<ItemCrated> CRATED_SAND = register(Items.SAND, "crated_sand");
	public static final FeatureItem<ItemCrated> CRATED_RED_SAND = register(Items.RED_SAND, "crated_red_sand");
	public static final FeatureItem<ItemCrated> CRATED_OBSIDIAN = register(Items.OBSIDIAN, "crated_obsidian");
	public static final FeatureItem<ItemCrated> CRATED_NETHERRACK = register(Items.NETHERRACK, "crated_netherrack");
	public static final FeatureItem<ItemCrated> CRATED_SOUL_SAND = register(Items.SOUL_SAND, "crated_soul_sand");
	public static final FeatureItem<ItemCrated> CRATED_SANDSTONE = register(Items.SANDSTONE, "crated_sandstone");
	public static final FeatureItem<ItemCrated> CRATED_NETHER_BRICKS = register(Items.NETHER_BRICKS, "crated_nether_bricks");
	public static final FeatureItem<ItemCrated> CRATED_MYCELIUM = register(Items.MYCELIUM, "crated_mycelium");
	public static final FeatureItem<ItemCrated> CRATED_GRAVEL = register(Items.GRAVEL, "crated_gravel");
	public static final FeatureItem<ItemCrated> CRATED_OAK_SAPLING = register(Items.OAK_SAPLING, "crated_oak_sapling");
	public static final FeatureItem<ItemCrated> CRATED_BIRCH_SAPLING = register(Items.BIRCH_SAPLING, "crated_birch_sapling");
	public static final FeatureItem<ItemCrated> CRATED_JUNGLE_SAPLING = register(Items.JUNGLE_SAPLING, "crated_jungle_sapling");
	public static final FeatureItem<ItemCrated> CRATED_SPRUCE_SAPLING = register(Items.SPRUCE_SAPLING, "crated_spruce_sapling");
	public static final FeatureItem<ItemCrated> CRATED_ACACIA_SAPLING = register(Items.ACACIA_SAPLING, "crated_acacia_sapling");
	public static final FeatureItem<ItemCrated> CRATED_DARK_OAK_SAPLING = register(Items.DARK_OAK_SAPLING, "crated_dark_oak_sapling");

	public static final FeatureItem<ItemCrated> CRATED_BEESWAX = register(CoreItems.BEESWAX, "crated_beeswax");
	public static final FeatureItem<ItemCrated> CRATED_REFRACTORY_WAX = register(CoreItems.REFRACTORY_WAX, "crated_refractory_wax");

	// Apiculture
	public static final FeatureItem<ItemCrated> CRATED_POLLEN_CLUSTER_NORMAL = register(ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.NORMAL), "crated_pollen_cluster_normal");
	public static final FeatureItem<ItemCrated> CRATED_POLLEN_CLUSTER_CRYSTALLINE = register(ApicultureItems.POLLEN_CLUSTER.get(EnumPollenCluster.CRYSTALLINE), "crated_pollen_cluster_crystalline");
	public static final FeatureItem<ItemCrated> CRATED_PROPOLIS = register(ApicultureItems.PROPOLIS.get(EnumPropolis.NORMAL), "crated_propolis");
	public static final FeatureItem<ItemCrated> CRATED_HONEYDEW = register(ApicultureItems.HONEYDEW, "crated_honeydew");
	public static final FeatureItem<ItemCrated> CRATED_ROYAL_JELLY = register(ApicultureItems.ROYAL_JELLY, "crated_royal_jelly");
	public static final FeatureItemGroup<ItemCrated, EnumHoneyComb> CRATED_BEE_COMBS = REGISTRY.itemGroup(comb -> new ItemCrated(ApicultureItems.BEE_COMBS.get(comb).stack()), "crated_bee_comb", EnumHoneyComb.VALUES);

	// TODO: Arboriculture crates (requires tags)
	// ICrateRegistry crateRegistry = StorageManager.crateRegistry;
	// crateRegistry.registerCrate(EnumFruit.CHERRY.getStack());
	// crateRegistry.registerCrate(EnumFruit.WALNUT.getStack());
	// crateRegistry.registerCrate(EnumFruit.CHESTNUT.getStack());
	// crateRegistry.registerCrate(EnumFruit.LEMON.getStack());
	// crateRegistry.registerCrate(EnumFruit.PLUM.getStack());
	// crateRegistry.registerCrate(EnumFruit.PAPAYA.getStack());
	// crateRegistry.registerCrate(EnumFruit.DATES.getStack());

	static {
		CRATES.addAll(CRATED_BEE_COMBS.getFeatures());
	}

	private CrateItems() {
	}

	private static FeatureItem<ItemCrated> register(ItemLike contained, String identifier) {
		FeatureItem<ItemCrated> item = REGISTRY.item(() -> new ItemCrated(new ItemStack(contained)), identifier);
		CRATES.add(item);
		return item;
	}

	public static List<FeatureItem<ItemCrated>> getCrates() {
		return CRATES;
	}
}
