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

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.apiculture.entities.EntityBee;
import forestry.core.proxy.Proxies;

public class RenderBeeItem implements IItemRenderer {

	private EntityBee entity;

	private static float getWingYaw(IBee bee) {
		float wingYaw = 1f;

		if (bee.isAlive()) {
			long systemTime = System.currentTimeMillis();
			long flapping = systemTime + bee.getIdent().hashCode();
			float flap = (float) (flapping % 1000) / 1000;   // 0 to 1

			wingYaw = getIrregularWingYaw(flapping, flap);

		}

		return wingYaw;
	}

	private static float getIrregularWingYaw(long flapping, float flap) {
		long irregular = flapping / 1000;
		float wingYaw;

		if (irregular % 11 == 0) {
			wingYaw = 0.75f;
		} else {
			if (irregular % 7 == 0 || irregular % 19 == 0) {
				flap *= 8;
				flap = flap % 1;
			}
			wingYaw = getRegularWingYaw(flap);
		}

		return wingYaw;
	}

	private static float getRegularWingYaw(float flap) {
		return flap < 0.5 ? 0.75f + flap : 1.75f - flap;
	}

	private IBee initBee(ItemStack item, boolean scaled) {
		IBee bee = BeeManager.beeRoot.getMember(item);
		if (bee == null) {
			bee = BeeManager.beeRoot.templateAsIndividual(BeeManager.beeRoot.getDefaultTemplate());
		}

		if (entity == null) {
			entity = new EntityBee(Proxies.common.getClientInstance().theWorld);
		}
		entity.setSpecies(bee.getGenome().getPrimary());
		entity.setType(BeeManager.beeRoot.getType(item));
		/*
		if(scaled)
			entity.setScale(butterfly.getSize());
		else
			entity.setScale(EntityButterfly.DEFAULT_BUTTERFLY_SIZE);
		 */

		return bee;
	}

	/*private void renderBeeHalo() {

		IIcon background = TextureManager.getInstance().getDefault("habitats/desert");
		float xPos = 1.0f; float yPos = 1.0f;
		float width = 1f; float height = 1f;

		Proxies.render.bindTexture(SpriteSheet.ITEMS);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(xPos + 0), (double)(yPos + height), 0f, (double)background.getMinU(), (double)background.getMaxV());
        tessellator.addVertexWithUV((double)(xPos + width), (double)(yPos + height), 0f, (double)background.getMaxU(), (double)background.getMaxV());
        tessellator.addVertexWithUV((double)(xPos + width), (double)(yPos + 0), 0f, (double)background.getMaxU(), (double)background.getMinV());
        tessellator.addVertexWithUV((double)(xPos + 0), (double)(yPos + 0), 0f, (double)background.getMinU(), (double)background.getMinV());
        tessellator.draw();
	}*/

	private void renderBeeItem(IBee bee, float translateX, float translateY, float translateZ) {
		float yaw = 1;
		float pitch = 1;

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glPushMatrix();

		GL11.glScalef(2.0f, 2.0f, 2.0f);
		GL11.glTranslatef(translateX, translateY, translateZ);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float) Math.atan(pitch / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);

		entity.renderYawOffset = (float) Math.atan(yaw / 40.0F) * 20.0F;
		entity.rotationYaw = (float) Math.atan(yaw / 40.0F) * 40.0F;
		entity.rotationPitch = -((float) Math.atan(pitch / 40.0F)) * 20.0F;
		entity.rotationYawHead = entity.rotationYaw;

		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, getWingYaw(bee));

		GL11.glPopMatrix();

		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glPopAttrib();

	}

	private void renderBeeInInventory(IBee bee) {

		/*
		GL11.glPushMatrix();
        //GL11.glTranslatef(-0.3f, -2.5f, 0f);
        GL11.glScalef(-1.0f, 1.0f, 1.0f);
        //GL11.glScalef((float)Math.PI / 2, 1.0f, 1.0f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(-((float) Math.atan((double) (1 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        renderBeeHalo();
        GL11.glPopMatrix();
		 */

		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		RenderHelper.enableStandardItemLighting();

		GL11.glPushMatrix();

		GL11.glTranslatef(-0.3f, -2.5f, 0f);
		GL11.glScalef(-3.0f, 3.0f, 3.0f);
		GL11.glRotatef(32.0F, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(1.6f, 1f, 1f);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		entity.renderYawOffset = 0;
		entity.rotationYaw = 0;
		entity.rotationPitch = 0;
		entity.rotationYawHead = entity.rotationYaw;

		RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, getWingYaw(bee));

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
				return true;
			case EQUIPPED:
				return true;
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
				renderBeeItem(initBee(item, true), 0f, 0f, 0f);
				break;
			case EQUIPPED:
				renderBeeItem(initBee(item, true), 1.0f, 0f, 0.5f);
				break;
			case INVENTORY:
				renderBeeInInventory(initBee(item, false));
				break;
			default:
		}
	}

}
