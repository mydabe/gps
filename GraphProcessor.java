import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.io.FileInputStream;

/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * 
 * @author Brandon Fain
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * 
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    private Point[] points;
    private HashMap<Integer, HashSet<Integer>> edges = new HashMap<>();

    public void initialize(FileInputStream file) throws Exception {
        // TODO: Implement initialize
        Scanner sc = new Scanner(file);

        int vertices = sc.nextInt();
        points = new Point[vertices];
        for (int i = 0; i < vertices; i++) {
            edges.put(i, new HashSet<Integer>());
        }

        int numEdges = sc.nextInt();

        for (int i = 0; i < vertices; i++) {
            String name = sc.next();
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            points[i] = new Point(x, y);
        }

        for (int i = 0; i < numEdges; i++) {
            String name;
            int from = sc.nextInt();
            int to = sc.nextInt();
            edges.get(from).add(to);
            edges.get(to).add(from);
            if (!sc.hasNextInt() & sc.hasNext())
                name = sc.next();
        }

        sc.close();
    }

    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * 
     * @param p A point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO: Implement nearestPoint
        Point nearest = points[0];
        double min = p.distance(points[0]);
        for (int i = 1; i < points.length; i++) {
            if (p.distance(points[i]) < min) {
                min = p.distance(points[i]);
                nearest = points[i];
            }
        }

        return nearest;
    }

    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points,
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * 
     * @param start Beginning point. May or may not be in the graph.
     * @param end   Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        // TODO Implement routeDistance
        double distance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            distance += route.get(i).distance(route.get(i + 1));
        }

        return distance;
    }

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * 
     * @param p1 one point
     * @param p2 another point
     * @return true if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        // TODO: Implement connected
        int p1Index = getIndex(p1);
        int p2Index = getIndex(p2);

        HashSet<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(p1Index);
        visited.add(p1Index);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            if (current == p2Index) {
                return true;
            }
            for (int neighbor : edges.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * 
     * @param start Beginning point.
     * @param end   Destination point.
     * @return The shortest path [start, ..., end].
     * @throws InvalidAlgorithmParameterException if there is no such route,
     *                                            either because start is not
     *                                            connected to end or because start
     *                                            equals end.
     */
    public List<Point> route(Point start, Point end) throws InvalidAlgorithmParameterException {
        // TODO: Implement route
        int startVertex = getIndex(start);
        int endVertex = getIndex(end);

        if (startVertex == endVertex)
            return new ArrayList<Point>();

        int[] path = new int[points.length];
        path[startVertex] = startVertex;

        double[] distance = new double[points.length];
        Arrays.fill(distance, Double.MAX_VALUE);
        distance[startVertex] = 0;

        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Double.compare(distance[o1], distance[o2]);
            }
        };

        PriorityQueue<Integer> queue = new PriorityQueue<>(comparator);

        queue.add(startVertex);

        HashSet<Integer> visited = new HashSet<>();

        while (!queue.isEmpty()) {

            int current = queue.poll();
            if (visited.contains(current))
                continue;
            visited.add(current);

            if (current == endVertex) {
                return constructPath(path, startVertex, endVertex);
            }
            for (int neighbor : edges.get(current)) {
                if (distance[neighbor] == Double.MAX_VALUE) {
                    distance[neighbor] = distance[current] + points[current].distance(points[neighbor]);
                    path[neighbor] = current;
                    queue.add(neighbor);
                }
            }
        }

        throw new InvalidAlgorithmParameterException("No route found");
    }

    private List<Point> constructPath(int[] path, int startVertex, int endVertex) {
        HashSet<Point> set = new HashSet<>();
        List<Point> result = new LinkedList<>();
        int current = endVertex;
        while (current != startVertex) {
            if (set.contains(points[current])) {
                System.out.println("somethign wrong");
                Collections.reverse(result);
                return result;
            }
            result.add(points[current]);
            set.add(points[current]);
            current = path[current];
        }
        result.add(points[startVertex]);
        Collections.reverse(result);
        return result;
    }

    private int getIndex(Point p) {
        for (int i = 0; i < points.length; i++) {
            if (points[i].equals(p)) {
                return i;
            }
        }
        return -1;
    }

}
