/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.utils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockModelDefinition;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;

@OnlyIn(Dist.CLIENT)
public class ModelUtil {

	private static final Map<ResourceLocation, BlockModelDefinition> blockDefinitions = Maps.newHashMap();

	public static boolean resourceExists(ResourceLocation location) {
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		try {
			resourceManager.getResource(location);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * @return The model from the item of the stack.
	 */
	@Nullable
	public static IBakedModel getModel(ItemStack stack) {
		ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
		if (renderItem == null || renderItem.getItemModelMesher() == null) {
			return null;
		}
		return renderItem.getItemModelMesher().getItemModel(stack);
	}

	public static SimpleModelState loadModelState(ResourceLocation location) {
		return new SimpleModelState(PerspectiveMapWrapper.getTransforms(loadTransformFromJson(location)));
	}

	private static ItemCameraTransforms loadTransformFromJson(ResourceLocation location) {
		try (Reader reader = getReaderForResource(location)) {
			return BlockModel.deserialize(reader).getAllTransforms();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ItemCameraTransforms.DEFAULT;
	}

	private static Reader getReaderForResource(ResourceLocation location) throws IOException {
		ResourceLocation file = new ResourceLocation(location.getNamespace(),
			location.getPath() + ".json");
		IResource iresource = Minecraft.getInstance().getResourceManager().getResource(file);
		return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
	}

	public static BlockModelDefinition getModelBlockDefinition(ResourceLocation location) {
		try {
			ResourceLocation resourcelocation = getBlockstateLocation(location);
			return blockDefinitions.computeIfAbsent(resourcelocation,
				k -> loadMultipartMBD(location, resourcelocation));
		} catch (Exception exception) {
			Log.error("Failed to getModelBlockDefinition", exception);
		}
		return new BlockModelDefinition(new ArrayList<>());
	}

	private static ResourceLocation getBlockstateLocation(ResourceLocation location) {
		return new ResourceLocation(location.getNamespace(),
			"blockstates/" + location.getPath() + ".json");
	}

	private static BlockModelDefinition loadMultipartMBD(ResourceLocation location, ResourceLocation fileIn) {
		List<BlockModelDefinition> list = Lists.newArrayList();
		Minecraft mc = Minecraft.getInstance();
		IResourceManager manager = mc.getResourceManager();

		try {
			for (IResource resource : manager.getAllResources(fileIn)) {
				list.add(loadModelBlockDefinition(location, resource));
			}
		} catch (IOException e) {
			throw new RuntimeException("Encountered an exception when loading model definition of model " + fileIn, e);
		}

		return new BlockModelDefinition(list);
	}

	//TODO - how to load from arbitary stream now, then can uncomment
	private static BlockModelDefinition loadModelBlockDefinition(ResourceLocation location, IResource resource) {
		InputStream inputStream = null;
		BlockModelDefinition definition;

		try {
			inputStream = resource.getInputStream();
			definition = BlockModelDefinition.fromJson(new BlockModelDefinition.ContainerHolder(), new InputStreamReader(inputStream, Charsets.UTF_8), location);
		} catch (Exception exception) {
			throw new RuntimeException("Encountered an exception when loading model definition of \'" + location
				+ "\' from: \'" + resource.getLocation() + "\' in resourcepack: \'"
				+ resource.getPackName() + "\'", exception);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return definition;
	}
}
