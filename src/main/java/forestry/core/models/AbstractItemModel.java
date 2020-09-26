package forestry.core.models;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public abstract class AbstractItemModel extends AbstractBakedModel {

    @Override
    protected ItemOverrideList createOverrides() {
        return new OverrideList();
    }

    /**
     * A override is complex if the returned model needs a override too.
     */
    protected boolean complexOverride() {
        return false;
    }

    protected abstract IBakedModel getOverride(IBakedModel model, ItemStack stack);

    protected IBakedModel getOverride(
            IBakedModel model,
            ItemStack stack,
            @Nullable ClientWorld world,
            @Nullable LivingEntity entity
    ) {
        return getOverride(model, stack);
    }


    private class OverrideList extends ItemOverrideList {
        public OverrideList() {
            super();
        }

        @Override
        public IBakedModel getOverrideModel(
                IBakedModel model,
                ItemStack stack,
                @Nullable ClientWorld world,
                @Nullable LivingEntity entity
        ) {
            IBakedModel overrideModel = getOverride(model, stack, world, entity);
            return complexOverride() ? overrideModel.getOverrides()
                    .getOverrideModel(overrideModel, stack, world, entity) : overrideModel;
        }
    }
}
