package forestry.arboriculture.models;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.TreeManager;
import forestry.arboriculture.blocks.BlockPile;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.tiles.TilePile;
import forestry.core.blocks.properties.UnlistedBlockAccess;
import forestry.core.blocks.properties.UnlistedBlockPos;
import forestry.core.models.BlankModel;
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
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelProcessingHelper;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelWoodPile extends BlankModel {

	private final Function<ResourceLocation, TextureAtlasSprite> textureGetter = new DefaultTextureGetter();
	@Nullable
	private static IModel modelWoodPileItem;
	@Nullable
	private static IModel modelWoodPileBlock;
	private static final Cache<String, IBakedModel> blockCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
	private static final Cache<String, IBakedModel> itemCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	/**
	 * Init the model with datas from the ModelBakeEvent.
	 */
	public static void onModelBake(ModelBakeEvent event) {
		blockCache.invalidateAll();
		itemCache.invalidateAll();
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState stateExtended = (IExtendedBlockState) state;

			IBlockAccess world = stateExtended.getValue(UnlistedBlockAccess.BLOCKACCESS);
			BlockPos pos = stateExtended.getValue(UnlistedBlockPos.POS);

			TilePile pile = TileUtil.getTile(world, pos, TilePile.class);

			if (pile == null) {
				return Collections.emptyList();
			}
			IAlleleTreeSpecies treeSpecies = pile.getTreeSpecies();

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

			IBakedModel model = bakeModel(treeSpecies, false);
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

	private IBakedModel bakeModel(IAlleleTreeSpecies treeSpecies, boolean isItem) {
		ImmutableMap.Builder<String, String> textures = ImmutableMap.builder();
		String treeUID = treeSpecies.getUID();
		Cache<String, IBakedModel> map = isItem ? itemCache : blockCache;
		IBakedModel model = map.getIfPresent(treeUID);
		if (model == null) {
			textures.put("woodBark", treeSpecies.getWoodProvider().getSprite(false).getIconName());
			textures.put("woodTop", treeSpecies.getWoodProvider().getSprite(true).getIconName());
			IModel retextureWoodPile = ModelProcessingHelper.retexture(isItem ? modelWoodPileItem : modelWoodPileBlock, textures.build());
			model = retextureWoodPile.bake(ModelRotation.X0_Y0, isItem ? DefaultVertexFormats.ITEM : DefaultVertexFormats.BLOCK, textureGetter);
			map.put(treeUID, model);
		}
		return model;
	}

	private class PileItemOverrideList extends ItemOverrideList {
		public PileItemOverrideList() {
			super(Collections.emptyList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
			if (modelWoodPileItem == null) {
				try {
					modelWoodPileItem = ModelLoaderRegistry.getModel(new ResourceLocation("forestry:item/woodPile"));
				} catch (Exception e) {
					return originalModel;
				}
				if (modelWoodPileItem == null) {
					return originalModel;
				}
			}
			IAlleleTreeSpecies treeSpecies = BlockPile.getTreeSpecies(stack);
			if (treeSpecies == null) {
				treeSpecies = (IAlleleTreeSpecies) TreeManager.treeRoot.getDefaultTemplate()[TreeManager.treeRoot.getSpeciesChromosomeType().ordinal()];
			}
			return bakeModel(treeSpecies, true);
		}
	}

}
