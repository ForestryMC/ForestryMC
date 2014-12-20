package forestry.core.gadgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.core.CreativeTabForestry;
import forestry.core.render.TextureManager;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockResourceStorageBlock extends Block {
	
	public BlockResourceStorageBlock() {
		super(Material.iron);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
	}

	@SideOnly(Side.CLIENT)
	private IIcon iconApatite;
	@SideOnly(Side.CLIENT)
	private IIcon iconCopper;
	@SideOnly(Side.CLIENT)
	private IIcon iconTin;
	@SideOnly(Side.CLIENT)
	private IIcon iconBronze;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List itemList) {
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
		itemList.add(new ItemStack(this, 1, 2));
		itemList.add(new ItemStack(this, 1, 3));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		iconApatite = TextureManager.getInstance().registerTex(register, "storage/apatite");
		iconCopper = TextureManager.getInstance().registerTex(register, "storage/copper");
		iconTin = TextureManager.getInstance().registerTex(register, "storage/tin");
		iconBronze = TextureManager.getInstance().registerTex(register, "storage/bronze");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int meta) {
		IIcon icon;
		if (meta == 0) {
			icon = iconApatite;
		} else if (meta == 1) {
			icon = iconCopper;
		} else if (meta == 2) {
			icon = iconTin;
		} else  {
			icon = iconBronze;
		}
		
		return icon;
	}

}
