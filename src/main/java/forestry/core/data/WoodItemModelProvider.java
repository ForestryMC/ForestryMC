package forestry.core.data;

import forestry.api.arboriculture.EnumForestryWoodType;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.blocks.*;
import forestry.arboriculture.features.ArboricultureBlocks;
import forestry.modules.features.FeatureBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class WoodItemModelProvider extends ModelProvider {

    public WoodItemModelProvider(DataGenerator generator) {
        super(generator, "item");
    }

    @Override
    protected void registerModels() {
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS
                .getFeatureByType()
                .entrySet()) {
            addPlank(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addPlank(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryPlank, BlockItem>> stair : ArboricultureBlocks.PLANKS_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addPlank(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS
                .getFeatureByType()
                .entrySet()) {
            addLog(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addLog(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryLog, BlockItem>> stair : ArboricultureBlocks.LOGS_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addLog(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS
                .getFeatureByType()
                .entrySet()) {
            addStair(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addStair(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryStairs, BlockItem>> stair : ArboricultureBlocks.STAIRS_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addStair(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS
                .getFeatureByType()
                .entrySet()) {
            addSlab(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addSlab(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestrySlab, BlockItem>> stair : ArboricultureBlocks.SLABS_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addSlab(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES
                .getFeatureByType()
                .entrySet()) {
            addFence(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addFence(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFence, BlockItem>> stair : ArboricultureBlocks.FENCES_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addFence(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES
                .getFeatureByType()
                .entrySet()) {
            addFenceGate(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addFenceGate(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumVanillaWoodType, FeatureBlock<BlockForestryFenceGate, BlockItem>> stair : ArboricultureBlocks.FENCE_GATES_VANILLA_FIREPROOF
                .getFeatureByType()
                .entrySet()) {
            addFenceGate(stair.getValue().item(), stair.getKey());
        }
        for (Map.Entry<EnumForestryWoodType, FeatureBlock<BlockForestryDoor, BlockItem>> stair : ArboricultureBlocks.DOORS
                .getFeatureByType()
                .entrySet()) {
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

    private String getLocation(IWoodType type, WoodBlockKind kind) {
        String location;
        if (type instanceof EnumVanillaWoodType) {
            location = "block/" + type.getString() + "_" + kind.getString();
        } else if (kind == WoodBlockKind.DOOR) {
            location = "forestry:item/doors/" + type.getString();
        } else {
            String kindName = kind.getString();
            if (!kindName.endsWith("s")) {
                kindName = kindName + "s";
            }
            location = "forestry:block/arboriculture/" + kindName + "/" + type.getString();
        }
        return location;
    }

    private void addPlank(BlockItem item, IWoodType type) {
        registerModel(item, new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.PLANKS))));
    }

    private void addLog(BlockItem item, IWoodType type) {
        registerModel(item, new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.LOG))));
    }

    private void addStair(BlockItem item, IWoodType type) {
        registerModel(item, new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.STAIRS))));
    }

    private void addSlab(BlockItem item, IWoodType type) {
        registerModel(item, new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.SLAB))));
    }

    private void addFence(BlockItem item, IWoodType type) {
        registerModel(
                item,
                new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.FENCE) + "_inventory"))
        );
    }

    private void addFenceGate(BlockItem item, IWoodType type) {
        registerModel(
                item,
                new ModelBuilder().parent(new ResourceLocation(getLocation(type, WoodBlockKind.FENCE_GATE)))
        );
    }

    private void addDoor(BlockItem item, IWoodType type) {
        registerModel(
                item,
                new ModelBuilder().item().layer(0, new ResourceLocation(getLocation(type, WoodBlockKind.DOOR)))
        );
    }
}
