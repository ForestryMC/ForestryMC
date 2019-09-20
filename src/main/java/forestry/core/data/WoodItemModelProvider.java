package forestry.core.data;

import java.util.Map;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.blocks.BlockForestryDoor;
import forestry.arboriculture.blocks.BlockForestryFence;
import forestry.arboriculture.blocks.BlockForestryFenceGate;
import forestry.arboriculture.blocks.BlockForestryLog;
import forestry.arboriculture.blocks.BlockForestryPlank;
import forestry.arboriculture.blocks.BlockForestrySlab;
import forestry.arboriculture.blocks.BlockForestryStairs;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.core.config.Constants;
import forestry.modules.features.FeatureBlock;

public class WoodItemModelProvider extends ModelProvider {

	public WoodItemModelProvider(DataGenerator generator) {
		super(generator, "item");
	}

	@Override
	protected void registerModels() {
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS.getFeatureByType().entrySet()) {
			addPlank(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addPlank(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS.getFeatureByType().entrySet()) {
			addLog(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addLog(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS.getFeatureByType().entrySet()) {
			addStair(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addStair(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS.getFeatureByType().entrySet()) {
			addSlab(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addSlab(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES.getFeatureByType().entrySet()) {
			addFence(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_FIREPROOF.getFeatureByType().entrySet()) {
			addFence(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addFence(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF.getFeatureByType().entrySet()) {
			addFenceGate(stair.getValue().item(), stair.getKey());
		}
		for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryDoor, BlockItem>> stair : ArboricultureBlocks.DOORS.getFeatureByType().entrySet()) {
			addDoor(stair.getValue().item(), stair.getKey());
		}
		//Replaced by the model loader later
		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getItems()) {
			registerModel(leaves, new ModelBuilder().parent(new ResourceLocation("forestry:block/leaves")));
		}
		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getItems()) {
			registerModel(leaves, new ModelBuilder().parent(new ResourceLocation("forestry:block/leaves")));
		}
		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DEFAULT.getItems()) {
			registerModel(leaves, new ModelBuilder().parent(new ResourceLocation("forestry:block/leaves")));
		}
	}

	private void addPlank(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/planks/" + type.getName())));
	}

	private void addLog(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/logs/" + type.getName())));
	}

	private void addStair(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/stairs/" + type.getName())));
	}

	private void addSlab(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/slabs/" + type.getName())));
	}

	private void addFence(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/fences/" + type.getName() + "_inventory")));
	}

	private void addFenceGate(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().parent(new ResourceLocation(Constants.MOD_ID, "block/arboriculture/fence_gates/" + type.getName())));
	}

	private void addDoor(BlockItem item, IWoodType type) {
		registerModel(item, new ModelBuilder().item().layer(0, new ResourceLocation(Constants.MOD_ID, "item/doors/" + type.getName())));
	}
}
