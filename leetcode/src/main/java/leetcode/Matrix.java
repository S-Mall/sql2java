package leetcode;

import java.util.ArrayList;
import java.util.List;

public class Matrix {

    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if(matrix.length == 0 || matrix[0].length == 0)
            return result;
        for (int v1 : matrix[0]) {
            result.add(v1);
        }
        if(matrix.length == 1)
            return result;
        int[][] orig = new int[matrix.length-1][matrix[0].length];
        for(int i = 1; i < matrix.length; i++) {
            orig[i-1] = matrix[i];
        }

        int[][] temp = new int[orig[0].length][orig.length];
        while(orig.length > 1) {
            transpose(orig, temp);
            for (int v : temp[0]) {
                result.add(v);
            }
            if(temp.length == 1)
                return result;
            orig = new int[temp.length-1][temp[0].length];
            for(int i = 1; i < temp.length; i++) {
                orig[i-1] = temp[i];
            }
            temp = new int[orig[0].length][orig.length];
        }
        for(int i = orig[0].length-1; i >= 0 ; i--) {
            result.add(orig[0][i]);
        }

        return result;
    }

    static void transpose(int A[][], int B[][])
    {
        int N = A[0].length, M = A.length;
        int i, j;
        for (i = 0; i < N; i++)
            for (j = 0; j < M; j++)
                B[i][j] = A[j][N-i-1];
    }

    public static void main(String[] args) {
        //System.out.println(spiralOrder(new int[][] {}));
        System.out.println(spiralOrder(new int[][]{ {1,2,3,4},{5,6,7,8},{9,10,11,12}}));
        System.out.println(spiralOrder(new int[][]{ {1}}));
        System.out.println(spiralOrder(new int[][]{ {1}, {2}, {3}}));
        //System.out.println(spiralOrder(new int[][]{ {1}}));
        int[][] test = new int[][]{ {1,2,3,4},{5,6,7,8},{9,10,11,12}};
        int[][] trans = new int[test[0].length][test.length];
        int[][] test1 = new int[][]{ {5,6,7,8},{9,10,11,12}};
        int[][] trans1 = new int[test1[0].length][test1.length];
        int[][] test2 = new int[][]{ {7,11},{6,10},{5,9}};
        int[][] trans2 = new int[test2[0].length][test2.length];
        transpose(test, trans);
        transpose(test1, trans1);
        transpose(test2, trans2);
        System.out.println(trans);
    }
}
