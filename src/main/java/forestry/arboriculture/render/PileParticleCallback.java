package forestry.arboriculture.render;

import forestry.api.arboriculture.EnumPileType;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.blocks.BlockBase;
import forestry.core.proxy.Proxies;
import forestry.core.render.ParticleHelper;
import forestry.core.render.TextureManager;
import forestry.core.render.ParticleHelper.Callback;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PileParticleCallback extends ParticleHelper.DefaultCallback<BlockPile>{

	public PileParticleCallback(BlockPile block) {
		super(block);
	}
	
	@Override
	protected void setTexture(EntityDiggingFX fx, World world, BlockPos pos, IBlockState state) {
		EnumPileType type = block.getPileType();
		TilePile pile = TileUtil.getTile(world, pos, TilePile.class);
		if(pile == null || pile.getTree() == null){
			super.setTexture(fx, world, pos, state);
			return;
		}
		TextureAtlasSprite texture;
		if(type == EnumPileType.DIRT){
			texture = TextureManager.registerSprite(new ResourceLocation("forestry:blocks/loam"));
		}else if(type == EnumPileType.WOOD){
			texture = pile.getTree().getGenome().getPrimary().getWoodProvider().getSprite(false);
		}else{
			texture = TextureManager.registerSprite(new ResourceLocation("forestry:blocks/ash"));
		}
		fx.setParticleTexture(texture);
	}

}
