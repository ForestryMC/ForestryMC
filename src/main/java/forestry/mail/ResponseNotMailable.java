package forestry.mail;

import net.minecraft.network.chat.Component;

import forestry.api.mail.IPostalState;

public class ResponseNotMailable implements IPostalState {
	private final IPostalState state;

	public ResponseNotMailable(IPostalState state) {
		this.state = state;
	}

	@Override
	public boolean isOk() {
		return false;
	}

	@Override
	public Component getDescription() {
		return Component.translatable("for.chat.mail.response.not.mailable.format", state.getDescription());
	}
}
