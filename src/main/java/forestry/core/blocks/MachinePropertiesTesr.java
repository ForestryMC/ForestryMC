package forestry.core.blocks;

import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderForestryItem;
import forestry.core.render.RenderForestryTile;
import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {

	@Nullable
	@OnlyIn(Dist.CLIENT)
	private IForestryRenderer<? super T> renderer;
	@Nullable
	@OnlyIn(Dist.CLIENT)
    private Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> tileRenderer;

	private final String particleTextureLocation;
	private final boolean isFullCube;

    public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, String particleTextureLocation) {
        this(teType, name, particleTextureLocation, true);
    }

    public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, VoxelShape shape, String particleTextureLocation) {
        this(teType, name, shape, particleTextureLocation, true);
    }

    public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, String particleTextureLocation, boolean isFullCube) {
        super(teType, name);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

    public MachinePropertiesTesr(Supplier<FeatureTileType<? extends T>> teType, String name, VoxelShape shape, String particleTextureLocation, boolean isFullCube) {
        super(teType, name, shape);
		this.particleTextureLocation = particleTextureLocation;
		this.isFullCube = isFullCube;
	}

	@OnlyIn(Dist.CLIENT)
	public void setRenderer(IForestryRenderer<? super T> renderer) {
		this.renderer = renderer;
        this.tileRenderer = (dispatcher) -> new RenderForestryTile<>(dispatcher, renderer);
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
            ClientRegistry.bindTileEntityRenderer(getTeType(), tileRenderer);
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
        return properties.setISTER(() -> () -> new RenderForestryItem(machinePropertiesTesr.getRenderer()));
	}
}
