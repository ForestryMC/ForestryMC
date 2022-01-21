package forestry.core.data;

import java.util.Map;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.core.Direction;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.blocks.BlockDecorativeLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeaves;
import forestry.arboriculture.blocks.BlockDefaultLeavesFruit;
import forestry.arboriculture.blocks.BlockForestryDoor;
import forestry.arboriculture.blocks.BlockForestryFence;
import forestry.arboriculture.blocks.BlockForestryFenceGate;
import forestry.arboriculture.blocks.BlockForestryLog;
import forestry.arboriculture.blocks.BlockForestryPlank;
import forestry.arboriculture.blocks.BlockForestrySlab;
import forestry.arboriculture.blocks.BlockForestryStairs;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.modules.features.FeatureBlock;

public class WoodBlockStateProvider extends BlockStateProvider {

	public WoodBlockStateProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void registerStates() {
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES.getFeatureByType().entrySet()) {
			addFence(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_FIREPROOF.getFeatureByType().entrySet()) {
			addFence(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addFence(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryDoor, BlockItem>> stair : ArboricultureBlocks.DOORS.getFeatureByType().entrySet()) {
			addDoor(stair.getValue(), stair.getKey());
		}
		//Replaced by the model loader later
		for (BlockDecorativeLeaves leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model("forestry:block/leaves")));
		}
		for (BlockDefaultLeavesFruit leaves : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model("forestry:block/leaves")).alwaysIgnore(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT));
		}
		for (BlockDefaultLeaves leaves : ArboricultureBlocks.LEAVES_DEFAULT.getBlocks()) {
			addVariants(leaves, new Builder().always(variant -> variant.model("forestry:block/leaves")).alwaysIgnore(LeavesBlock.DISTANCE, LeavesBlock.PERSISTENT));
		}
	}

	private String getLocation(IWoodType type, WoodBlockKind kind) {
		String location;
		if (type instanceof EnumVanillaWoodType) {
			location = "block/" + type.getSerializedName() + "_" + kind.getSerializedName();
		} else {
			String kindName = kind.getSerializedName();
			if (!kindName.endsWith("s")) {
				kindName = kindName + "s";
			}
			location = "forestry:block/arboriculture/" + kindName + "/" + type.getSerializedName();
		}
		return location;
	}

	private void addPlank(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		addVariants(feature.block(), new Builder().always((variant -> variant.model(getLocation(type, WoodBlockKind.PLANKS)))));
	}

	private void addLog(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		addVariants(feature.block(), new Builder()
			.always((variant) -> variant.model(getLocation(type, WoodBlockKind.LOG)))
			.property(BlockStateProperties.AXIS, Direction.Axis.X, (variant) -> variant.rotationX(90).rotationY(90))
			.property(BlockStateProperties.AXIS, Direction.Axis.Z, (variant) -> variant.rotationX(90)));
	}

	private void addStair(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = getLocation(type, WoodBlockKind.STAIRS);
		BlockState defaultState = feature.defaultState();
		addVariants(feature.block(), new Builder()
			.alwaysIgnore(StairBlock.WATERLOGGED)
				.always((variant) -> variant.lock(true))
				.property(StairBlock.HALF, Half.TOP, (variant) -> variant.rotationX(180))
				.property(StairBlock.SHAPE, StairsShape.INNER_LEFT, (variant) -> variant.model(modelLocation + "_inner"))
				.property(StairBlock.SHAPE, StairsShape.INNER_RIGHT, (variant) -> variant.model(modelLocation + "_inner"))
				.property(StairBlock.SHAPE, StairsShape.OUTER_LEFT, (variant) -> variant.model(modelLocation + "_outer"))
				.property(StairBlock.SHAPE, StairsShape.OUTER_RIGHT, (variant) -> variant.model(modelLocation + "_outer"))
				.property(StairBlock.SHAPE, StairsShape.STRAIGHT, (variant) -> variant.model(modelLocation))
				.property(StairBlock.FACING, Direction.WEST, (variant) -> variant.rotationY(180))
				.property(StairBlock.FACING, Direction.SOUTH, (variant) -> variant.rotationY(90))
				.property(StairBlock.FACING, Direction.NORTH, (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.rotationY(0))
				.state(defaultState.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), (variant) -> variant.rotationY(180))
				.state(defaultState.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.rotationY(0))
				.state(defaultState.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM).setValue(StairBlock.SHAPE, StairsShape.INNER_LEFT), (variant) -> variant.rotationY(180))
				.state(defaultState.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.rotationY(180))
				.state(defaultState.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), (variant) -> variant.rotationY(0))
				.state(defaultState.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.rotationY(180))
				.state(defaultState.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.TOP).setValue(StairBlock.SHAPE, StairsShape.INNER_RIGHT), (variant) -> variant.rotationY(0))
		);
	}

	private void addSlab(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = getLocation(type, WoodBlockKind.SLAB);
		String plankLocation;
		if (type instanceof EnumVanillaWoodType) {
			plankLocation = "block/" + type.getSerializedName() + "_planks";
		} else {
			plankLocation = "forestry:block/arboriculture/planks/" + type.getSerializedName();
		}
		addVariants(feature.block(), new Builder()
			.alwaysIgnore(SlabBlock.WATERLOGGED)
			.property(SlabBlock.TYPE, SlabType.TOP, (variant) -> variant.model(modelLocation + "_top"))
			.property(SlabBlock.TYPE, SlabType.BOTTOM, (variant) -> variant.model(modelLocation))
			.property(SlabBlock.TYPE, SlabType.DOUBLE, (variant) -> variant.model(plankLocation)));
	}

	private void addFence(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = getLocation(type, WoodBlockKind.FENCE);
		addVariants(feature.block(), new MultipartBuilder()
			.always(variant -> variant.model(modelLocation + (type instanceof EnumVanillaWoodType ? "_post" : "")))
			.property(variant -> variant.model(modelLocation + "_side").lock(true), CrossCollisionBlock.NORTH, true)
			.property(variant -> variant.model(modelLocation + "_side").lock(true).rotationY(90), CrossCollisionBlock.EAST, true)
			.property(variant -> variant.model(modelLocation + "_side").lock(true).rotationY(180), CrossCollisionBlock.SOUTH, true)
			.property(variant -> variant.model(modelLocation + "_side").lock(true).rotationY(270), CrossCollisionBlock.WEST, true));
	}

	private void addFenceGate(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = getLocation(type, WoodBlockKind.FENCE_GATE);
		addVariants(feature.block(), new Builder()
				.always((variant) -> variant.lock(true))
				.alwaysIgnore(FenceGateBlock.POWERED)
				.property(HorizontalDirectionalBlock.FACING, Direction.WEST, (variant) -> variant.rotationY(90))
				.property(HorizontalDirectionalBlock.FACING, Direction.EAST, (variant) -> variant.rotationY(270))
				.property(HorizontalDirectionalBlock.FACING, Direction.NORTH, (variant) -> variant.rotationY(180))
			.property(FenceGateBlock.IN_WALL, false, FenceGateBlock.OPEN, false, (variant) -> variant.model(modelLocation))
			.property(FenceGateBlock.IN_WALL, false, FenceGateBlock.OPEN, true, (variant) -> variant.model(modelLocation + "_open"))
			.property(FenceGateBlock.IN_WALL, true, FenceGateBlock.OPEN, false, (variant) -> variant.model(modelLocation + "_wall"))
			.property(FenceGateBlock.IN_WALL, true, FenceGateBlock.OPEN, true, (variant) -> variant.model(modelLocation + "_wall_open")));
	}

	private void addDoor(FeatureBlock<? extends Block, BlockItem> feature, IWoodType type) {
		String modelLocation = getLocation(type, WoodBlockKind.DOOR);
		BlockState defaultState = feature.block().defaultBlockState();
		addVariants(feature.block(), new Builder()
				.alwaysIgnore(DoorBlock.POWERED)
				.property(DoorBlock.FACING, Direction.SOUTH, (variant) -> variant.rotationY(90))
				.property(DoorBlock.FACING, Direction.WEST, (variant) -> variant.rotationY(180))
				.property(DoorBlock.FACING, Direction.NORTH, (variant) -> variant.rotationY(270))
				.push().ignore(DoorBlock.HALF)
				.state(defaultState.setValue(DoorBlock.FACING, Direction.EAST).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(180))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.NORTH).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(0))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.EAST).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(270))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(0))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.WEST).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(90))
				.state(defaultState.setValue(DoorBlock.FACING, Direction.NORTH).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.rotationY(180))
				.popIgnore()
				.push().ignore(DoorBlock.FACING)
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, false), (variant) -> variant.model(modelLocation + "_bottom"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.model(modelLocation + "_bottom"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, false), (variant) -> variant.model(modelLocation + "_bottom_hinge"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.model(modelLocation + "_bottom_hinge"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, false), (variant) -> variant.model(modelLocation + "_top"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, true), (variant) -> variant.model(modelLocation + "_top"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT).setValue(DoorBlock.OPEN, false), (variant) -> variant.model(modelLocation + "_top_hinge"))
				.state(defaultState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.HINGE, DoorHingeSide.LEFT).setValue(DoorBlock.OPEN, true), (variant) -> variant.model(modelLocation + "_top_hinge")));
	}
}
