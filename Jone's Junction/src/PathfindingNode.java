import java.util.Objects;

public class PathfindingNode {
	int xPos, yPos;
	boolean walkable;
	int gCost = Integer.MAX_VALUE;
	int hCost;
	int fCost; 
	PathfindingNode parent; 
	PathfindingNode(int xPos, int yPos, boolean walkable)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.walkable = walkable;
	}
	
	public boolean sameNode(PathfindingNode other)
	{
		return this.xPos == other.xPos & this.yPos == other.yPos;
	}
	
	public int hashCode()
	{
		return Objects.hash(xPos, yPos);
	}
}