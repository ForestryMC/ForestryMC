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
package forestry.core.render;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import forestry.core.items.ItemCrated;

/**
 * Adapted from RenderTankCartItem by CovertJaguar <http://www.railcraft.info>
 */
public class RenderCrateItem implements IItemRenderer {

	private static final ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
	private static final ResourceLocation ITEM_TEXTURE = TextureMap.locationItemsTexture;
	private static final ResourceLocation GLINT_TEXTURE = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	private static final float PIXEL = 1.0f / 16.0f;

	private final RenderItem renderItem = new RenderItem();

	@Override
	public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
		switch (type) {
			case INVENTORY:
			case ENTITY:
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.ENTITY_BOBBING;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		if (type == ItemRenderType.INVENTORY) {
			render(ItemRenderType.INVENTORY, stack);
		} else if (type == ItemRenderType.ENTITY) {
			if (RenderManager.instance.options.fancyGraphics) {
				renderAsEntity(stack, (EntityItem) data[1]);
			} else {
				renderAsEntityFlat(stack);
			}
		} else if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			GL11.glPushMatrix();
			renderEquipped(stack);

			ItemStack contained = getContainedFromCrate(stack);

			if (contained != null) {
				float scale = 0.5f;
				GL11.glScalef(scale, scale, 1.1f);
				GL11.glTranslatef(10f * PIXEL, 8f * PIXEL, 0.001f);
				if (contained.getItem() instanceof ItemBlock) {
					renderIn3D(contained);
				} else {
					renderEquipped(contained);
				}
			}
			GL11.glPopMatrix();
		}
	}

	private void renderEquipped(ItemStack stack) {
		GL11.glPushMatrix();
		Tessellator tessellator = Tessellator.instance;

		int meta = stack.getItemDamage();
		for (int pass = 0; pass < stack.getItem().getRenderPasses(meta); ++pass) {
			IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
			if (icon == null) {
				continue;
			}

			if (renderItem.renderWithColor) {
				int color = stack.getItem().getColorFromItemStack(stack, pass);
				float c1 = (float) (color >> 16 & 255) / 255.0F;
				float c2 = (float) (color >> 8 & 255) / 255.0F;
				float c3 = (float) (color & 255) / 255.0F;

				GL11.glColor4f(c1, c2, c3, 1.0F);
			}

			float uv1 = icon.getMinU();
			float uv2 = icon.getMaxU();
			float uv3 = icon.getMinV();
			float uv4 = icon.getMaxV();

			ItemRenderer.renderItemIn2D(tessellator, uv2, uv3, uv1, uv4, icon.getIconWidth(), icon.getIconHeight(), PIXEL);
		}

		GL11.glPopMatrix();
	}

	private void renderAsEntity(ItemStack stack, EntityItem entity) {
		GL11.glPushMatrix();
		byte iterations = 1;
		if (stack.stackSize > 1) {
			iterations = 2;
		}
		if (stack.stackSize > 15) {
			iterations = 3;
		}
		if (stack.stackSize > 31) {
			iterations = 4;
		}

		Random rand = new Random(187L);

		float offsetZ = PIXEL + 0.021875F;

		GL11.glRotatef((((float) entity.age + 1.0F) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.25F, -(offsetZ * (float) iterations / 2.0F));

		for (int count = 0; count < iterations; ++count) {
			if (count > 0) {
				float offsetX = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
				float offsetY = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
				GL11.glTranslatef(offsetX, offsetY, offsetZ);
			} else {
				GL11.glTranslatef(0f, 0f, offsetZ);
			}

			renderIn3D(stack);

			ItemStack contained = getContainedFromCrate(stack);

			if (contained != null) {
				GL11.glPushMatrix();
				float scale = 0.5f;
				GL11.glScalef(scale, scale, 1.1F);
				GL11.glTranslatef(10.0f * PIXEL, 8.0f * PIXEL, 0.003F);
				renderIn3D(contained);
				GL11.glPopMatrix();
			}
		}
		GL11.glPopMatrix();
	}

	private void renderIn3D(ItemStack stack) {
		Tessellator tessellator = Tessellator.instance;
		if (RenderManager.instance.renderEngine == null) {
			return;
		}

		int meta = stack.getItemDamage();
		for (int pass = 0; pass < stack.getItem().getRenderPasses(meta); ++pass) {
			IIcon icon = stack.getItem().getIconFromDamageForRenderPass(meta, pass);
			if (icon == null) {
				continue;
			}

			if (renderItem.renderWithColor) {
				int color = stack.getItem().getColorFromItemStack(stack, pass);
				float c1 = (float) (color >> 16 & 255) / 255.0F;
				float c2 = (float) (color >> 8 & 255) / 255.0F;
				float c3 = (float) (color & 255) / 255.0F;

				GL11.glColor4f(c1, c2, c3, 1.0F);
			}

			float minU = icon.getMinU();
			float maxU = icon.getMaxU();
			float minV = icon.getMinV();
			float maxV = icon.getMaxV();

			if (stack.getItemSpriteNumber() == 0) {
				RenderManager.instance.renderEngine.bindTexture(BLOCK_TEXTURE);
			} else {
				RenderManager.instance.renderEngine.bindTexture(ITEM_TEXTURE);
			}

			ItemRenderer.renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), PIXEL);

			if (stack.hasEffect(pass)) {
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				{
					GL11.glDepthFunc(GL11.GL_EQUAL);
					GL11.glDisable(GL11.GL_LIGHTING);
					RenderManager.instance.renderEngine.bindTexture(GLINT_TEXTURE);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);

					float f13 = 0.76F;
					float f14 = 0.125F;
					float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
					GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);

					GL11.glMatrixMode(GL11.GL_TEXTURE);

					GL11.glPushMatrix();
					{
						GL11.glScalef(f14, f14, f14);
						GL11.glTranslatef(f15, 0.0F, 0.0F);
						GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
						ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, PIXEL);
					}
					GL11.glPopMatrix();

					GL11.glPushMatrix();
					{
						GL11.glScalef(f14, f14, f14);
						f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
						GL11.glTranslatef(-f15, 0.0F, 0.0F);
						GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
						ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, PIXEL);
					}
					GL11.glPopMatrix();

					GL11.glMatrixMode(GL11.GL_MODELVIEW);
				}
				GL11.glPopAttrib();
			}
		}
	}

	private void renderAsEntityFlat(ItemStack stack) {
		GL11.glPushMatrix();
		byte iterations = 1;
		if (stack.stackSize > 1) {
			iterations = 2;
		}
		if (stack.stackSize > 15) {
			iterations = 3;
		}
		if (stack.stackSize > 31) {
			iterations = 4;
		}

		Random rand = new Random(187L);

		for (int ii = 0; ii < iterations; ++ii) {
			GL11.glPushMatrix();
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180 - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

			if (ii > 0) {
				float var12 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float var13 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
				float var14 = (rand.nextFloat() * 2.0F - 1.0F) * 0.3F;
				GL11.glTranslatef(var12, var13, var14);
			}

			GL11.glTranslatef(0.5f, 0.8f, 0);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glScalef(1f / 16f, 1f / 16f, 1);

			render(ItemRenderType.ENTITY, stack);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

	private void render(ItemRenderType type, ItemStack stack) {
		GL11.glPushMatrix();
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		IIcon crateTexture = stack.getIconIndex();
		renderItem.renderIcon(0, 0, crateTexture, 16, 16);

		ItemStack contained = getContainedFromCrate(stack);

		if (contained != null) {

			int meta = contained.getItemDamage();

			float scale = 0.5f;
			GL11.glScalef(scale, scale, 1);
			GL11.glTranslatef(6f, 8f, 0);
			if (type == ItemRenderType.ENTITY) {
				GL11.glTranslatef(0, 0, -0.1f);
			}

			if (contained.getItem() instanceof ItemBlock) {
				GL11.glScalef(16f, 16f, 1f);
				GL11.glTranslatef(1f, 1f, 0.1f);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				renderIn3D(contained);
			} else {
				for (int pass = 0; pass < contained.getItem().getRenderPasses(meta); ++pass) {
					IIcon containedTexture = contained.getItem().getIconFromDamageForRenderPass(meta, pass);
					if (containedTexture == null) {
						continue;
					}

					if (renderItem.renderWithColor) {
						int color = contained.getItem().getColorFromItemStack(contained, pass);
						float c1 = (float) (color >> 16 & 255) / 255.0F;
						float c2 = (float) (color >> 8 & 255) / 255.0F;
						float c3 = (float) (color & 255) / 255.0F;

						GL11.glColor4f(c1, c2, c3, 1.0F);
					}

					renderItem.renderIcon(0, 0, containedTexture, 16, 16);
				}
			}
		}

		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}

	private static ItemStack getContainedFromCrate(ItemStack crate) {
		if (crate == null) {
			return null;
		}

		Item crateItem = crate.getItem();
		if (crateItem instanceof ItemCrated) {
			return ((ItemCrated) crateItem).getContained();
		}
		return null;
	}

}