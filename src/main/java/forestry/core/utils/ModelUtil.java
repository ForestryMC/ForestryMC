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

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelUtil {

	private static final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();

	public static boolean resourceExists(ResourceLocation location) {
		IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
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
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
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
			return ModelBlock.deserialize(reader).getAllTransforms();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ItemCameraTransforms.DEFAULT;
	}

	private static Reader getReaderForResource(ResourceLocation location) throws IOException {
		ResourceLocation file = new ResourceLocation(location.getNamespace(),
			location.getPath() + ".json");
		IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(file);
		return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
	}

	public static ModelBlockDefinition getModelBlockDefinition(ResourceLocation location) {
		try {
			ResourceLocation resourcelocation = getBlockstateLocation(location);
			return blockDefinitions.computeIfAbsent(resourcelocation,
				k -> loadMultipartMBD(location, resourcelocation));
		} catch (Exception exception) {
			Log.error("Failed to getModelBlockDefinition", exception);
		}
		return new ModelBlockDefinition(new ArrayList<>());
	}

	private static ResourceLocation getBlockstateLocation(ResourceLocation location) {
		return new ResourceLocation(location.getNamespace(),
			"blockstates/" + location.getPath() + ".json");
	}

	private static ModelBlockDefinition loadMultipartMBD(ResourceLocation location, ResourceLocation fileIn) {
		List<ModelBlockDefinition> list = Lists.newArrayList();
		Minecraft mc = Minecraft.getMinecraft();
		IResourceManager manager = mc.getResourceManager();

		try {
			for (IResource resource : manager.getAllResources(fileIn)) {
				list.add(loadModelBlockDefinition(location, resource));
			}
		} catch (IOException e) {
			throw new RuntimeException("Encountered an exception when loading model definition of model " + fileIn, e);
		}

		return new ModelBlockDefinition(list);
	}

	private static ModelBlockDefinition loadModelBlockDefinition(ResourceLocation location, IResource resource) {
		InputStream inputStream = null;
		ModelBlockDefinition definition;

		try {
			inputStream = resource.getInputStream();
			definition = ModelBlockDefinition.parseFromReader(new InputStreamReader(inputStream, Charsets.UTF_8), location);
		} catch (Exception exception) {
			throw new RuntimeException("Encountered an exception when loading model definition of \'" + location
				+ "\' from: \'" + resource.getResourceLocation() + "\' in resourcepack: \'"
				+ resource.getResourcePackName() + "\'", exception);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return definition;
	}
}
