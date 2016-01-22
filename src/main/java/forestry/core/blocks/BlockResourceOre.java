/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.core.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import forestry.api.core.IModelManager;
import forestry.api.core.IItemModelRegister;
import forestry.core.CreativeTabForestry;
import forestry.plugins.PluginCore;

public class BlockResourceOre extends Block implements IItemModelRegister {
	private static final PropertyEnum<ResourceType> RESOURCE = PropertyEnum.create("resource", ResourceType.class);
	
	public enum ResourceType implements IStringSerializable {
		APATITE,
		COPPER,
		TIN;
		
		public static final ResourceType[] VALUES = values();

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	public BlockResourceOre() {
		super(Material.rock);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
		setDefaultState(this.blockState.getBaseState().withProperty(RESOURCE, ResourceType.APATITE));
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { RESOURCE });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(RESOURCE).ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(RESOURCE, ResourceType.values()[meta]);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
		

		if (state.getValue(RESOURCE) == ResourceType.APATITE) {
			this.dropXpOnBlockBreak(world, pos, MathHelper.getRandomIntegerInRange(world.rand, 1, 4));
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		
		ResourceType metadata = state.getValue(RESOURCE);

		if (metadata == ResourceType.APATITE) {
			int fortuneModifier = RANDOM.nextInt(fortune + 2) - 1;
			if (fortuneModifier < 0) {
				fortuneModifier = 0;
			}

			int amount = (2 + RANDOM.nextInt(5)) * (fortuneModifier + 1);
			if (amount > 0) {
				drops.add(PluginCore.items.apatite.getItemStack(amount));
			}
		} else {
			drops.add(new ItemStack(this, 1, metadata.ordinal()));
		}

		return drops;
	}

	@Override
	public int getDamageValue(World world, BlockPos pos) {
		return getMetaFromState(world.getBlockState(pos));
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	// / CREATIVE INVENTORY
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		for (ResourceType resourceType : ResourceType.values()) {
			ItemStack stack = get(resourceType, 1);
			itemList.add(stack);
		}
	}

	/* MODLES */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerModel(Item item, IModelManager manager) {
		manager.registerItemModel(item, 0, "ores/apatite");
		manager.registerItemModel(item, 1, "ores/copper");
		manager.registerItemModel(item, 2, "ores/tin");
	}

	public ItemStack get(ResourceType type, int amount) {
		return new ItemStack(this, amount, type.ordinal());
	}
}
