package forestry.arboriculture.features;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.arboriculture.genetics.IAlleleFruit;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodAccess;
import forestry.arboriculture.blocks.BlockArboriculture;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.blocks.BlockForestryDoor;
import forestry.arboriculture.blocks.BlockForestryFence;
import forestry.arboriculture.blocks.BlockForestryFenceGate;
import forestry.arboriculture.blocks.BlockForestryLeaves;
import forestry.arboriculture.blocks.BlockForestryLog;
import forestry.arboriculture.blocks.BlockForestryPlank;
import forestry.arboriculture.blocks.BlockForestrySlab;
import forestry.arboriculture.blocks.BlockForestryStairs;
import forestry.arboriculture.blocks.BlockFruitPod;
import forestry.arboriculture.blocks.BlockSapling;
import forestry.arboriculture.blocks.BlockTypeArboricultureTesr;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.genetics.alleles.AlleleFruits;
import forestry.arboriculture.items.ItemBlockDecorativeLeaves;
import forestry.arboriculture.items.ItemBlockLeaves;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.arboriculture.items.ItemBlockWoodDoor;
import forestry.arboriculture.items.ItemBlockWoodSlab;
import forestry.core.config.Constants;
import forestry.core.items.ItemBlockBase;
import forestry.modules.ForestryModuleUids;
import forestry.modules.features.FeatureBlock;
import forestry.modules.features.FeatureBlockGroup;
import forestry.modules.features.FeatureType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

public class ArboricultureBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(Constants.MOD_ID).getRegistry(ForestryModuleUids.ARBORICULTURE);
	/* WOOD */
	public static final FeatureBlockGroup<BlockForestryLog, EnumForestryWoodType> LOGS = woodGroup(BlockForestryLog::new, WoodBlockKind.LOG, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryLog, EnumForestryWoodType> LOGS_FIREPROOF = woodGroup(BlockForestryLog::new, WoodBlockKind.LOG, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryLog, EnumVanillaWoodType> LOGS_VANILLA_FIREPROOF = woodGroup(BlockForestryLog::new, WoodBlockKind.LOG, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestryPlank, EnumForestryWoodType> PLANKS = woodGroup(BlockForestryPlank::new, WoodBlockKind.PLANKS, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryPlank, EnumForestryWoodType> PLANKS_FIREPROOF = woodGroup(BlockForestryPlank::new, WoodBlockKind.PLANKS, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryPlank, EnumVanillaWoodType> PLANKS_VANILLA_FIREPROOF = woodGroup(BlockForestryPlank::new, WoodBlockKind.PLANKS, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestrySlab, EnumForestryWoodType> SLABS = woodGroup((type) -> new BlockForestrySlab(PLANKS.get(type).block()), ItemBlockWoodSlab::new, WoodBlockKind.SLAB, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestrySlab, EnumForestryWoodType> SLABS_FIREPROOF = woodGroup((type) -> new BlockForestrySlab(PLANKS_FIREPROOF.get(type).block()), ItemBlockWoodSlab::new, WoodBlockKind.SLAB, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestrySlab, EnumVanillaWoodType> SLABS_VANILLA_FIREPROOF = woodGroup((type) -> new BlockForestrySlab(PLANKS_VANILLA_FIREPROOF.get(type).block()), ItemBlockWoodSlab::new, WoodBlockKind.SLAB, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestryFence, EnumForestryWoodType> FENCES = woodGroup(BlockForestryFence::new, WoodBlockKind.FENCE, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryFence, EnumForestryWoodType> FENCES_FIREPROOF = woodGroup(BlockForestryFence::new, WoodBlockKind.FENCE, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryFence, EnumVanillaWoodType> FENCES_VANILLA_FIREPROOF = woodGroup(BlockForestryFence::new, WoodBlockKind.FENCE, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestryFenceGate, EnumForestryWoodType> FENCE_GATES = woodGroup(BlockForestryFenceGate::new, WoodBlockKind.FENCE_GATE, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryFenceGate, EnumForestryWoodType> FENCE_GATES_FIREPROOF = woodGroup(BlockForestryFenceGate::new, WoodBlockKind.FENCE_GATE, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryFenceGate, EnumVanillaWoodType> FENCE_GATES_VANILLA_FIREPROOF = woodGroup(BlockForestryFenceGate::new, WoodBlockKind.FENCE_GATE, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestryStairs, EnumForestryWoodType> STAIRS = woodGroup((type) -> new BlockForestryStairs(PLANKS.get(type).block()), WoodBlockKind.STAIRS, false, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryStairs, EnumForestryWoodType> STAIRS_FIREPROOF = woodGroup((type) -> new BlockForestryStairs(PLANKS_FIREPROOF.get(type).block()), WoodBlockKind.STAIRS, true, EnumForestryWoodType.VALUES);
	public static final FeatureBlockGroup<BlockForestryStairs, EnumVanillaWoodType> STAIRS_VANILLA_FIREPROOF = woodGroup((type) -> new BlockForestryStairs(PLANKS_VANILLA_FIREPROOF.get(type).block()), WoodBlockKind.STAIRS, true, EnumVanillaWoodType.VALUES);

	public static final FeatureBlockGroup<BlockForestryDoor, EnumForestryWoodType> DOORS = REGISTRY.blockGroup(BlockForestryDoor::new, EnumForestryWoodType.VALUES).setItem(ItemBlockWoodDoor::new).setIdent("door").affix().create();

	/* GENETICS */
	public static final FeatureBlock<BlockSapling, BlockItem> SAPLING_GE = REGISTRY.block(BlockSapling::new, "sapling_ge");
	public static final FeatureBlock<BlockForestryLeaves, ItemBlockLeaves> LEAVES = REGISTRY.block(BlockForestryLeaves::new, ItemBlockLeaves::new, "leaves");
	public static final FeatureBlockGroup<BlockDefaultLeaves, TreeDefinition> LEAVES_DEFAULT = REGISTRY.blockGroup(BlockDefaultLeaves::new, TreeDefinition.VALUES).setItem(ItemBlockLeaves::new).setIdent("default_leaves").affix().create();
	public static final FeatureBlockGroup<BlockDefaultLeavesFruit, TreeDefinition> LEAVES_DEFAULT_FRUIT = REGISTRY.blockGroup(BlockDefaultLeavesFruit::new, TreeDefinition.VALUES).setItem(ItemBlockLeaves::new).setIdent("default_leaves_fruit").affix().create();
	public static final FeatureBlockGroup<BlockDecorativeLeaves, TreeDefinition> LEAVES_DECORATIVE = REGISTRY.blockGroup(BlockDecorativeLeaves::new, TreeDefinition.VALUES).setItem(ItemBlockDecorativeLeaves::new).setIdent("decorative_leaves").affix().create();
	public static final FeatureBlockGroup<BlockFruitPod, IAlleleFruit> PODS = REGISTRY.blockGroup(BlockFruitPod::new, AlleleFruits.getFruitAllelesWithModels()).setIdent("pods").create();

	/* MACHINES */
	public static final FeatureBlock<BlockArboriculture, ItemBlockBase> TREE_CHEST = REGISTRY.block(() -> new BlockArboriculture(BlockTypeArboricultureTesr.ARB_CHEST), (block) -> new ItemBlockBase<>(block, BlockTypeArboricultureTesr.ARB_CHEST), "tree_chest");

	private static <B extends Block & IWoodTyped, S extends IWoodType> FeatureBlockGroup<B, S> woodGroup(BiFunction<Boolean, S, B> constructor, WoodBlockKind kind, boolean fireproof, S[] types) {
		return woodGroup(constructor, ItemBlockWood::new, kind, fireproof, types);
	}

	private static <B extends Block & IWoodTyped, S extends IWoodType> FeatureBlockGroup<B, S> woodGroup(BiFunction<Boolean, S, B> constructor, Function<B, BlockItem> itemConstructor, WoodBlockKind kind, boolean fireproof, S[] types) {
		return registerWood(REGISTRY.blockGroup((type) -> constructor.apply(fireproof, type), types).setItem(itemConstructor).setIdent((fireproof ? "fireproof_" : "") + kind.getName()).affix().create(), kind);
	}

	private static <B extends Block & IWoodTyped, S extends IWoodType> FeatureBlockGroup<B, S> woodGroup(Function<S, B> constructor, WoodBlockKind kind, boolean fireproof, S[] types) {
		return woodGroup(constructor, ItemBlockWood::new, kind, fireproof, types);
	}

	private static <B extends Block & IWoodTyped, S extends IWoodType> FeatureBlockGroup<B, S> woodGroup(Function<S, B> constructor, Function<B, BlockItem> itemConstructor, WoodBlockKind kind, boolean fireproof, S[] types) {
		return registerWood(REGISTRY.blockGroup(constructor, types).setItem(itemConstructor).setIdent((fireproof ? "fireproof_" : "") + kind.getName()).affix().create(), kind);
	}

	private static <B extends Block & IWoodTyped, S extends IWoodType> FeatureBlockGroup<B, S> registerWood(FeatureBlockGroup<B, S> group, WoodBlockKind kind) {
		REGISTRY.addListener(FeatureType.ITEM, event -> WoodAccess.getInstance().registerFeatures(group, kind));
		return group;
	}

	private ArboricultureBlocks() {
	}
}
