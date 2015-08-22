package forestry.core.gadgets;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelObject;
import forestry.api.core.IVariantObject;
import forestry.core.CreativeTabForestry;
import forestry.core.gadgets.BlockResourceStorageBlock.Resources;
import forestry.core.render.TextureManager;

public class BlockResourceStorageBlock extends Block implements IModelObject, IVariantObject {
	
	public static final PropertyEnum RESOURCES = PropertyEnum.create("resource", Resources.class);
	
	public enum Resources implements IStringSerializable
	{
		APATITE,
		COPPER,
		TIN,
		BRONZE;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	public BlockResourceStorageBlock() {
		super(Material.iron);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return((Resources)state.getValue(RESOURCES)).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(RESOURCES, Resources.values()[meta]);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
	}

	@Override
	public int getDamageValue(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return getMetaFromState(state);
	}

	@Override
	public String[] getVariants() {
		return new String[]{ "apatite", "copper", "tin", "bronze" };
	}

	@Override
	public ModelType getModelType() {
		return ModelType.META;
	}

}
