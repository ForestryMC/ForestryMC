package forestry.core.owner;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

public class GameProfileDataSerializer implements DataSerializer<Optional<GameProfile>> {
	public static final GameProfileDataSerializer INSTANCE = new GameProfileDataSerializer();

	public static void register() {
		DataSerializers.registerSerializer(INSTANCE);
	}

	private GameProfileDataSerializer() {

	}

	@Override
	public void write(PacketBuffer buf, Optional<GameProfile> value) {
		if (!value.isPresent()) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			GameProfile gameProfile = value.get();
			buf.writeUuid(gameProfile.getId());
			buf.writeString(gameProfile.getName());
		}
	}

	@Override
	public Optional<GameProfile> read(PacketBuffer buf) throws IOException {
		if (buf.readBoolean()) {
			UUID uuid = buf.readUuid();
			String name = buf.readStringFromBuffer(1024);
			GameProfile gameProfile = new GameProfile(uuid, name);
			return Optional.of(gameProfile);
		} else {
			return Optional.absent();
		}
	}

	@Override
	public DataParameter<Optional<GameProfile>> createKey(int id) {
		return new DataParameter<>(id, this);
	}
}
