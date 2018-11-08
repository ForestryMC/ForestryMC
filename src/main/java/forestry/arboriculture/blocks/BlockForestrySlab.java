package forestry.arboriculture.blocks;

import java.util.Collection;
import java.util.Random;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

public abstract class BlockForestrySlab<T extends Enum<T> & IWoodType> extends BlockSlab implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	protected static final int VARIANTS_PER_BLOCK = 8;

	private final boolean fireproof;
	private final int blockNumber;

	protected BlockForestrySlab(boolean fireproof, int blockNumber) {
		super(Material.WOOD);
		this.fireproof = fireproof;
		this.blockNumber = blockNumber;

		IBlockState iblockstate = this.blockState.getBaseState();

		if (!isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
		}

		PropertyWoodType<T> variant = getVariant();
		setDefaultState(iblockstate.withProperty(variant, variant.getFirstType()));

		setCreativeTab(Tabs.tabArboriculture);
		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		setHarvestLevel("axe", 0);
		useNeighborBrightness = true;
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	public abstract PropertyWoodType<T> getVariant();

	public int getBlockNumber() {
		return blockNumber;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.isDouble() ? new BlockStateContainer(this, getVariant()) : new BlockStateContainer(this, HALF, getVariant());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		T woodType = getWoodType(meta);
		IBlockState iblockstate = this.getDefaultState().withProperty(getVariant(), woodType);

		if (!this.isDouble()) {
			iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
		}

		return iblockstate;
	}

	@Override
	public abstract T getWoodType(int meta);

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = damageDropped(state);

		if (!this.isDouble() && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
			meta |= 8;
		}

		return meta;
	}

	@Override
	public int damageDropped(IBlockState state) {
		T woodType = state.getValue(getVariant());
		return woodType.getMetadata() - blockNumber * VARIANTS_PER_BLOCK;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		T woodType = state.getValue(getVariant());
		ItemStack slab = TreeManager.woodAccess.getStack(woodType, getBlockKind(), isFireproof());
		return slab.getItem();
	}

	@SuppressWarnings("deprecation") // this is the way the vanilla slabs work
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		T woodType = state.getValue(getVariant());
		ItemStack slab = TreeManager.woodAccess.getStack(woodType, getBlockKind(), isFireproof());
		return new ItemStack(slab.getItem(), 1, getMetaFromState(state));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	public String getTranslationKey(int meta) {
		T woodType = getWoodType(meta);
		return WoodHelper.getDisplayName(this, woodType);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (!isDouble()) {
			for (T woodType : getVariant().getAllowedValues()) {
				list.add(TreeManager.woodAccess.getStack(woodType, getBlockKind(), fireproof));
			}
		}
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		T woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireproof ? 0 : 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return fireproof ? 0 : 5;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.SLAB;
	}

	@Override
	public IProperty getVariantProperty() {
		return getVariant();
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return getWoodType(stack.getMetadata());
	}

	@Override
	public Collection<T> getWoodTypes() {
		return getVariant().getAllowedValues();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		String blockPath = isDouble() ? "double_slab" : getBlockKind().toString();
		ProxyArboricultureClient.registerWoodStateMapper(this, new WoodTypeStateMapper(this, blockPath, getVariant()));
	}
}
