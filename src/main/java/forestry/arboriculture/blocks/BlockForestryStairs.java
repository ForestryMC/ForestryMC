package forestry.arboriculture.blocks;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.arboriculture.proxy.ProxyArboricultureClient;

public class BlockForestryStairs<T extends Enum<T> & IWoodType> extends BlockStairs implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private final boolean fireproof;
	private final T woodType;

	public BlockForestryStairs(boolean fireproof, IBlockState modelState, T woodType) {
		super(modelState);
		this.fireproof = fireproof;
		this.woodType = woodType;
		setCreativeTab(Tabs.tabArboriculture);
		setHarvestLevel("axe", 0);
	}

	/* MODELS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		ModelBakery.registerItemVariants(item, WoodHelper.getDefaultResourceLocations(this));
		ProxyArboricultureClient.registerWoodMeshDefinition(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerStateMapper() {
		ProxyArboricultureClient.registerWoodStateMapper(this, new WoodTypeStateMapper(this, null));
	}

	@Override
	public boolean isFireproof() {
		return fireproof;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.STAIRS;
	}

	@Override
	public T getWoodType(int meta) {
		return woodType;
	}

	@Override
	public Collection<T> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		int meta = getMetaFromState(blockState);
		T woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (fireproof) {
			return 0;
		}
		return 5;
	}
}
