package forestry.farming.models;

import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.blocks.EnumFarmMaterial;
import forestry.farming.features.FarmingBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public class ModelFarmBlock extends ModelBlockCached<BlockFarm, BlockFarm> {
    public ModelFarmBlock() {
        super(BlockFarm.class);
    }

    @Override
    protected BlockFarm getInventoryKey(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof BlockFarm) {
            return ((BlockFarm) block);
        }
        return FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block();
    }

    @Override
    protected BlockFarm getWorldKey(BlockState state, IModelData extraData) {
        Block block = state.getBlock();
        if (block instanceof BlockFarm) {
            return ((BlockFarm) block);
        }
        return FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block();
    }

    @Override
    protected void bakeBlock(
            BlockFarm blockFarm,
            IModelData extraData,
            BlockFarm key,
            ModelBaker baker,
            boolean inventory
    ) {
        EnumFarmBlockType type = key.getType();
        EnumFarmMaterial material = key.getFarmMaterial();
        TextureAtlasSprite[] textures = material.getSprites();

        // Add the plain block.
        baker.addBlockModel(textures, 0);
        // Add the overlay block.
        baker.addBlockModel(type.getSprites(), 0);

        // Set the particle sprite
        baker.setParticleSprite(textures[0]);
    }

    @Override
    public boolean isSideLit() {
        return false;
    }
}
