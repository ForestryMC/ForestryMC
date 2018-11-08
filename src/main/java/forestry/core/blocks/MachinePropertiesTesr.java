package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.client.ForgeHooksClient;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import forestry.core.tiles.TileForestry;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {
	@Nullable
	@SideOnly(Side.CLIENT)
	private TileEntitySpecialRenderer<? super T> renderer;

	private final String particleTextureLocation;
	private final boolean isFullCube;

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation) {
		this(teClass, name, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, AxisAlignedBB boundingBox, String particleTextureLocation) {
		this(teClass, name, boundingBox, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, AxisAlignedBB boundingBox, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name, boundingBox);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	@SideOnly(Side.CLIENT)
	public void setRenderer(TileEntitySpecialRenderer<? super T> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void registerTileEntity() {
		super.registerTileEntity();
		Block block = getBlock();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && renderer != null && block != null) {
			ClientRegistry.bindTileEntitySpecialRenderer(getTeClass(), renderer);
			Item item = Item.getItemFromBlock(block);
			if (item != Items.AIR) {
				ForgeHooksClient.registerTESRItemStack(item, 0, getTeClass());
			}
		}
	}

	@Override
	public String getParticleTextureLocation() {
		return particleTextureLocation;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return isFullCube;
	}
}
