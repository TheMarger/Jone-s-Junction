
public class Can extends Throwable {

    private static final int DefaultThrowRadius = 10;
    private static final int DefaultSoundValue = 5;
    private static final int DefaultSoundArea = 10;

    public Can() {
        super("Can", DefaultThrowRadius, DefaultSoundValue, DefaultSoundArea);
    }
}
