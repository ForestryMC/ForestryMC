package forestry.core.owner;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import com.mojang.authlib.GameProfile;

public class GameProfileDataSerializer implements DataSerializer<Optional<GameProfile>> {
	public static final GameProfileDataSerializer INSTANCE = new GameProfileDataSerializer();

	public static void register() {
		DataSerializers.registerSerializer(INSTANCE);
	}

	private GameProfileDataSerializer() {

	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Override
	public void write(PacketBuffer buf, Optional<GameProfile> value) {
		if (!value.isPresent()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			GameProfile gameProfile = value.get();
			buf.writeUniqueId(gameProfile.getId());
			buf.writeString(gameProfile.getName());
		}
	}

	@Override
	public Optional<GameProfile> read(PacketBuffer buf) {
		if (buf.readBoolean()) {
			UUID uuid = buf.readUniqueId();
			String name = buf.readString(1024);
			GameProfile gameProfile = new GameProfile(uuid, name);
			return Optional.of(gameProfile);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public DataParameter<Optional<GameProfile>> createKey(int id) {
		return new DataParameter<>(id, this);
	}

	@Override
	public Optional<GameProfile> copyValue(Optional<GameProfile> value) {
		return value;
	}
}
