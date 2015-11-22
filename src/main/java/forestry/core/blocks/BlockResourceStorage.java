package forestry.core.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.core.CreativeTabForestry;
import forestry.core.render.TextureManager;

public class BlockResourceStorage extends Block {
	public enum ResourceType {
		APATITE,
		COPPER,
		TIN,
		BRONZE;

		public static final ResourceType[] VALUES = values();

		@SideOnly(Side.CLIENT)
		public IIcon icon;
	}

	public BlockResourceStorage() {
		super(Material.iron);
		setHardness(3F);
		setResistance(5F);
		setCreativeTab(CreativeTabForestry.tabForestry);
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
	public int damageDropped(int damage) {
		return damage;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		ResourceType.APATITE.icon = TextureManager.registerTex(register, "storage/apatite");
		ResourceType.COPPER.icon = TextureManager.registerTex(register, "storage/copper");
		ResourceType.TIN.icon = TextureManager.registerTex(register, "storage/tin");
		ResourceType.BRONZE.icon = TextureManager.registerTex(register, "storage/bronze");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int meta) {
		if (meta < 0 || meta >= ResourceType.VALUES.length) {
			return null;
		}

		return ResourceType.VALUES[meta].icon;
	}

	public ItemStack get(ResourceType type) {
		return new ItemStack(this, 1, type.ordinal());
	}
}
