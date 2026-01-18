
public class FoodTray extends Throwable {

    private static final int DefaultThrowRadius = 15;
    private static final int DefaultSoundValue = 10;
    private static final int DefaultSoundArea = 15;

    public FoodTray() {
        super("Food Tray", DefaultThrowRadius, DefaultSoundValue, DefaultSoundArea);
    }
}
