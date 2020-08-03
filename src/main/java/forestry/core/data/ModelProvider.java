package forestry.core.data;

import com.google.common.collect.Maps;
import com.google.gson.*;
import forestry.modules.features.FeatureItem;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// TODO: Migrate to forge system
public abstract class ModelProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final String folder;
    protected final Map<String, ModelBuilder> pathToBuilder = Maps.newLinkedHashMap();
    protected final DataGenerator generator;

    public ModelProvider(DataGenerator generator, String folder) {
        this.generator = generator;
        this.folder = folder;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        this.pathToBuilder.clear();
        this.registerModels();
        pathToBuilder.forEach((key, builder) -> {
            JsonObject jsonobject = builder.serialize();
            Path path = this.makePath(key);
            try {
                String s = GSON.toJson(jsonobject);
                String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
                if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
                    Files.createDirectories(path.getParent());

                    try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                        bufferedwriter.write(s);
                    }
                }

                cache.recordHash(path, s1);
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't save models to {}", path, ioexception);
            }

        });
    }

    protected abstract void registerModels();

    protected void registerModel(Item item, ModelBuilder builder) {
        registerModel(item.getRegistryName().getPath(), builder);
    }

    protected void registerModel(Block block, ModelBuilder builder) {
        registerModel(block.getRegistryName().getPath(), builder);
    }

    protected void registerModel(FeatureItem feature, ModelBuilder builder) {
        registerModel(feature.item(), builder);
    }

    protected void registerModel(String path, ModelBuilder builder) {
        pathToBuilder.put(path, builder);
    }

    protected Path makePath(String location) {
        return this.generator.getOutputFolder().resolve("assets/forestry/models/" + folder + "/" + location + ".json");
    }

    @Override
    public String getName() {
        return "Model Provider";
    }

    public static class ModelBuilder {
        @Nullable
        public ResourceLocation parent;
        @Nullable
        public ResourceLocation loader;
        public final Map<String, ResourceLocation> textures = new HashMap<>();
        public final Map<String, JsonElement> loaderData = new HashMap<>();
        public final List<Element> elements = new LinkedList<>();
        @Nullable
        private Boolean ambientOcclusion = null;

        public ModelBuilder element(Element element) {
            this.elements.add(element);
            return this;
        }

        public ModelBuilder ambientOcclusion(boolean ambientOcclusion) {
            this.ambientOcclusion = ambientOcclusion;
            return this;
        }

        public ModelBuilder loader(ResourceLocation loader) {
            this.loader = loader;
            return this;
        }

        public ModelBuilder loaderData(String key, JsonElement element) {
            loaderData.put(key, element);
            return this;
        }

        public ModelBuilder parent(String parent) {
            return parent(new ResourceLocation(parent));
        }

        public ModelBuilder parent(ResourceLocation parent) {
            this.parent = parent;
            return this;
        }

        public ModelBuilder particle(ResourceLocation location) {
            return texture("particle", location);
        }

        public ModelBuilder item() {
            return parent("item/generated");
        }

        public ModelBuilder layer(int index, ResourceLocation location) {
            textures.put("layer" + index, location);
            return this;
        }

        public ModelBuilder texture(String key, ResourceLocation location) {
            textures.put(key, location);
            return this;
        }

        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            if (parent != null) {
                object.addProperty("parent", parent.toString());
            }
            if (loader != null) {
                object.addProperty("loader", loader.toString());
            }
            if (!loaderData.isEmpty()) {
                for (Map.Entry<String, JsonElement> entry : loaderData.entrySet()) {
                    object.add(entry.getKey(), entry.getValue());
                }
            }
            JsonObject texturesObj = new JsonObject();
            if (!textures.isEmpty()) {
                for (Map.Entry<String, ResourceLocation> texture : textures.entrySet()) {
                    texturesObj.addProperty(texture.getKey(), texture.getValue().toString());
                }
                object.add("textures", texturesObj);
            }
            if (ambientOcclusion != null) {
                object.addProperty("ambientocclusion", ambientOcclusion);
            }
            if (!elements.isEmpty()) {
                JsonArray elementsArray = new JsonArray();
                for (Element element : elements) {
                    elementsArray.add(element.serialize());
                }
                object.add("elements", elementsArray);
            }
            return object;
        }
    }

    public static class Element {
        private final Vector3i from;
        private final Vector3i to;
        private final Face[] faces = new Face[6];
        private boolean shade = true;
        @Nullable
        public Rotation rotation;

        public Element(Vector3i from, Vector3i to) {
            this.from = from;
            this.to = to;
        }

        public Element shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public Element face(Direction direction, Face face) {
            this.faces[direction.getIndex()] = face;
            return this;
        }

        public Element rotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        private JsonElement serialize() {
            JsonObject obj = new JsonObject();
            obj.add("from", serializeVex(from));
            obj.add("to", serializeVex(to));
            obj.addProperty("shade", shade);
            if (rotation != null) {
                obj.add("rotation", rotation.serialize());
            }
            JsonObject facesObj = new JsonObject();
            for (int i = 0; i < faces.length; i++) {
                Face face = faces[i];
                if (face == null) {
                    continue;
                }
                Direction direction = Direction.byIndex(i);
                facesObj.add(direction.getString(), face.serialize());
            }
            obj.add("faces", facesObj);
            return obj;
        }
    }

    public static class Rotation {
        private Vector3i origin = Vector3i.NULL_VECTOR;
        @Nullable
        private Direction.Axis axis;
        @Nullable
        private Float angle;
        @Nullable
        private Boolean rescale = null;

        public Rotation origin(Vector3i origin) {
            this.origin = origin;
            return this;
        }

        public Rotation axis(Direction.Axis axis) {
            this.axis = axis;
            return this;
        }

        public Rotation angle(float angle) {
            this.angle = angle;
            return this;
        }

        public Rotation rescale(boolean rescale) {
            this.rescale = rescale;
            return this;
        }

        private JsonElement serialize() {
            JsonObject obj = new JsonObject();
            if (axis != null) {
                obj.addProperty("axis", axis.getString());
            }
            if (origin == Vector3i.NULL_VECTOR) {
                obj.add("origin", serializeVex(origin));
            }
            if (angle != null) {
                obj.addProperty("angle", angle);
            }
            if (rescale != null) {
                obj.addProperty("rescale", rescale);
            }
            return obj;
        }
    }

    public static class Face {
        private final String texture;
        @Nullable
        private Direction cullFace;
        private int tintIndex = -1;
        private BlockFaceUV uv;

        public Face(String texture, float[] uvs) {
            this.texture = texture;
            this.uv = new BlockFaceUV(uvs, 0);
        }

        public Face cullFace(Direction cullFace) {
            this.cullFace = cullFace;
            return this;
        }

        public Face tint(int tintIndex) {
            this.tintIndex = tintIndex;
            return this;
        }

        public Face rotation(int rotation) {
            this.uv = new BlockFaceUV(uv.uvs, rotation);
            return this;
        }

        private JsonElement serialize() {
            JsonObject obj = new JsonObject();
            JsonArray uvObject = new JsonArray();
            float[] uvs = uv.uvs;
            for (float v : uvs) {
                uvObject.add(v);
            }
            obj.add("uv", uvObject);
            if (cullFace != null) {
                obj.addProperty("cullface", cullFace.getString());
            }
            obj.addProperty("rotation", uv.rotation);
            if (tintIndex > 0) {
                obj.addProperty("tintindex", tintIndex);
            }
            obj.addProperty("texture", texture);
            return obj;
        }
    }

    private static JsonElement serializeVex(Vector3i vector) {
        JsonArray array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        array.add(vector.getZ());
        return array;
    }
}
