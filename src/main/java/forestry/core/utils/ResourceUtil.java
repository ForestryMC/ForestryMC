package forestry.core.utils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceUtil {
	private ResourceUtil() {
	}

	public static BufferedReader createReader(IResource resource) {
		return new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
	}

	@Nullable
	public static IResource getResource(ResourceLocation location) {
		try {
			IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			return resourceManager.getResource(location);
		} catch (IOException e) {
			return null;
		}
	}

	public static List<IResource> getResources(ResourceLocation location) {
		try {
			IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			return resourceManager.getAllResources(location);
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}
}
