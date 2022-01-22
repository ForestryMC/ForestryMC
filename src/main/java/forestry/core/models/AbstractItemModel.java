package forestry.core.models;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractItemModel extends AbstractBakedModel {

	@Override
	protected ItemOverrides createOverrides() {
		return new OverrideList();
	}

	/**
	 * A override is complex if the returned model needs a override too.
	 */
	protected boolean complexOverride() {
		return false;
	}

	protected abstract BakedModel getOverride(BakedModel model, ItemStack stack);

	protected BakedModel getOverride(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
		return getOverride(model, stack);
	}


	private class OverrideList extends ItemOverrides {
		public OverrideList() {
			super();
		}

		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int p_173469_) {
			BakedModel overrideModel = getOverride(model, stack, world, entity);
			return complexOverride() ? overrideModel.getOverrides().resolve(overrideModel, stack, world, entity, p_173469_) : overrideModel;
		}
	}
}
