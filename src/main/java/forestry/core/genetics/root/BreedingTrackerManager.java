package forestry.core.genetics.root;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.DistExecutor;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IBreedingTrackerHandler;
import forestry.api.genetics.IBreedingTrackerManager;

public enum BreedingTrackerManager implements IBreedingTrackerManager {
	INSTANCE;

	BreedingTrackerManager() {
		sidedHandler = DistExecutor.runForDist(() -> () -> new ClientHandler(), () -> () -> new ServerHandler());
	}

	private static final Map<String, IBreedingTrackerHandler> factories = new LinkedHashMap<>();

	@Nullable
	private SidedHandler sidedHandler = null;

	@Override
	public void registerTracker(String rootUID, IBreedingTrackerHandler handler) {
		factories.put(rootUID, handler);
	}

	@Override
	public <T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile profile) {
		return getSidedHandler().getTracker(rootUID, world, profile);
	}

	private SidedHandler getSidedHandler() {
		Preconditions.checkNotNull(sidedHandler, "Called breeding tracker method to early.");
		return sidedHandler;
	}

	private interface SidedHandler {
		<T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile player);
	}

	private static class ServerHandler implements SidedHandler {

		@SuppressWarnings("unchecked")
		public <T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile player) {
			IBreedingTrackerHandler handler = factories.get(rootUID);
			String filename = handler.getFileName(player);
			ServerWorld overworld = ((ServerWorld) world).getServer().getWorld(DimensionType.OVERWORLD);
			T tracker = (T) overworld.getSavedData().getOrCreate(() -> (WorldSavedData) handler.createTracker(filename), filename);
			handler.populateTracker(tracker, overworld, player);
			return tracker;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static class ClientHandler extends ServerHandler {
		private final Map<String, IBreedingTracker> trackerByUID = new LinkedHashMap<>();

		@Override
		@SuppressWarnings("unchecked")
		public <T extends IBreedingTracker> T getTracker(String rootUID, IWorld world, @Nullable GameProfile profile) {
			if (world instanceof ServerWorld) {
				return super.getTracker(rootUID, world, profile);
			}
			IBreedingTrackerHandler handler = factories.get(rootUID);
			String filename = handler.getFileName(profile);
			T tracker = (T) trackerByUID.computeIfAbsent(rootUID, (key) -> handler.createTracker(filename));
			handler.populateTracker(tracker, Minecraft.getInstance().world, profile);
			return tracker;
		}
	}
}
