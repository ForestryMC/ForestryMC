package forestry.core.blocks;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;

public class BlockResourceStorage extends Block implements IItemModelRegister {
	public static final PropertyEnum RESOURCE = PropertyEnum.create("resource", ResourceType.class);
	
	public enum ResourceType implements IStringSerializable {
		APATITE,
		COPPER,
		TIN,
		BRONZE;

		public static final ResourceType[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public BlockResourceStorage() {
		super(Material.iron);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.blockState.getBaseState().withProperty(RESOURCE, ResourceType.APATITE));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, RESOURCE);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((ResourceType) state.getValue(RESOURCE)).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(RESOURCE, ResourceType.values()[meta]);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List itemList) {
		for (ResourceType resourceType : ResourceType.values()) {
			ItemStack stack = get(resourceType);
			itemList.add(stack);
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "storage/apatite");
		manager.registerItemModel(item, 1, "storage/copper");
		manager.registerItemModel(item, 2, "storage/tin");
		manager.registerItemModel(item, 3, "storage/bronze");
	}

	public ItemStack get(ResourceType type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
