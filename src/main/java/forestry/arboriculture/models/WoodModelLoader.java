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
package forestry.arboriculture.models;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition.MissingVariantException;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.config.Constants;
import forestry.core.utils.ModelUtil;

@SideOnly(Side.CLIENT)
public enum WoodModelLoader implements ICustomModelLoader {
	INSTANCE;

	public static final List<String> validFiles = new ArrayList<>();

	static {
		validFiles.add("door");
		validFiles.add("double_slab");
		validFiles.add("fence");
		validFiles.add("fence_gate");
		validFiles.add("log");
		validFiles.add("planks");
		validFiles.add("slab");
		validFiles.add("stairs");
	}

	// NOOP, handled in loader
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if (!(modelLocation instanceof ModelResourceLocation)
			|| !modelLocation.getNamespace().equals(Constants.MOD_ID)
			|| !modelLocation.getPath().contains("arboriculture")) {
			return false;
		}
		String path = modelLocation.getPath();
		for (String validFile : validFiles) {
			if (path.endsWith(validFile)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		ModelResourceLocation variant = (ModelResourceLocation) modelLocation;
		ModelBlockDefinition definition = ModelUtil.getModelBlockDefinition(variant);
		try {
			VariantList variants = definition.getVariant(variant.getVariant());
			return new SimpleModel(variant, variants);
		} catch (MissingVariantException e) {
			if (definition.hasMultipartData()) {
				return new MultipartModel(new ResourceLocation(variant.getNamespace(), variant.getPath()),
					definition.getMultipartData());
			}
			throw e;
		}
	}
}
