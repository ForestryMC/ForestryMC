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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;

import forestry.core.proxy.Proxies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel.MapWrapper;

public class ModelUtil {

	/**
	 * @return The model from the item of the stack.
	 */
	@Nullable
	public static IBakedModel getModel(ItemStack stack) {
		RenderItem renderItem = Proxies.common.getClientInstance().getRenderItem();
		if(renderItem == null || renderItem.getItemModelMesher() == null){
			return null;
		}
		return renderItem.getItemModelMesher().getItemModel(stack);
	}
	
	public static SimpleModelState loadModelState(ResourceLocation location) {
		return new SimpleModelState(MapWrapper.getTransforms(loadTransformFromJson(location)));
	}

	private static ItemCameraTransforms loadTransformFromJson(ResourceLocation location) {
		try {
			return ModelBlock.deserialize(getReaderForResource(location)).getAllTransforms();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ItemCameraTransforms.DEFAULT;
	}
	
	private static Reader getReaderForResource(ResourceLocation location) throws IOException {
		ResourceLocation file = new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".json");
		IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(file);
		return new BufferedReader(new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8));
	}
	
}
