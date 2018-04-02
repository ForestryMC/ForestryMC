package forestry.core.utils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceUtil {
	private ResourceUtil() {
	}

	@Nullable
	public static IResource getResource(ResourceLocation location) {
		try {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			return resourceManager.getResource(location);
		} catch (IOException e) {
			return null;
		}
	}

	public static List<IResource> getResources(ResourceLocation location) {
		try {
			IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
			return resourceManager.getAllResources(location);
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	public static boolean resourceExists(ResourceLocation location) {
		return getResource(location) != null;
	}
}
