
import java.util.List;

public class PatrolRoute {

    private List<Tile> points;
    private int index = 0;

    public PatrolRoute(List<Tile> points) {
        this.points = points;
    }

    public Tile getCurrentPoint() {
        return points.get(index);
    }

    public void moveToNext() {
        index = (index + 1) % points.size();
    }
}

