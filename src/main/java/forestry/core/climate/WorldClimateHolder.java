package forestry.core.climate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import net.minecraftforge.common.util.Constants;

import forestry.api.climate.IClimateState;
import forestry.api.climate.IClimateTransformer;
import forestry.api.climate.IWorldClimateHolder;
import forestry.api.climate.Position2D;
import forestry.api.core.INbtWritable;
import forestry.core.utils.NBTUtilForestry;

import it.unimi.dsi.fastutil.longs.Long2LongArrayMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class WorldClimateHolder extends WorldSavedData implements IWorldClimateHolder {
	private static final TransformerData DEFAULT_DATA = new TransformerData(0L, ClimateStateHelper.INSTANCE.absent(), 0, false, new long[0]);

	static final String NAME = "forestry_climate";

	private static final String TRANSFORMERS_KEY = "Transformers";
	private static final String CHUNK_KEY = "Chunk";
	private static final String TRANSFORMERS_DATA_KEY = "Data";
	private static final String STATE_DATA_KEY = "Data";
	private static final String POS_KEY = "Pos";
	private static final String RANGE_KEY = "Range";
	private static final String CIRCULAR_KEY = "circular";
	private static final String CHUNKS_KEY = "Chunks";

	private final Long2ObjectMap<TransformerData> transformers = new Long2ObjectOpenHashMap<>();
	private final Long2ObjectMap<long[]> transformersByChunk = new Long2ObjectOpenHashMap<>();
	private final Long2LongMap chunkUpdates = new Long2LongArrayMap();

	@Nullable
	private World world;

	public WorldClimateHolder(String name) {
		super(name);
	}

	public void setWorld(@Nullable World world) {
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		transformers.clear();
		NBTTagList transformerData = nbt.getTagList(TRANSFORMERS_KEY, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < transformerData.tagCount(); i++) {
			NBTTagCompound tagCompound = transformerData.getCompoundTagAt(i);
			TransformerData data = new TransformerData(tagCompound);
			transformers.put(data.position, data);
		}
		NBTTagList chunkData = nbt.getTagList(CHUNK_KEY, Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < chunkData.tagCount(); i++) {
			NBTTagCompound tagCompound = chunkData.getCompoundTagAt(i);
			long pos = tagCompound.getLong(POS_KEY);
			long[] chunkTransformers = NBTUtilForestry.getLongArray(tagCompound.getTag(TRANSFORMERS_DATA_KEY));
			transformersByChunk.put(pos, chunkTransformers);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList transformerData = new NBTTagList();
		for (Map.Entry<Long, TransformerData> entry : transformers.entrySet()) {
			TransformerData data = entry.getValue();
			transformerData.appendTag(data.writeToNBT(new NBTTagCompound()));
		}
		compound.setTag(TRANSFORMERS_KEY, transformerData);
		NBTTagList chunkData = new NBTTagList();
		for (Map.Entry<Long, long[]> entry : transformersByChunk.entrySet()) {
			NBTTagCompound tagCompound = new NBTTagCompound();
			tagCompound.setLong(POS_KEY, entry.getKey());
			tagCompound.setTag(TRANSFORMERS_DATA_KEY, new NBTTagLongArray(entry.getValue()));
			chunkData.appendTag(tagCompound);
		}
		compound.setTag(CHUNK_KEY, chunkData);
		return compound;
	}

	@Override
	public IClimateState getClimate(long position) {
		return transformers.getOrDefault(position, DEFAULT_DATA).climateState;
	}

	@Override
	public void addTransformer(long chunkPos, long transformerPos) {
		long[] oldData = transformersByChunk.get(chunkPos);
		long[] newData;
		if (oldData != null) {
			for (long pos : oldData) {
				if (pos == transformerPos) {
					return;
				}
			}
			newData = Arrays.copyOf(oldData, oldData.length + 1);
		} else {
			newData = new long[1];
		}
		newData[newData.length - 1] = transformerPos;
		transformersByChunk.put(chunkPos, newData);
		setDirty(true);
		markChunkUpdate(chunkPos);
	}

	@Override
	public void removeTransformer(long chunkPos, long transformerPos) {
		long[] oldData = transformersByChunk.get(chunkPos);
		if (oldData == null) {
			return;
		}
		for (long pos : oldData) {
			if (pos == transformerPos) {
				if (oldData.length == 1) {
					transformersByChunk.remove(chunkPos);
					chunkUpdates.remove(chunkPos);
				} else {
					long[] newData = Arrays.copyOf(oldData, oldData.length - 1);
					transformersByChunk.put(chunkPos, newData);
				}
				setDirty(true);
				markChunkUpdate(chunkPos);
				return;
			}
		}
	}

	private void markChunkUpdate(long chunkPos) {
		if (world != null) {
			chunkUpdates.put(chunkPos, world.getTotalWorldTime());
		}
	}

	@Override
	public void updateTransformer(IClimateTransformer transformer) {
		BlockPos position = transformer.getCoordinates();
		long longPos = position.toLong();
		TransformerData data = transformers.get(longPos);
		if (data != null) {
			boolean needChunkUpdate = data.range != transformer.getRange() || data.circular != transformer.isCircular() || data.chunks.length == 0;
			boolean needClimateUpdate = !data.climateState.equals(transformer.getCurrent());
			data.climateState = transformer.getCurrent().toImmutable();
			if (needChunkUpdate) {
				data.circular = transformer.isCircular();
				data.range = transformer.getRange();
				data.chunks = updateTransformerChunks(transformer, needClimateUpdate);
			} else if (needClimateUpdate) {
				for (long chunkPos : data.chunks) {
					markChunkUpdate(chunkPos);
				}
			}
		} else {
			long[] transformerChunks = updateTransformerChunks(transformer, false);
			transformers.put(longPos, new TransformerData(longPos, transformer.getCurrent().toImmutable(), transformer.getRange(), transformer.isCircular(), transformerChunks));
		}
		setDirty(true);
	}

	private long[] updateTransformerChunks(IClimateTransformer transformer, boolean forceDirty) {
		BlockPos transformerPos = transformer.getCoordinates();
		long longPos = transformerPos.toLong();
		int range = transformer.getRange();
		Set<Long> chunkSet = new HashSet<>();
		for (int x = transformerPos.getX() - range; x <= transformerPos.getX() + range; x += 16) {
			for (int z = transformerPos.getZ() - range; z <= transformerPos.getZ() + range; z += 16) {
				int chunkX = x >> 4;
				int chunkZ = z >> 4;
				long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
				addTransformer(chunkPos, longPos);
				chunkSet.add(chunkPos);
				if (forceDirty) {
					markChunkUpdate(chunkPos);
				}
			}
		}
		return chunkSet.stream().mapToLong(l -> l).toArray();
	}

	@Override
	public void removeTransformer(IClimateTransformer transformer) {
		removeTransformerChunks(transformer);
		transformers.remove(transformer.getCoordinates().toLong());
		setDirty(true);
	}

	private void removeTransformerChunks(IClimateTransformer transformer) {
		BlockPos transformerPos = transformer.getCoordinates();
		long longPos = transformerPos.toLong();
		TransformerData data = transformers.get(longPos);
		if (data == null) {
			return;
		}
		for (long chunkPos : data.chunks) {
			removeTransformer(chunkPos, longPos);
		}
	}

	@Override
	public int getRange(long position) {
		return transformers.getOrDefault(position, DEFAULT_DATA).range;
	}

	@Override
	public boolean isCircular(long position) {
		return transformers.getOrDefault(position, DEFAULT_DATA).circular;
	}

	@Override
	public boolean isPositionInTransformerRange(long position, Position2D blockPos) {
		BlockPos pos = BlockPos.fromLong(position);
		int range = getRange(position);
		if (isCircular(position)) {
			double distance = Math.round(blockPos.getDistance(pos));
			return range > 0.0F && distance <= range;
		}
		return MathHelper.abs(blockPos.getX() - pos.getX()) <= range && MathHelper.abs(blockPos.getZ() - pos.getZ()) <= range;
	}

	@Override
	public IClimateState getState(BlockPos pos) {
		long chunkPos = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
		if (!transformersByChunk.containsKey(chunkPos)) {
			return ClimateStateHelper.INSTANCE.absent();
		}
		double transformerCount = 0;
		IClimateState state = ClimateStateHelper.INSTANCE.mutableZero();
		for (long transformerPos : transformersByChunk.get(chunkPos)) {
			if (isPositionInTransformerRange(transformerPos, new Position2D(pos))) {
				state = state.add(getClimate(transformerPos));
				transformerCount++;
			}
		}
		return transformerCount > 0 ? state.multiply(1.0D / transformerCount).toImmutable() : ClimateStateHelper.INSTANCE.absent();
	}

	@Override
	public boolean hasTransformers(BlockPos pos) {
		return transformersByChunk.containsKey(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
	}

	@Override
	public long getLastUpdate(BlockPos pos) {
		return getLastUpdate(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
	}

	@Override
	public long getLastUpdate(long chunkPos) {
		return chunkUpdates.get(chunkPos);
	}

	private static class TransformerData implements INbtWritable {
		private IClimateState climateState = ClimateStateHelper.INSTANCE.absent();
		private int range;
		private boolean circular;
		private long position;
		private long[] chunks = new long[0];

		private TransformerData(long position, IClimateState climateState, int range, boolean circular, long[] chunks) {
			this.position = position;
			this.climateState = climateState;
			this.range = range;
			this.circular = circular;
			this.chunks = chunks;
		}

		private TransformerData(NBTTagCompound nbt) {
			position = nbt.getLong(POS_KEY);
			range = nbt.getInteger(RANGE_KEY);
			climateState = ClimateStateHelper.INSTANCE.create(nbt.getCompoundTag(STATE_DATA_KEY));
			circular = nbt.getBoolean(CIRCULAR_KEY);
			chunks = NBTUtilForestry.getLongArray(nbt.getTag(CHUNKS_KEY));
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt.setLong(POS_KEY, position);
			nbt.setTag(STATE_DATA_KEY, ClimateStateHelper.INSTANCE.writeToNBT(new NBTTagCompound(), climateState));
			nbt.setInteger(RANGE_KEY, range);
			nbt.setBoolean(CIRCULAR_KEY, circular);
			nbt.setTag(CHUNKS_KEY, new NBTTagLongArray(chunks));
			return nbt;
		}
	}
}
