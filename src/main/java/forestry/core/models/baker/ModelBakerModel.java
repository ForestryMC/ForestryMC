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
package forestry.core.models.baker;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBakerModel implements IBakedModel {
	protected List<BakedQuad>[] faces = new List[6];
	protected List<BakedQuad> general;

	public ModelBakerModel() {
		general = new ArrayList<BakedQuad>();
		for (EnumFacing f : EnumFacing.VALUES)
			faces[f.ordinal()] = new ArrayList<BakedQuad>();
	}

	private ModelBakerModel(List<BakedQuad> general, List<BakedQuad>[] faces) {
		this.general = general;
		this.faces = faces;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return getTransform();
	}

	public ItemCameraTransforms getTransform() {
		Vector3f rotation = new Vector3f(getRotation()[0], getRotation()[1], getRotation()[2]);
		Vector3f translation = new Vector3f(getTranslation()[0], getTranslation()[1], getTranslation()[2]);
		translation.scale(0.0625F);
		MathHelper.clamp_double(translation.x, -1.5D, 1.5D);
		MathHelper.clamp_double(translation.y, -1.5D, 1.5D);
		MathHelper.clamp_double(translation.z, -1.5D, 1.5D);
		Vector3f scale = new Vector3f(getScale()[0], getScale()[1], getScale()[2]);
		MathHelper.clamp_double(scale.x, -1.5D, 1.5D);
		MathHelper.clamp_double(scale.y, -1.5D, 1.5D);
		MathHelper.clamp_double(scale.z, -1.5D, 1.5D);

		new ItemTransformVec3f(rotation, translation, scale);
		ItemTransformVec3f transformV = new ItemTransformVec3f(rotation, translation, scale);
		return new ItemCameraTransforms(transformV, ItemCameraTransforms.DEFAULT.firstPerson, ItemCameraTransforms.DEFAULT.head, ItemCameraTransforms.DEFAULT.gui, ItemCameraTransforms.DEFAULT.ground, ItemCameraTransforms.DEFAULT.fixed);
	}

	protected float[] getRotation() {
		return new float[] { -80, -45, 170 };
	}

	protected float[] getTranslation() {
		return new float[] { 0, 1.5F, -2.75F };
	}

	protected float[] getScale() {
		return new float[] { 0.375F, 0.375F, 0.375F };
	}

	@Override
	public List getGeneralQuads() {
		return general;
	}

	@Override
	public List getFaceQuads(EnumFacing face) {
		return faces[face.ordinal()];
	}

	public ModelBakerModel copy() {
		return new ModelBakerModel(general, faces);
	}
}
