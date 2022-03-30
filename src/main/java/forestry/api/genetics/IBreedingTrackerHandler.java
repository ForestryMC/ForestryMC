package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

public interface IBreedingTrackerHandler {

	String getFileName(@Nullable GameProfile profile);

	IBreedingTracker createTracker();

	IBreedingTracker createTracker(CompoundTag tag);

	void populateTracker(IBreedingTracker tracker, @Nullable Level world, @Nullable GameProfile profile);

}
