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

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.MultipartBakedModel;
import net.minecraft.client.renderer.block.model.multipart.Multipart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultipartModel implements IRetexturableModel {

	private final ResourceLocation location;
	private final Multipart multipart;
	private final ImmutableMap<Selector, IModel> partModels;

	public MultipartModel(ResourceLocation location, Multipart multipart) throws Exception {
		this.location = location;
		this.multipart = multipart;
		ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
		for (Selector selector : multipart.getSelectors()) {
			builder.put(selector, new SimpleModel(location, selector.getVariantList()));
		}
		partModels = builder.build();
	}

	private MultipartModel(ResourceLocation location, Multipart multipart, ImmutableMap<Selector, IModel> partModels)
			throws Exception {
		this.location = location;
		this.multipart = multipart;
		this.partModels = partModels;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableSet.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableSet.of();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
							Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();

		for (Selector selector : multipart.getSelectors()) {
			IModel model = partModels.get(selector);
			IBakedModel bakedModel = model.bake(model.getDefaultState(), format, bakedTextureGetter);
			builder.putModel(selector.getPredicate(multipart.getStateContainer()), bakedModel);
		}

		return builder.makeMultipartModel();
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		try {
			ImmutableMap.Builder<Selector, IModel> builder = ImmutableMap.builder();
			for (Selector selector : multipart.getSelectors()) {
				IModel model = new SimpleModel(location, selector.getVariantList());
				model = ModelProcessingHelper.retexture(model, textures);
				builder.put(selector, model);
			}
			return new MultipartModel(location, multipart, builder.build());
		} catch (Exception e) {
			return this;
		}
	}

	public Multipart getMultipart() {
		return multipart;
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

}
