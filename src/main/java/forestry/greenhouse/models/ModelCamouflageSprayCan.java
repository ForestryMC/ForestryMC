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
package forestry.greenhouse.models;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.models.BlankModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.models.ModelManager;
import forestry.core.models.TRSRBakedModel;
import forestry.greenhouse.inventory.ItemInventoryCamouflageSprayCan;

@SideOnly(Side.CLIENT)
public class ModelCamouflageSprayCan extends BlankModel {

	public ModelCamouflageSprayCan() {
	}

	private class SprayCanOverrideList extends ItemOverrideList {

		public SprayCanOverrideList() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			IModel basicModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("forestry:item/camouflage_spray_can"));
			IBakedModel model = basicModel.bake(basicModel.getDefaultState(), DefaultVertexFormats.ITEM, DefaultTextureGetter.INSTANCE);
			IBakedModel filledModel = basicModel.bake(basicModel.getDefaultState(), DefaultVertexFormats.ITEM, new DefaultTextureGetter());
			IBakedModel camouflageModel = null;

			if (stack.getTagCompound() != null && entity instanceof EntityPlayer) {
				ItemInventoryCamouflageSprayCan inventory = new ItemInventoryCamouflageSprayCan((EntityPlayer) entity, stack);
				ItemStack camouflage = inventory.getStackInSlot(0);
				if (!camouflage.isEmpty()) {
					IModel filledBasicModel = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation("forestry:item/camouflage_spray_can_filled"));
					filledModel = filledBasicModel.bake(filledBasicModel.getDefaultState(), DefaultVertexFormats.ITEM, new DefaultTextureGetter());

					camouflageModel = new TRSRBakedModel(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(camouflage), -0.0625F * 3, 0.0625F * 3, 0.0625F, 0.5F);
				}
			}
			return new SprayCanModel(model, filledModel, camouflageModel, ModelManager.getInstance().getDefaultItemState());
		}

	}

	private class SprayCanModel extends BlankModel implements IPerspectiveAwareModel {

		private final IBakedModel originalModel;
		@Nullable
		private final IBakedModel camouflageModel;
		private final IModelState state;
		private final IBakedModel filledModel;

		public SprayCanModel(IBakedModel originalModel, IBakedModel filledModel, @Nullable IBakedModel camouflageModel, IModelState state) {
			this.originalModel = originalModel;
			this.filledModel = filledModel;
			this.camouflageModel = camouflageModel;
			this.state = state;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
			List<BakedQuad> quads = new ArrayList<>();
			quads.addAll(filledModel.getQuads(state, side, rand));
			if (camouflageModel != null) {
				quads.addAll(camouflageModel.getQuads(state, side, rand));
			}
			return quads;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			if (cameraTransformType == TransformType.GUI) {
				return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, state, cameraTransformType);
			}
			return IPerspectiveAwareModel.MapWrapper.handlePerspective(originalModel, state, cameraTransformType);
		}

	}	@Override
	protected ItemOverrideList createOverrides() {
		return new SprayCanOverrideList();
	}




}
