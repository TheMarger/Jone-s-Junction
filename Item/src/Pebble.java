
public class Pebble extends Throwable {

	private static final int DefaultThrowRadius = 10;
	private static final int DefaultSoundValue = 3;
	private static final int DefaultSoundArea = 5;

	public Pebble() {
		super("Pebble", DefaultThrowRadius, DefaultSoundValue, DefaultSoundArea);
	}
}
