package forestry.core.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.math.shapes.VoxelShape;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;

import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderForestryItem;
import forestry.core.render.RenderForestryTile;
import forestry.core.tiles.TileForestry;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {

	@Nullable
	@OnlyIn(Dist.CLIENT)
	private IForestryRenderer<? super T> renderer;
	@Nullable
	@OnlyIn(Dist.CLIENT)
	private TileEntityRenderer<? super T> tileRenderer;

	private final String particleTextureLocation;
	private final boolean isFullCube;

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation) {
		this(teClass, name, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, VoxelShape shape, String particleTextureLocation) {
		this(teClass, name, shape, particleTextureLocation, true);
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	public MachinePropertiesTesr(Class<T> teClass, String name, VoxelShape shape, String particleTextureLocation, boolean isFullCube) {
		super(teClass, name, shape);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	@OnlyIn(Dist.CLIENT)
	public void setRenderer(IForestryRenderer<? super T> renderer) {
		this.renderer = renderer;
		this.tileRenderer = new RenderForestryTile<>(renderer);
	}

	@Override
	@Nullable
	public IForestryRenderer<? super T> getRenderer() {
		return renderer;
	}

	@Override
	public void clientSetup() {
		super.clientSetup();
		if (tileRenderer != null) {
			ClientRegistry.bindTileEntitySpecialRenderer(getTeClass(), tileRenderer);
		}
	}

	@Override
	public String getParticleTextureLocation() {
		return particleTextureLocation;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return isFullCube;
	}

	public static Item.Properties setRenderer(Item.Properties properties, IBlockType type) {
		IMachineProperties machineProperties = type.getMachineProperties();
		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER || !(machineProperties instanceof IMachinePropertiesTesr)) {
			return properties;
		}
		IMachinePropertiesTesr machinePropertiesTesr = (IMachinePropertiesTesr) machineProperties;
		if (machinePropertiesTesr.getRenderer() == null) {
			return properties;
		}
		return properties.setTEISR(() -> () -> new RenderForestryItem(machinePropertiesTesr.getRenderer()));
	}
}
