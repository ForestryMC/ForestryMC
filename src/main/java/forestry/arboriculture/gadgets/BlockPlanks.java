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
package forestry.arboriculture.gadgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.Tabs;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.WoodType;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockPlanks extends Block implements IWoodTyped {

	public static enum PlankCat {
		CAT0, CAT1
	}

	protected final PlankCat cat;

	public BlockPlanks(PlankCat cat) {
		this(cat, Material.wood);
	}

	protected BlockPlanks(PlankCat cat, Material material) {
		super(material);
		this.cat = cat;
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(Tabs.tabArboriculture);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		int count = (cat == PlankCat.CAT0 ? 16 : 8);
		for (int i = 0; i < count; i++)
			itemList.add(new ItemStack(this, 1, i));
	}

	/* ICONS */
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		WoodType.registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return getWoodType(meta).getPlankIcon();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	/* PROPERTIES */
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return getWoodType(world.getBlockMetadata(x, y, z)).getHardness();
	}

	@Override
	public boolean isWood(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 20;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return true;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return 5;
	}

	@Override
	public WoodType getWoodType(int meta) {
		if(cat.ordinal() * 16 + meta < WoodType.VALUES.length)
			return WoodType.VALUES[cat.ordinal() * 16 + meta];
		else
			return WoodType.LARCH;
	}

	@Override
	public String getBlockKind() {
		return "planks";
	}
}
