package forestry.api.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

public interface IBreedingTrackerHandler {

	String getFileName(@Nullable GameProfile profile);

	IBreedingTracker createTracker(String fileName);

	void populateTracker(IBreedingTracker tracker, @Nullable World world, @Nullable GameProfile profile);

}
