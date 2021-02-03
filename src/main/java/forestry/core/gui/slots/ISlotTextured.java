package forestry.core.gui.slots;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

import com.mojang.datafixers.util.Pair;

public interface ISlotTextured {
	@Nullable
	Pair<ResourceLocation, ResourceLocation> getBackground();
}
