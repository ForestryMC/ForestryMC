package forestry.farming.render;

import forestry.api.core.IModelRenderer;
import forestry.api.core.sprite.ISprite;
import forestry.core.config.ForestryBlock;
import forestry.core.render.OverlayRenderingHandler;
import forestry.core.render.TextureManager;
import forestry.farming.multiblock.BlockFarm;
import forestry.farming.multiblock.EnumFarmBlockTexture;
import forestry.farming.multiblock.TileFarm;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;

public class FarmBlockRenderer extends OverlayRenderingHandler {

	@Override
	public void renderInventory(Block block, ItemStack item, IModelRenderer renderer, ItemRenderType renderType) {
		if (block == null) {
			return;
		}
		
		EnumFarmBlockTexture type = EnumFarmBlockTexture.getFromCompound(item.getTagCompound());
		
		ISprite[] textures = getSprites(type);
		ISprite[] texturesOverlay = getOverlaySprites(item.getItemDamage());

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);

		//GL11.glTranslatef(translateX, translateY, translateZ);

		renderer.renderFaceYNeg(null, textures[0]);
		renderer.renderFaceYNeg(null, texturesOverlay[0]);

		renderer.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(null, textures[1]);
		renderer.renderFaceYPos(null, texturesOverlay[1]);

		renderer.setNormal(0.0F, 0.0F, -1F);
		renderer.renderFaceZNeg(null, textures[2]);
		renderer.renderFaceZNeg(null, texturesOverlay[2]);

		renderer.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(null, textures[3]);
		renderer.renderFaceZPos(null, texturesOverlay[3]);

		renderer.setNormal(-1F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(null, textures[4]);
		renderer.renderFaceXNeg(null, texturesOverlay[4]);

		renderer.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(null, textures[5]);
		renderer.renderFaceXPos(null, texturesOverlay[5]);

		//GL11.glTranslatef(0.5F, 0.5F, 0.5F);

		block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean renderInWorld(Block block, IBlockAccess world, BlockPos pos, IModelRenderer renderer) {
		
		TileFarm farm = (TileFarm) world.getTileEntity(pos);
		
		ISprite[] textures = getSprites(farm.getFarmBlockTexture());
		ISprite[] texturesOverlay = getOverlaySprites(farm.getBlockMetadata());
		
		// Render the plain block.
		renderer.renderStandardBlock(block, pos, textures);
		renderFarmOverlay(world, (BlockFarm) ForestryBlock.farm.block(), pos, renderer, texturesOverlay);

		return true;
	}
	
	private static void renderFarmOverlay(IBlockAccess world, BlockFarm block, BlockPos pos, IModelRenderer renderer, ISprite[] texturesOverlay) {
		int mixedBrightness = block.getMixedBrightnessForBlock(world, pos);

		float adjR = 0.5f;
		float adjG = 0.5f;
		float adjB = 0.5f;

		// Bottom
		renderBottomFace(world, block, pos, renderer, texturesOverlay[0], mixedBrightness, adjR, adjG, adjB);
		renderTopFace(world, block, pos, renderer, texturesOverlay[1], mixedBrightness, adjR, adjG, adjB);
		renderEastFace(world, block, pos, renderer, texturesOverlay[2], mixedBrightness, adjR, adjG, adjB);
		renderWestFace(world, block, pos, renderer, texturesOverlay[3], mixedBrightness, adjR, adjG, adjB);
		renderNorthFace(world, block, pos, renderer, texturesOverlay[4], mixedBrightness, adjR, adjG, adjB);
		renderSouthFace(world, block, pos, renderer, texturesOverlay[5], mixedBrightness, adjR, adjG, adjB);
	}
	
	private static ISprite[] getSprites(EnumFarmBlockTexture texture)
	{
		ISprite[] textures = new ISprite[6];
		textures[0] = getSprite(texture, 0);
		textures[1] = getSprite(texture, 1);
		textures[2] = getSprite(texture, 2);
		textures[3] = getSprite(texture, 3);
		textures[4] = getSprite(texture, 4);
		textures[5] = getSprite(texture, 5);
		return textures;
	}
	
	private static ISprite[] getOverlaySprites(int metadata)
	{
		ISprite[] textures = new ISprite[6];
		textures[0] = getOverlaySprite(metadata, 0);
		textures[1] = getOverlaySprite(metadata, 1);
		textures[2] = getOverlaySprite(metadata, 2);
		textures[3] = getOverlaySprite(metadata, 3);
		textures[4] = getOverlaySprite(metadata, 4);
		textures[5] = getOverlaySprite(metadata, 5);
		return textures;
	}
	
	private static ISprite getSprite(EnumFarmBlockTexture texture, int side)
	{
		TextureManager manager = TextureManager.getInstance();
		switch (texture) {
		case BRICK:
			return manager.registerTex("minecraft", "blocks", "brick");
		case BRICK_STONE:
			return manager.registerTex("minecraft", "blocks", "stonebrick");
		case BRICK_CHISELED:
			return manager.registerTex("minecraft", "blocks", "stonebrick_carved");
		case BRICK_CRACKED:
			return manager.registerTex("minecraft", "blocks", "stonebrick_cracked");
		case BRICK_MOSSY:
			return manager.registerTex("minecraft", "blocks", "stonebrick_mossy");
		case BRICK_NETHER:
			return manager.registerTex("minecraft", "blocks", "nether_brick");
		case SANDSTONE_CHISELED:
			if(side == 0)
				return manager.registerTex("minecraft", "blocks", "sandstone_bottom");
			else if(side == 1)
				return manager.registerTex("minecraft", "blocks", "sandstone_top");
			return manager.registerTex("minecraft", "blocks", "sandstone_carved");
		case SANDSTONE_SMOOTH:
			if(side == 0)
				return manager.registerTex("minecraft", "blocks", "sandstone_bottom");
			else if(side == 1)
				return manager.registerTex("minecraft", "blocks", "sandstone_top");
			return manager.registerTex("minecraft", "blocks", "sandstone_smooth");
		case QUARTZ:
			if(side == 0)
				return manager.registerTex("minecraft", "blocks", "quartz_block_bottom");
			else if(side == 1)
				return manager.registerTex("minecraft", "blocks", "quartz_block_top");
			return manager.registerTex("minecraft", "blocks", "quartz_block_side");
		case QUARTZ_CHISELED:
			if(side == 0 || side == 1)
				return manager.registerTex("minecraft", "blocks", "quartz_block_chiseled_top");
			return manager.registerTex("minecraft", "blocks", "quartz_block_chiseled");
		case QUARTZ_LINES:
			if(side == 0 || side == 1)
				return manager.registerTex("minecraft", "blocks", "quartz_block_lines_top");
			return manager.registerTex("minecraft", "blocks", "quartz_block_lines");
		default:
			return null;
		}
	}
	
	private static ISprite getOverlaySprite(int metadata, int side)
	{
		switch (side) {
		case 1:
		case 0:
			if(metadata == 0)
				return EnumFarmBlockTexture.getIcon(2);
			else if(metadata == 1)
				return EnumFarmBlockTexture.getIcon(2);
			else if(metadata == 2)
				return EnumFarmBlockTexture.getIcon(4);
			else if(metadata == 3)
				return EnumFarmBlockTexture.getIcon(5);
			else if(metadata == 4)
				return EnumFarmBlockTexture.getIcon(6);
			else if(metadata == 5)
				return EnumFarmBlockTexture.getIcon(7);
		case 2:
		case 3:
		case 4:
		case 5:
			if(metadata == 0)
				return EnumFarmBlockTexture.getIcon(0);
			else if(metadata == 1)
				return EnumFarmBlockTexture.getIcon(3);
			else if(metadata == 2)
				return EnumFarmBlockTexture.getIcon(4);
			else if(metadata == 3)
				return EnumFarmBlockTexture.getIcon(5);
			else if(metadata == 4)
				return EnumFarmBlockTexture.getIcon(6);
			else if(metadata == 5)
				return EnumFarmBlockTexture.getIcon(7);
		default:
			return EnumFarmBlockTexture.getIcon(0);
		}
	}
	
	@Override
	public TextureAtlasSprite getTexture() {
		return getSprites(EnumFarmBlockTexture.BRICK)[0].getSprite();
	}

}
