package forestry.arboriculture.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ITree;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.genetics.Tree;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.blocks.propertys.UnlistedBlockAccess;
import forestry.core.blocks.propertys.UnlistedBlockPos;
import forestry.core.models.BlankItemModel;
import forestry.core.models.DefaultTextureGetter;
import forestry.core.tiles.TileUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelWoodPile extends BlankItemModel{

	private final Function<ResourceLocation, TextureAtlasSprite> textureGetter = new DefaultTextureGetter();
	@SideOnly(Side.CLIENT)
	private IModel modelWoodPileItem;
	private IModel modelWoodPileBlock;
	private static final Map<String, IBakedModel> blockCache = new HashMap<>();
	private static final Map<String, IBakedModel> itemCache = new HashMap<>();
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		if(state instanceof IExtendedBlockState){
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;
	
			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);
			
			TilePile pile = TileUtil.getTile(world, pos, TilePile.class);
			
			if(pile == null){
				return Collections.emptyList();
			}
			ITree tree = pile.getTree();
			if (tree == null) {
				tree = TreeManager.treeRoot.templateAsIndividual(TreeManager.treeRoot.getDefaultTemplate());
			}
			
			if (modelWoodPileBlock == null) {
				try {
					modelWoodPileBlock = ModelLoaderRegistry.getModel(new ResourceLocation("forestry:block/woodPile"));
				} catch (Exception e) {
					return Collections.emptyList();
				}
				if (modelWoodPileBlock == null) {
					return Collections.emptyList();
				}
			}
			
			IBakedModel model = bakeModel(tree, false);
			return model.getQuads(state, side, rand);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return TreeDefinition.Oak.getGenome().getPrimary().getWoodProvider().getSprite(false);
	}
	
	@Override
	protected ItemOverrideList createOverrides() {
		return new PileItemOverrideList();
	}
	
	private IBakedModel bakeModel(ITree tree, boolean isItem) {
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		IAlleleTreeSpecies treeSpecies = tree.getGenome().getPrimary();
		String treeUID = treeSpecies.getUID();
		Map<String, IBakedModel> map = isItem ? itemCache : blockCache;
		if(!map.containsKey(treeUID)){
			textures.put("woodBark", treeSpecies.getWoodProvider().getSprite(false).getIconName());
			textures.put("woodTop", treeSpecies.getWoodProvider().getSprite(true).getIconName());
			IModel retextureWoodPile = ModelProcessingHelper.retexture(isItem ? modelWoodPileItem : modelWoodPileBlock, textures.build());
			map.put(treeUID, retextureWoodPile.bake(ModelRotation.X0_Y0, isItem ? DefaultVertexFormats.ITEM : DefaultVertexFormats.BLOCK, textureGetter));
		}
		return map.get(treeUID);
	}
	
	private class PileItemOverrideList extends ItemOverrideList {
		public PileItemOverrideList() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if (modelWoodPileItem == null) {
				try {
					modelWoodPileItem = ModelLoaderRegistry.getModel(new ResourceLocation("forestry:item/woodPile"));
				} catch (Exception e) {
					return null;
				}
				if (modelWoodPileItem == null) {
					return null;
				}
			}
			ITree tree = null;
			if(stack.hasTagCompound()){
				tree = new Tree(stack.getTagCompound().getCompoundTag("ContainedTree"));
			}
			if (tree == null) {
				tree = TreeManager.treeRoot.templateAsIndividual(TreeManager.treeRoot.getDefaultTemplate());
			}
			return bakeModel(tree, true);
		}
	}

}
