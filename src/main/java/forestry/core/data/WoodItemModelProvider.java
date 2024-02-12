package forestry.core.data;

import java.util.Map;

import deleteme.RegistryNameFinder;
import forestry.core.config.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.resources.ResourceLocation;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.blocks.BlockForestryDoor;
import forestry.arboriculture.blocks.BlockForestryFence;
import forestry.arboriculture.blocks.BlockForestryFenceGate;
import forestry.arboriculture.blocks.BlockForestryLog;
import forestry.arboriculture.blocks.BlockForestryPlank;
import forestry.arboriculture.blocks.BlockForestrySlab;
import forestry.arboriculture.blocks.BlockForestryStairs;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.modules.features.FeatureBlock;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class WoodItemModelProvider extends ItemModelProvider {

	public WoodItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, Constants.MOD_ID, existingFileHelper);
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

		// Replaced by the model loader later
		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DECORATIVE.getItems()) {
			withExistingParent(RegistryNameFinder.getRegistryName(leaves).getPath(), new ResourceLocation(Constants.MOD_ID, "block/leaves"));
		}

		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DEFAULT_FRUIT.getItems()) {
			withExistingParent(RegistryNameFinder.getRegistryName(leaves).getPath(), new ResourceLocation(Constants.MOD_ID, "block/leaves"));
		}

		for (BlockItem leaves : ArboricultureBlocks.LEAVES_DEFAULT.getItems()) {
			withExistingParent(RegistryNameFinder.getRegistryName(leaves).getPath(), new ResourceLocation(Constants.MOD_ID, "block/leaves"));
		}
	}

	private String getLocation(IWoodType type, WoodBlockKind kind) {
		String location;
		if (type instanceof EnumVanillaWoodType) {
			location = "block/" + type.getSerializedName() + "_" + kind.getSerializedName();
		} else if (kind == WoodBlockKind.DOOR) {
			location = "forestry:item/doors/" + type.getSerializedName();
		} else {
			String kindName = kind.getSerializedName();
			if (!kindName.endsWith("s")) {
				kindName = kindName + "s";
			}
			location = "forestry:block/arboriculture/" + kindName + "/" + type.getSerializedName();
		}
		return location;
	}

	private void addPlank(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.PLANKS)));
	}

	private void addLog(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.LOG)));
	}

	private void addStair(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.STAIRS)));
	}

	private void addSlab(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.SLAB)));
	}

	private void addFence(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.FENCE) + "_inventory"));
	}

	private void addFenceGate(BlockItem item, IWoodType type) {
		withExistingParent(RegistryNameFinder.getRegistryName(item).getPath(), new ResourceLocation(getLocation(type, WoodBlockKind.FENCE_GATE)));
	}

	private void addDoor(BlockItem item, IWoodType type) {
		singleTexture(RegistryNameFinder.getRegistryName(item).getPath(), mcLoc("item/generated"), "layer0", new ResourceLocation(getLocation(type, WoodBlockKind.DOOR)));
	}
}
