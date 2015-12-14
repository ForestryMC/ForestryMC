package forestry.arboriculture.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import forestry.api.arboriculture.EnumWoodType;
import forestry.arboriculture.items.ItemBlockWood;
import forestry.core.utils.ItemStackUtil;

public class RenderSlabItem implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
			case ENTITY:
			case EQUIPPED_FIRST_PERSON:
			case EQUIPPED:
			case INVENTORY:
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
			case ENTITY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f, true);
				break;
			case EQUIPPED:
			case EQUIPPED_FIRST_PERSON:
				renderItem((RenderBlocks) data[0], item, 0.5f, 0.5f, 0.5f, true);
				break;
			case INVENTORY:
				renderItem((RenderBlocks) data[0], item, 0f, 0f, 0f, false);
				break;
			default:
		}
	}

	private static void renderItem(RenderBlocks renderer, ItemStack itemStack, float x, float y, float z, boolean fullBlock) {
		Tessellator tessellator = Tessellator.instance;
		Block block = ItemStackUtil.getBlock(itemStack);

		if (!(itemStack.getItem() instanceof ItemBlockWood) || block == null) {
			return;
		}

		EnumWoodType woodType = ItemBlockWood.getWoodType(itemStack);

		IIcon plankIcon = IconProviderWood.getPlankIcon(woodType);
		if (plankIcon == null) {
			return;
		}

		GL11.glTranslatef(x, y, z);

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		// top
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
		tessellator.draw();

		// front right
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
		tessellator.draw();

		// front left
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
		tessellator.draw();

		if (fullBlock) {
			// bottom
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, -1.0F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tessellator.draw();

			// back right
			tessellator.startDrawingQuads();
			tessellator.setNormal(0.0F, 0.0F, -1.0F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tessellator.draw();

			// back left
			tessellator.startDrawingQuads();
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, plankIcon);
			tessellator.draw();
		}

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
