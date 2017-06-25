package forestry.arboriculture.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class BlockCharcoal extends Block implements IStateMapperRegister, IItemModelRegister {

	public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 15);
	
	public BlockCharcoal() {
		super(Material.ROCK, MapColor.BLACK);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(Tabs.tabArboriculture);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AMOUNT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for(int i = 0;i < 16;i++){
			manager.registerItemModel(item, i);
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AMOUNT);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(AMOUNT, meta);
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if(state.getValue(AMOUNT) > 0){
			drops.add(new ItemStack(Items.COAL, state.getValue(AMOUNT) + 9, 1));
		}
		super.getDrops(drops, world, pos, state, fortune);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(AMOUNT).build());
	}

}
