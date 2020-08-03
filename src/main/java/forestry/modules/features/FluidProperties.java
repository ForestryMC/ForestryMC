package forestry.modules.features;

import forestry.core.items.DrinkProperties;
import forestry.core.render.ForestryResource;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.awt.*;

public class FluidProperties {
    public final int density;
    public final int viscosity;
    public final int temperature;
    public final Color particleColor;
    public final int flammability;
    public final boolean flammable;
    @Nullable
    public final DrinkProperties properties;
    public final ResourceLocation[] resources = new ResourceLocation[2];

    public FluidProperties(FeatureFluid.Builder builder) {
        this.density = builder.density;
        this.viscosity = builder.viscosity;
        this.temperature = builder.temperature;
        this.particleColor = builder.particleColor;
        this.flammability = builder.flammability;
        this.flammable = builder.flammable;
        this.properties = builder.properties;
        this.resources[0] = new ForestryResource("block/liquid/" + builder.identifier + "_still");
        this.resources[1] = new ForestryResource("block/liquid/" + builder.identifier + "_flow");
        if (!resourceExists(resources[1])) {
            this.resources[1] = resources[0];
        }
    }

    public boolean resourceExists(ResourceLocation location) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return false;
        }
        IResourceManager resourceManager = minecraft.getResourceManager();
        return resourceManager.hasResource(location);
    }
}
