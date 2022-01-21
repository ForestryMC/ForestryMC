package forestry.core.genetics.root;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;
import forestry.core.utils.WorldUtils;

public class ServerBreedingHandler implements BreedingTrackerManager.SidedHandler {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBreedingTracker> T getTracker(String rootUID, LevelAccessor world, @Nullable GameProfile player) {
		IBreedingTrackerHandler handler = BreedingTrackerManager.factories.get(rootUID);
		String filename = handler.getFileName(player);
		ServerLevel overworld = WorldUtils.asServer(world).getServer().getLevel(Level.OVERWORLD);
		T tracker = (T) overworld.getDataStorage().computeIfAbsent(tag -> (SavedData) handler.createTracker(tag), () -> (SavedData) handler.createTracker(), filename);
		handler.populateTracker(tracker, overworld, player);
		return tracker;
	}
}
