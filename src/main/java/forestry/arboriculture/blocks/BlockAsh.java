package forestry.arboriculture.blocks;

import java.util.Random;

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
import net.minecraft.world.World;

import net.minecraftforge.client.model.ModelLoader;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.api.core.IItemModelRegister;
import forestry.api.core.IModelManager;
import forestry.api.core.IStateMapperRegister;
import forestry.core.ModuleCore;

public class BlockAsh extends Block implements IStateMapperRegister, IItemModelRegister {

	public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 15);

	public BlockAsh() {
		super(Material.GROUND, MapColor.BLACK);
		setSoundType(SoundType.SAND);
		setHarvestLevel("shovel", 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AMOUNT);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		for (int i = 0; i < 16; i++) {
			manager.registerItemModel(item, i, "ash_block");
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
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
		Random rand = world instanceof World ? ((World) world).rand : new Random();
		int amount = state.getValue(AMOUNT);
		if (amount > 0) {
			amount += 9;
			amount += rand.nextInt(1 + fortune);
			drops.add(new ItemStack(Items.COAL, amount, 1));
			drops.add(new ItemStack(ModuleCore.getItems().ash, 1 + rand.nextInt(amount / 4)));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerStateMapper() {
		ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(AMOUNT).build());
	}

}
