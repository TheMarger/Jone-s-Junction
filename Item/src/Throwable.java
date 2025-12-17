public class Throwable extends item {

private int throwRadius;
private int soundValue;
private int soundArea;


public Throwable(String itemName, int throwRadius, int soundValue, int soundArea) {
super(itemName);
this.throwRadius = throwRadius;
this.soundValue = soundValue;
this.soundArea = soundArea;
}


public int getThrowRadius() {
return throwRadius;
}

public void setThrowRadius(int throwRadius) {
this.throwRadius = throwRadius;
}

public int getSoundValue() {
return soundValue;
}

public void setSoundValue(int soundValue) {
this.soundValue = soundValue;
}

public int getSoundArea() {
return soundArea;
}

public void setSoundArea(int soundArea) {
this.soundArea = soundArea;
}
}