package multiplayer;

import java.io.IOException;
import java.io.InputStream;

import multiplayer.conversion.ConverterInStream;

public interface InputReceiver {
	public abstract boolean useInput(ConverterInStream in) throws IOException;
	public abstract int requestType();
}
