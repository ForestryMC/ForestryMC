package forestry.core.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IModelRegister;
import forestry.core.CreativeTabForestry;
import forestry.core.gadgets.BlockResourceStorageBlock.Resources;
import forestry.core.render.TextureManager;

public class BlockResourceStorageBlock extends Block implements IModelRegister {
	
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
		setStepSound(soundTypeMetal);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.blockState.getBaseState().withProperty(RESOURCES, Resources.APATITE));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[]{RESOURCES});
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
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "apatite");
		manager.registerItemModel(item, 1, "copper");
		manager.registerItemModel(item, 2, "tin");
		manager.registerItemModel(item, 3, "bronze");
	}

}
