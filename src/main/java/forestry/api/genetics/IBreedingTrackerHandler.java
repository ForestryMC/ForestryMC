package forestry.api.genetics;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IBreedingTrackerHandler {

    String getFileName(@Nullable GameProfile profile);

    IBreedingTracker createTracker(String fileName);

    void populateTracker(IBreedingTracker tracker, @Nullable World world, @Nullable GameProfile profile);

}
