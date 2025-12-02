import java.util.*;

public class Pathfinding {
	private static int[][] directions = {
			{1,0,10}, {-1,0,10}, {0,1,10}, {0,-1,10},
			{1,1,14}, {1,-1,14}, {-1,1,14}, {-1,-1,14} };
	public static List<PathfindingNode> findPath(PathfindingNode[][] grid, PathfindingNode start, PathfindingNode end)
	{
		PriorityQueue<PathfindingNode> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
		Set<PathfindingNode> closedSet = new HashSet<>();
		start.gCost = 0;
		start.hCost = heuristicFunction(start, end);
		start.fCost = start.gCost + start.hCost;
		openSet.add(start);
		
		while(!openSet.isEmpty())
		{
			PathfindingNode current = openSet.poll();
			if(current.sameNode(end))
			{
				return reconstructPath(current);
			}
			closedSet.add(current);
			
			for(int directionIndex = 0; directionIndex < directions.length; directionIndex++)
			{
				int[] currentDirection = directions[directionIndex];
				int neighbourX = current.xPos + currentDirection[0];
				int neighbourY = current.yPos + currentDirection[1];
				int cost = currentDirection[2];
				
				if(neighbourX < 0 || neighbourY < 0 || neighbourX >= grid.length || neighbourY >= grid[0].length)
				{
					continue;
				}
				
				PathfindingNode neighbour = grid[neighbourX][neighbourY];
				
				if(!neighbour.walkable || closedSet.contains(neighbour))
				{
					continue;
				}
				if(cost == 14)
				{
					PathfindingNode n1 = grid[current.xPos + currentDirection[0]][current.yPos];
					PathfindingNode n2 = grid[current.xPos][current.yPos + currentDirection[1]];
					if(!n1.walkable || !n2.walkable)
					{
						continue;
					}
				}
				
				int tentativeG = current.gCost + cost;
				if(tentativeG < neighbour.gCost)
				{
					neighbour.gCost = tentativeG;
					neighbour.hCost = heuristicFunction(neighbour, end);
					neighbour.fCost = neighbour.gCost + neighbour.hCost;
					neighbour.parent = current;
					
					if(!openSet.contains(neighbour))
					{
						openSet.add(neighbour);
					}
				}
			}
		}
		return new ArrayList<>();
	}
	
	private static int heuristicFunction(PathfindingNode pointA, PathfindingNode pointB)
	{
		int distanceX = Math.abs(pointA.xPos - pointB.xPos);
		int distanceY = Math.abs(pointA.yPos - pointB.yPos);
		return 10 * (distanceX + distanceY) + (14 - 2 * 10) * Math.min(distanceX, distanceY);
	}
	
	private static List<PathfindingNode> reconstructPath(PathfindingNode end)
	{
		List<PathfindingNode> path = new ArrayList<>();
		PathfindingNode current = end;
		while(current != null)
		{
			path.add(current);
			current = current.parent;
		}
		Collections.reverse(path);
		return path;
	}
}
	
	