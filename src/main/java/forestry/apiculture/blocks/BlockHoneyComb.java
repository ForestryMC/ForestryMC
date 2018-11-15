package forestry.apiculture.blocks;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.apiculture.items.EnumHoneyComb;
import forestry.core.blocks.IBlockWithMeta;
import forestry.core.blocks.IColoredBlock;
import forestry.core.config.Config;
import forestry.core.config.Constants;

public abstract class BlockHoneyComb extends Block implements IItemModelRegister, IBlockWithMeta, IColoredBlock, IStateMapperRegister {
	public final int minMeta;

	public static final BlockHoneyComb[] create() {
		BlockHoneyComb[] blocks = new BlockHoneyComb[2];
		for (int i = 0; i < blocks.length; i++) {
			HoneyCombPredicate filter = new HoneyCombPredicate(i, 16);
			PropertyEnum<EnumHoneyComb> variant = PropertyEnum.create("type", EnumHoneyComb.class, filter);
			blocks[i] = new BlockHoneyComb(filter.minMeta) {
				@Override
				protected PropertyEnum<EnumHoneyComb> getVariant() {
					return variant;
				}
			};
		}
		return blocks;
	}

	public BlockHoneyComb(int minMeta) {
		super(Material.CLOTH);
		setHardness(1F);
		setCreativeTab(Tabs.tabApiculture);
		setDefaultState(this.blockState.getBaseState().withProperty(getVariant(), getVariant().getAllowedValues().iterator().next()));
		this.minMeta = minMeta;
	}

	protected abstract PropertyEnum<EnumHoneyComb> getVariant();

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getVariant());
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(getVariant()).ordinal() - minMeta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(getVariant(), EnumHoneyComb.get(minMeta + meta));
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (EnumHoneyComb honeyComb : getVariant().getAllowedValues()) {
			if (!honeyComb.isSecret() || Config.isDebug) {
				list.add(get(honeyComb));
			}
		}
	}

	@Override
	public String getTranslationKey() {
		return "tile.for.bee_combs";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (EnumHoneyComb comb : getVariant().getAllowedValues()) {
			manager.registerItemModel(item, comb.ordinal() - minMeta, "block_bee_combs");
		}
	}

	public ItemStack get(EnumHoneyComb honeyComb) {
		return new ItemStack(this, 1, honeyComb.ordinal() - minMeta);
	}

	@Override
	public String getNameFromMeta(int meta) {
		EnumHoneyComb honeyComb = EnumHoneyComb.get(minMeta + meta);
		return honeyComb.name;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		int meta = getMetaFromState(state);
		EnumHoneyComb honeyComb = EnumHoneyComb.get(minMeta + meta);
		if (tintIndex == 1) {
			return honeyComb.primaryColor;
		} else {
			return honeyComb.secondaryColor;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new HoneyCombStateMapper());
	}

	private static class HoneyCombPredicate implements Predicate<EnumHoneyComb> {
		private final int minMeta;
		private final int maxMeta;

		public HoneyCombPredicate(int blockNumber, int variantsPerBlock) {
			this.minMeta = blockNumber * variantsPerBlock;
			this.maxMeta = minMeta + variantsPerBlock - 1;
		}

		@Override
		public boolean apply(@Nullable EnumHoneyComb honeyComb) {
			return honeyComb != null && honeyComb.ordinal() >= minMeta && honeyComb.ordinal() <= maxMeta;
		}
	}

	@SideOnly(Side.CLIENT)
	private static class HoneyCombStateMapper extends StateMapperBase {

		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return new ModelResourceLocation(Constants.MOD_ID + ":bee_combs", "normal");
		}

	}
}
