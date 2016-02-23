package forestry.core.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import forestry.core.tiles.TileForestry;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {
	@Nullable
	private final TileEntitySpecialRenderer<? super T> renderer;

	public MachinePropertiesTesr(int meta, @Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer) {
		super(meta, teClass, name);
		this.renderer = renderer;
	}

	public MachinePropertiesTesr(int meta, @Nonnull Class<T> teClass, @Nonnull String name, @Nullable TileEntitySpecialRenderer<? super T> renderer, @Nonnull AxisAlignedBB boundingBox) {
		super(meta, teClass, name, boundingBox);
		this.renderer = renderer;
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
				ForgeHooksClient.registerTESRItemStack(item, getMeta(), getTeClass());
			}
		}
	}
}
