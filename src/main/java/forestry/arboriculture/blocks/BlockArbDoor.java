package forestry.arboriculture.blocks;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.arboriculture.EnumWoodType;
import forestry.api.arboriculture.TreeManager;
import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodHelper;
import forestry.core.proxy.Proxies;

public class BlockArbDoor extends BlockDoor implements IWoodTyped, IItemModelRegister, IStateMapperRegister {
	private final EnumWoodType woodType;

	public BlockArbDoor(EnumWoodType woodType) {
		super(Material.wood);
		this.woodType = woodType;

		setHarvestLevel("axe", 0);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerVariant(item, WoodHelper.getResourceLocations(this));
		manager.registerItemModel(item, new WoodHelper.WoodMeshDefinition(this));
	}

	@Override
	public void registerStateMapper() {
		Proxies.render.registerStateMapper(this, new WoodTypeStateMapper(this, null).addPropertyToRemove(POWERED));
	}

	@Nonnull
	@Override
	public String getBlockKind() {
		return "doors";
	}

	@Override
	public boolean isFireproof() {
		return false;
	}

	@Nonnull
	@Override
	public EnumWoodType getWoodType(int meta) {
		return woodType;
	}

	@Nonnull
	@Override
	public Collection<EnumWoodType> getWoodTypes() {
		return Collections.singleton(woodType);
	}

	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		IBlockState blockState = world.getBlockState(pos);
		int meta = getMetaFromState(blockState);
		EnumWoodType woodType = getWoodType(meta);
		return woodType.getHardness();
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos) {
		return getItem();
	}

	private Item getItem() {
		return TreeManager.woodItemAccess.getDoor(woodType).getItem();
	}
}
