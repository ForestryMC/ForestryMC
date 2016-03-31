package forestry.core.blocks;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.core.CreativeTabForestry;

public class BlockResourceStorage extends Block implements IItemModelRegister {
	public static final PropertyEnum<EnumResourceType> STORAGE_RESOURCES = PropertyEnum.create("resource", EnumResourceType.class);

	public BlockResourceStorage() {
		super(Material.iron);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.blockState.getBaseState().withProperty(STORAGE_RESOURCES, EnumResourceType.APATITE));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, STORAGE_RESOURCES);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STORAGE_RESOURCES).getMeta();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STORAGE_RESOURCES, EnumResourceType.VALUES[meta]);
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List<ItemStack> itemList) {
		for (EnumResourceType resourceType : EnumResourceType.VALUES) {
			itemList.add(get(resourceType));
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(EnumResourceType resourceType : EnumResourceType.VALUES){
			manager.registerItemModel(item, resourceType.getMeta(), "storage/" + resourceType.getName());
		}
	}

	public ItemStack get(EnumResourceType type) {
		return new ItemStack(this, 1, type.getMeta());
	}
}
