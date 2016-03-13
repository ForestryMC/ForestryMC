package forestry.greenhouse.models;

import forestry.greenhouse.blocks.BlockGreenhouse;
import forestry.greenhouse.blocks.BlockGreenhouseType;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import forestry.api.core.IModelBaker;
import forestry.core.models.ModelBlockOverlay;
import forestry.core.tiles.ICamouflagedBlock;

public class ModelGreenhouse extends ModelBlockOverlay<BlockGreenhouse> {

	public ModelGreenhouse() {
		super(BlockGreenhouse.class);
	}

	@Override
	protected void bakeInventoryBlock(BlockGreenhouse block, ItemStack item, IModelBaker baker) {
		bakeBlockModel(block, null, null, baker, null);
	}

	@Override
	protected void bakeWorldBlock(BlockGreenhouse block, IBlockAccess world, BlockPos pos, IExtendedBlockState stateExtended, IModelBaker baker) {
		TileEntity tile = world.getTileEntity(pos);
		if(!(tile instanceof ICamouflagedBlock)){
			return;
		}
		bakeBlockModel(block, pos, stateExtended, baker, (ICamouflagedBlock) tile);
	}
	
	private void bakeBlockModel(@Nonnull BlockGreenhouse block, @Nullable BlockPos pos, @Nullable IExtendedBlockState stateExtended, @Nonnull IModelBaker baker, @Nullable ICamouflagedBlock camouflageBlock){
		if(camouflageBlock != null){
			ItemStack camouflageBlockStack = camouflageBlock.getCamouflageBlock();
			if(camouflageBlockStack != null){
				BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
				baker.addBakedModel(modelShapes.getModelForState(Block.getBlockFromItem(camouflageBlockStack.getItem()).getStateFromMeta(camouflageBlockStack.getItemDamage())));
			}
			else if(block.getGreenhouseType() == BlockGreenhouseType.GLASS){
				TextureAtlasSprite glassSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null);
				baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null), 100);
				baker.setParticleSprite(glassSprite);
			}else{
				TextureAtlasSprite plainSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null);
				baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null), 100);
				baker.setParticleSprite(plainSprite);
			}
		}else{
			if(block.getGreenhouseType() == BlockGreenhouseType.GLASS){
				TextureAtlasSprite glassSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null);
				baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.GLASS, null), 100);
				baker.setParticleSprite(glassSprite);
			}else{
				TextureAtlasSprite plainSprite = BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null);
				baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(BlockGreenhouseType.PLAIN, null), 100);
				baker.setParticleSprite(plainSprite);
			}
		}
		if(block.getGreenhouseType() != BlockGreenhouseType.PLAIN && block.getGreenhouseType() != BlockGreenhouseType.GLASS){
			baker.addBlockModel(block, pos, BlockGreenhouseType.getSprite(block.getGreenhouseType(), stateExtended), 101);
		}
	}

}
