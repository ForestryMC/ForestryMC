package forestry.core.blocks;

import com.google.common.base.Preconditions;
import forestry.core.config.Constants;
import forestry.core.render.IForestryRenderer;
import forestry.core.render.RenderForestryItem;
import forestry.core.render.RenderForestryTile;
import forestry.core.tiles.TileForestry;
import forestry.modules.features.FeatureTileType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public class MachinePropertiesTesr<T extends TileForestry> extends MachineProperties<T> implements IMachinePropertiesTesr<T> {

    private final ResourceLocation particleTexture;
    private final boolean isFullCube;

    @Nullable
    @OnlyIn(Dist.CLIENT)
    private IForestryRenderer<? super T> renderer;
    @Nullable
    @OnlyIn(Dist.CLIENT)
    private Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> tileRenderer;

    public MachinePropertiesTesr(
            Supplier<FeatureTileType<? extends T>> teType,
            String name,
            IShapeProvider shape,
            ResourceLocation particleTexture,
            boolean isFullCube
    ) {
        super(teType, name, shape);
        this.particleTexture = particleTexture;
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
    public ResourceLocation getParticleTexture() {
        return particleTexture;
    }

    @Override
    public boolean isFullCube(BlockState state) {
        return isFullCube;
    }

    public static Item.Properties setRenderer(Item.Properties properties, IBlockType type) {
        DistExecutor.runWhenOn(Dist.CLIENT, () ->
                () -> {
                    IMachineProperties machineProperties = type.getMachineProperties();
                    if (!(machineProperties instanceof IMachinePropertiesTesr)) {
                        return;
                    }
                    IMachinePropertiesTesr machinePropertiesTesr = (IMachinePropertiesTesr) machineProperties;
                    if (machinePropertiesTesr.getRenderer() == null) {
                        return;
                    }
                    properties.setISTER(() -> () -> new RenderForestryItem(machinePropertiesTesr.getRenderer()));
                });
        return properties;
    }

    public static class Builder<T extends TileForestry> extends MachineProperties.Builder<T, Builder<T>> {
        @Nullable
        private ResourceLocation particleTexture;
        private boolean isFullCube = true;

        public Builder(Supplier<FeatureTileType<? extends T>> type, String name) {
            super(type, name);
        }

        public Builder() {
        }

        public Builder<T> setParticleTexture(String particleTexture) {
            return setParticleTexture(new ResourceLocation(Constants.MOD_ID, "block/" + particleTexture));
        }

        public Builder<T> setParticleTexture(ResourceLocation particleTexture) {
            this.particleTexture = particleTexture;
            return this;
        }

        public Builder<T> setNotFullCube() {
            isFullCube = false;
            return this;
        }

        public MachinePropertiesTesr<T> create() {
            Preconditions.checkNotNull(type);
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(shape);
            Preconditions.checkNotNull(particleTexture);
            return new MachinePropertiesTesr<>(type, name, shape, particleTexture, isFullCube);
        }
    }
}
