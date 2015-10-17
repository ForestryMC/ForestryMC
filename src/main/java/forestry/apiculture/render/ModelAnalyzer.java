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
package forestry.apiculture.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import forestry.core.proxy.Proxies;
import forestry.core.render.ForestryResource;

public class ModelAnalyzer extends ModelBase {

	private final ModelRenderer pedestal;
	private final ModelRenderer cover;
	private final ModelRenderer tower1;
	private final ModelRenderer tower2;

	private final ResourceLocation[] textures;

	public ModelAnalyzer(String gfxBase) {

		textures = new ResourceLocation[]{
				new ForestryResource(gfxBase + "pedestal.png"),
				new ForestryResource(gfxBase + "tower1.png"),
				new ForestryResource(gfxBase + "tower2.png"),
		};

		pedestal = new ModelRenderer(this, 0, 0);
		pedestal.addBox(-8F, -8F, -8F, 16, 1, 16);
		pedestal.setRotationPoint(8, 8, 8);

		cover = new ModelRenderer(this, 0, 0);
		cover.addBox(-8F, -8F, -8F, 16, 1, 16);
		cover.setRotationPoint(8, 8, 8);

		tower1 = new ModelRenderer(this, 0, 0);
		tower1.addBox(-8, -7, -7, 2, 14, 14);
		tower1.setRotationPoint(8, 8, 8);

		tower2 = new ModelRenderer(this, 0, 0);
		tower2.addBox(6, -7, -7, 2, 14, 14);
		tower2.setRotationPoint(8, 8, 8);

	}

	public void render(ForgeDirection orientation, float posX, float posY, float posZ) {

		GL11.glPushMatrix();

		GL11.glTranslatef(posX, posY, posZ);
		float[] angle = {0, 0, 0};

		if (orientation == null) {
			orientation = ForgeDirection.WEST;
		}
		switch (orientation) {
			case EAST:
				angle[1] = (float) Math.PI / 2;
				break;
			case WEST:
				angle[1] = -(float) Math.PI / 2;
				break;
			case SOUTH:
				break;
			case NORTH:
			default:
				angle[1] = (float) Math.PI;
				break;
		}

		float factor = (float) (1.0 / 16.0);

		Proxies.render.bindTexture(textures[0]);

		pedestal.rotateAngleX = angle[0];
		pedestal.rotateAngleY = angle[1];
		pedestal.rotateAngleZ = angle[2];
		pedestal.render(factor);

		cover.rotateAngleX = angle[0];
		cover.rotateAngleY = angle[1];
		cover.rotateAngleZ = (float) Math.PI;
		cover.render(factor);

		tower1.rotateAngleX = angle[0];
		tower1.rotateAngleY = angle[1];
		tower1.rotateAngleZ = angle[2];
		Proxies.render.bindTexture(textures[1]);
		tower1.render(factor);

		tower2.rotateAngleX = angle[0];
		tower2.rotateAngleY = angle[1];
		tower2.rotateAngleZ = angle[2];
		Proxies.render.bindTexture(textures[2]);
		tower2.render(factor);

		GL11.glPopMatrix();

	}

}
