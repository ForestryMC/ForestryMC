package forestry.core.genetics.root;

import javax.annotation.Nullable;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;

public class ServerBreedingHandler implements BreedingTrackerManager.SidedHandler {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile player) {
		IBreedingTrackerHandler handler = BreedingTrackerManager.factories.get(rootUID);
		String filename = handler.getFileName(player);
		ServerWorld overworld = ((ServerWorld) world).getServer().getLevel(World.OVERWORLD);
		T tracker = (T) overworld.getDataStorage().computeIfAbsent(() -> (WorldSavedData) handler.createTracker(filename), filename);
		handler.populateTracker(tracker, overworld, player);
		return tracker;
	}
}
