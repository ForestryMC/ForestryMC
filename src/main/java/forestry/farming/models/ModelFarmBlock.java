package forestry.farming.models;

import forestry.core.models.ModelBlockCached;
import forestry.core.models.baker.ModelBaker;
import forestry.farming.blocks.BlockFarm;
import forestry.farming.blocks.BlockFarm.State;
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
import org.apache.commons.lang3.tuple.Pair;

@OnlyIn(Dist.CLIENT)
public class ModelFarmBlock extends ModelBlockCached<BlockFarm, Pair<BlockFarm, State>> {
    public ModelFarmBlock() {
        super(BlockFarm.class);
    }

    @Override
    protected Pair<BlockFarm, State> getInventoryKey(ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (block instanceof BlockFarm) {
            return Pair.of((BlockFarm) block, State.PLAIN);
        }

        return Pair.of(
                FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block(),
                State.PLAIN
        );
    }

    @Override
    protected Pair<BlockFarm, State> getWorldKey(BlockState state, IModelData extraData) {
        Block block = state.getBlock();
        if (block instanceof BlockFarm) {
            return Pair.of((BlockFarm) block, state.get(BlockFarm.STATE));
        }

        return Pair.of(
                FarmingBlocks.FARM.get(EnumFarmBlockType.PLAIN, EnumFarmMaterial.BRICK).block(),
                State.PLAIN
        );
    }

    @Override
    protected void bakeBlock(
            BlockFarm blockFarm,
            IModelData extraData,
            Pair<BlockFarm, State> key,
            ModelBaker baker,
            boolean inventory
    ) {
        EnumFarmBlockType type = key.getLeft().getType();
        EnumFarmMaterial material = key.getLeft().getFarmMaterial();
        TextureAtlasSprite[] textures = material.getSprites();

        // Add the plain block.
        baker.addBlockModel(textures, 0);

        // Add the overlay block.
        baker.addBlockModel(type.getSprites(), 0);

        // Add band if plain block, since this is the only farm block type that can be used on layer 2
        if (type == EnumFarmBlockType.PLAIN && key.getRight() == State.BAND) {
            baker.addBlockModel(EnumFarmBlockType.getBandSprites(), 0);
        }

        // Set the particle sprite
        baker.setParticleSprite(textures[0]);
    }

    @Override
    public boolean isSideLit() {
        return false;
    }
}
