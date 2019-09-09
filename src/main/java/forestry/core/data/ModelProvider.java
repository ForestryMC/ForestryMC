package forestry.core.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class ModelProvider<T extends IForgeRegistryEntry<T>> implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	protected final IForgeRegistry<T> registry;
	protected final String folder;
	protected final Map<T, ModelBuilder<T>> entryToBuilder = Maps.newLinkedHashMap();
	protected final DataGenerator generator;

	public ModelProvider(DataGenerator generator, IForgeRegistry<T> registry, String folder) {
		this.generator = generator;
		this.registry = registry;
		this.folder = folder;
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		this.entryToBuilder.clear();
		this.registerModels();
		entryToBuilder.forEach((key, builder) -> {
			if (key.getRegistryName() == null) {
				return;
			}
			JsonObject jsonobject = builder.serialize();
			Path path = this.makePath(key.getRegistryName());
			try {
				String s = GSON.toJson(jsonobject);
				String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
				if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
					Files.createDirectories(path.getParent());

					try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
						bufferedwriter.write(s);
					}
				}

				cache.func_208316_a(path, s1);
			} catch (IOException ioexception) {
				LOGGER.error("Couldn't save models to {}", path, ioexception);
			}

		});
	}

	protected abstract void registerModels();

	protected Path makePath(ResourceLocation location) {
		return this.generator.getOutputFolder().resolve("assets/" + location.getNamespace() + "/models/" + folder + "/" + location.getPath() + ".json");
	}

	@Override
	public String getName() {
		return "Model Provider";
	}

	public static class ModelBuilder<T extends IForgeRegistryEntry<T>> {
		public final T entry;
		@Nullable
		public ResourceLocation parent;
		public final Map<String, ResourceLocation> textures = new HashMap<>();
		public final List<Element> elements = new LinkedList<>();
		private boolean ambientOcclusion = true;

		public ModelBuilder(T entry) {
			this.entry = entry;
		}

		public ModelBuilder<T> element(Element element) {
			this.elements.add(element);
			return this;
		}

		public ModelBuilder<T> ambientOcclusion(boolean ambientOcclusion) {
			this.ambientOcclusion = ambientOcclusion;
			return this;
		}

		public ModelBuilder<T> parent(ResourceLocation parent) {
			this.parent = parent;
			return this;
		}

		public ModelBuilder<T> particle(ResourceLocation location) {
			return texture("particle", location);
		}

		public ModelBuilder<T> texture(String key, ResourceLocation location) {
			textures.put(key, location);
			return this;
		}

		public JsonObject serialize() {
			JsonObject object = new JsonObject();
			if (parent != null) {
				object.addProperty("parent", parent.toString());
			}
			JsonObject texturesObj = new JsonObject();
			for (Map.Entry<String, ResourceLocation> texture : textures.entrySet()) {
				texturesObj.addProperty(texture.getKey(), texture.getValue().toString());
			}
			object.add("textures", object);
			object.addProperty("ambientocclusion", ambientOcclusion);
			JsonArray elementsArray = new JsonArray();
			for (Element element : elements) {
				elementsArray.add(element.serialize());
			}
			object.add("elements", elementsArray);
			return object;
		}
	}

	public static class Element {
		private final Vec3i from;
		private final Vec3i to;
		private final Face[] faces = new Face[6];
		private boolean shade = true;
		@Nullable
		public Rotation rotation;

		public Element(Vec3i from, Vec3i to) {
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
				facesObj.add(direction.getName(), face.serialize());
			}
			obj.add("faces", facesObj);
			return obj;
		}
	}

	public static class Rotation {
		private Vec3i origin = Vec3i.NULL_VECTOR;
		@Nullable
		private Direction.Axis axis;
		private float angle;
		private boolean rescale = false;

		public Rotation origin(Vec3i origin) {
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
				obj.addProperty("axis", axis.getName());
			}
			if (origin == Vec3i.NULL_VECTOR) {
				obj.add("origin", serializeVex(origin));
			}
			obj.addProperty("angle", angle);
			obj.addProperty("rescale", rescale);
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
				obj.addProperty("cullface", cullFace.getName());
			}
			obj.addProperty("rotation", uv.rotation);
			if (tintIndex > 0) {
				obj.addProperty("tintindex", tintIndex);
			}
			obj.addProperty("texture", texture);
			return obj;
		}
	}

	private static JsonElement serializeVex(Vec3i vector) {
		JsonArray array = new JsonArray();
		array.add(vector.getX());
		array.add(vector.getY());
		array.add(vector.getZ());
		return array;
	}
}
