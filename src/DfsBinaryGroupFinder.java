import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        if (image == null) {
            throw new NullPointerException("The image array cannot be null.");
        }

        for (int[] row : image) {
            if (row == null) {
                throw new NullPointerException("The subarrays cannot be null.");
            }
        }
        
        if (image.length == 0 || image[0].length == 0) {
            throw new IllegalArgumentException("The array is Invalid.");
        }

        boolean[][] visited = new boolean[image.length][image[0].length];
        List<Group> groups = new ArrayList<>();
         
        // traversing all pixels in the image
        for (int r = 0; r < image.length; r++) {
            for (int c = 0; c < image[0].length; c++) {
                if (image[r][c] == 1 && !visited[r][c]) {
                    List<int[]> pixels = findConnectedGroupsHelper(image, new int[]{r, c}, visited);
                    groups.add(createGroupFromPixels(pixels));
                }
            }
        }

        // sorting in descending order
        groups.sort(Collections.reverseOrder());   
        return groups;
    }

    public List<int[]> findConnectedGroupsHelper(int[][] image, int[] start, boolean[][] visited) {
        List<int[]> connectedPixels = new ArrayList<>();
        Stack<int[]> stack = new Stack<>();
        stack.push(start);
    
        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];
    
            if (row < 0 || row >= image.length || col < 0 || col >= image[0].length) continue;
            if (visited[row][col] || image[row][col] == 0) continue;
    
            visited[row][col] = true;
            connectedPixels.add(new int[]{row, col});
    
            // pushing the valid neighbors (UP, DOWN, LEFT, RIGHT)
            stack.push(new int[]{row - 1, col}); 
            stack.push(new int[]{row + 1, col}); 
            stack.push(new int[]{row, col - 1}); 
            stack.push(new int[]{row, col + 1}); 
        }
    
        return connectedPixels;
    }
    
    public static List<int[]> possibleDirections(int[][] image, int[] current) {
        int curR = current[0];
        int curC = current[1];
    
        List<int[]> moves = new ArrayList<>();
    
        // Directions array for UP, DOWN, LEFT, and RIGHT
        int[][] directions = new int[][]{
            {-1, 0}, 
            {1, 0}, 
            {0, -1}, 
            {0, 1}   
        };

        for (int[] direction : directions) {
            int newR = curR + direction[0];
            int newC = curC + direction[1];
    
            if (newR >= 0 && newR < image.length && newC >= 0 && newC < image[0].length && image[newR][newC] == 1) {
                moves.add(new int[]{newR, newC});
            }
        }
    
        return moves;
    }

    public Group createGroupFromPixels(List<int[]> pixels) {
        int sumOfX = 0;
        int sumOfY = 0;

        for (int[] p : pixels) {
            sumOfY += p[0]; // y is the row
            sumOfX += p[1]; // x is the column
        }

        int size = pixels.size();
        int centroidX = sumOfX / size;
        int centroidY = sumOfY / size;

        return new Group(size, new Coordinate(centroidX, centroidY));
    }
}
