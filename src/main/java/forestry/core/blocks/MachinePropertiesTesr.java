package forestry.core.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import forestry.core.tiles.TileForestry;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {
	@Nullable
	private final TileEntitySpecialRenderer<? super T> renderer;
	@Nonnull
	private final String particleTextureLocation;
	private final boolean isFullCube;

	public MachinePropertiesTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nonnull String particleTextureLocation) {
		this(teClass, name, renderer, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nonnull AxisAlignedBB boundingBox, @Nonnull String particleTextureLocation) {
		this(teClass, name, renderer, boundingBox, particleTextureLocation, true);
	}
	
	public MachinePropertiesTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nonnull String particleTextureLocation, boolean isFullCube) {
		super(teClass, name);
		this.renderer = renderer;
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	public MachinePropertiesTesr(@Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nonnull AxisAlignedBB boundingBox, @Nonnull String particleTextureLocation, boolean isFullCube) {
		super(teClass, name, boundingBox);
		this.renderer = renderer;
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	@Nullable
	@Override
	public TileEntitySpecialRenderer<? super T> getRenderer() {
		return renderer;
	}

	@Override
	public void registerTileEntity() {
		super.registerTileEntity();
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			ClientRegistry.bindTileEntitySpecialRenderer(getTeClass(), renderer);
			Item item = Item.getItemFromBlock(getBlock());
			if (item != null) {
				ForgeHooksClient.registerTESRItemStack(item, 0, getTeClass());
			}
		}
	}
	
	@Nonnull
	@Override
	public String getParticleTextureLocation() {
		return particleTextureLocation;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return isFullCube;
	}
}
