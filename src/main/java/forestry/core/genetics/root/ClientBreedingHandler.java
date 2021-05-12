package forestry.core.genetics.root;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;

public class ClientBreedingHandler extends ServerBreedingHandler {
	private final Map<String, IBreedingTracker> trackerByUID = new LinkedHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile profile) {
		if (world instanceof ServerWorld) {
			return super.getTracker(rootUID, world, profile);
		}
		IBreedingTrackerHandler handler = BreedingTrackerManager.factories.get(rootUID);
		String filename = handler.getFileName(profile);
		T tracker = (T) trackerByUID.computeIfAbsent(rootUID, (key) -> handler.createTracker(filename));
		handler.populateTracker(tracker, Minecraft.getInstance().level, profile);
		return tracker;
	}
}
