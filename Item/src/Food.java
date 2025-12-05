public class Food extends item {


private double staminaRestore;
private String spawnRule;


public Food(String itemName, double staminaRestore, String spawnRule) {
super(itemName);
this.staminaRestore = staminaRestore;
this.spawnRule = spawnRule;
}

public double getStaminaRestore() {
return staminaRestore;
}

public void setStaminaRestore(double staminaRestore) {
this.staminaRestore = staminaRestore;
}

public String getSpawnRule() {
return spawnRule;
}

public void setSpawnRule(String spawnRule) {
this.spawnRule = spawnRule;
}
}