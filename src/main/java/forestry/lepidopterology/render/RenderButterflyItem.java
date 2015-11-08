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
package forestry.lepidopterology.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import forestry.api.lepidopterology.ButterflyManager;
import forestry.api.lepidopterology.IButterfly;
import forestry.core.proxy.Proxies;
import forestry.lepidopterology.entities.EntityButterfly;

public class RenderButterflyItem implements IItemRenderer {

	private final ModelButterfly model;
	private EntityButterfly entity;
	
	public RenderButterflyItem() {
		model = new ModelButterfly();
	}
	
	private static float getWingYaw(IButterfly butterfly) {
		float wingYaw = 1f;

		if (butterfly.isAlive()) {
			long systemTime = System.currentTimeMillis();
			long flapping = systemTime + butterfly.getIdent().hashCode();
			float flap = (float) (flapping % 1024) / 1024;   // 0 to 1

			wingYaw = getIrregularWingYaw(flapping, flap);

		}

		return wingYaw;
	}
	
	public static float getIrregularWingYaw(long flapping, float flap) {
		long irregular = flapping / 1024;
		float wingYaw;
		
		if (irregular % 11 == 0) {
			wingYaw = 0.75f;
		} else {
			if (irregular % 7 == 0) {
				flap *= 4;
				flap = flap % 1;
			} else if (irregular % 19 == 0) {
				flap *= 6;
				flap = flap % 1;
			}
			wingYaw = getRegularWingYaw(flap);
		}
		
		return wingYaw;
	}
	
	private static float getRegularWingYaw(float flap) {
		return flap < 0.5 ? 0.75f + flap : 1.75f - flap;
	}
	
	private IButterfly initButterfly(ItemStack item) {
		IButterfly butterfly = ButterflyManager.butterflyRoot.getMember(item);
		if (butterfly == null) {
			butterfly = ButterflyManager.butterflyRoot.templateAsIndividual(ButterflyManager.butterflyRoot.getDefaultTemplate());
		}
		
		if (entity == null) {
			entity = new EntityButterfly(Proxies.common.getRenderWorld(), butterfly);
		} else {
			entity.setIndividual(butterfly);
		}
		
		return butterfly;
	}
	
	private void renderButterflyItem(IButterfly butterfly, float translateX, float translateY, float translateZ) {
		float yaw = 1;
		float pitch = 1;
		
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glPushMatrix();

		if (RenderItem.renderInFrame) {
			//GL11.glScalef(-2.0f, 2.0f, 2.0f);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(1.1f, 1f, 1f);

			GL11.glTranslatef(0, -0.7f, 0.2f);

			entity.renderYawOffset = 0;
			entity.rotationYaw = 0;
			entity.rotationPitch = 0;
			entity.rotationYawHead = entity.rotationYaw;

			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0f);

		} else {
			GL11.glTranslatef(translateX, translateY, translateZ);

			GL11.glScalef(-2.0f, 2.0f, 2.0f);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-((float) Math.atan((double) (pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

			entity.renderYawOffset = (float) Math.atan((double) (yaw / 40.0F)) * 20.0F;
			entity.rotationYaw = (float) Math.atan((double) (yaw / 40.0F)) * 40.0F;
			entity.rotationPitch = -((float) Math.atan((double) (pitch / 40.0F))) * 20.0F;
			entity.rotationYawHead = entity.rotationYaw;

			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, getWingYaw(butterfly));

		}

		GL11.glPopMatrix();

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glPopAttrib();

	}
	
	private void renderButterflyInInventory(IButterfly butterfly) {

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		RenderHelper.enableStandardItemLighting();
		GL11.glPushMatrix();

		GL11.glTranslatef(0f, -0.25f, 0f);
		GL11.glScalef(-2.0f, 2.0f, 2.0f);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-35.0F, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(1.1f, 1f, 1f);

		entity.renderYawOffset = 0;
		entity.rotationYaw = 0;
		entity.rotationPitch = 0;
		entity.rotationYawHead = entity.rotationYaw;

		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, getWingYaw(butterfly));

		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glPopAttrib();

	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
			case ENTITY:
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
			case INVENTORY:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		switch (helper) {
			case ENTITY_BOBBING:
			case ENTITY_ROTATION:
				return false;
			default:
				return true;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

		switch (type) {
			case ENTITY:
				renderButterflyItem(initButterfly(item), 0f, -0.5f, 0f);
				break;
			case EQUIPPED:
				renderButterflyItem(initButterfly(item), 0.5f, -0.9f, 1.0f);
				break;
			case EQUIPPED_FIRST_PERSON:
				renderButterflyItem(initButterfly(item), 0.5f, -0.9f, 1.0f);
				break;
			case INVENTORY:
				renderButterflyInInventory(initButterfly(item));
				break;
			default:
		}
	}

}
