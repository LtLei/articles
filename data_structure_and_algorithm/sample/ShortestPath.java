import java.util.ArrayList;
import java.util.List;

public class ShortestPath {
    public static void main(String[] args) {
        AMGraph<String> amGraph = new AMGraph<>(9);
        amGraph.insertVertex("v0");
        amGraph.insertVertex("v1");
        amGraph.insertVertex("v2");
        amGraph.insertVertex("v3");
        amGraph.insertVertex("v4");
        amGraph.insertVertex("v5");
        amGraph.insertVertex("v6");
        amGraph.insertVertex("v7");
        amGraph.insertVertex("v8");

        amGraph.insertEdge(0, 1, 1);
        amGraph.insertEdge(0, 2, 5);

        amGraph.insertEdge(1, 2, 3);
        amGraph.insertEdge(1, 3, 7);
        amGraph.insertEdge(1, 4, 5);

        amGraph.insertEdge(2, 4, 1);
        amGraph.insertEdge(2, 5, 7);

        amGraph.insertEdge(3, 4, 2);
        amGraph.insertEdge(3, 6, 3);

        amGraph.insertEdge(4, 5, 3);
        amGraph.insertEdge(4, 6, 6);
        amGraph.insertEdge(4, 7, 9);

        amGraph.insertEdge(5, 7, 5);

        amGraph.insertEdge(6, 7, 2);
        amGraph.insertEdge(6, 8, 7);

        amGraph.insertEdge(7, 8, 4);

        ShortestPath sp = new ShortestPath();
//        sp.floyd(amGraph);
        sp.dijkstra(amGraph, 0);
    }

    /**
     * 迪杰斯特拉算法
     *
     * @param amGraph
     * @param fromIndex 从某个顶点开始，计算它到其他每个顶点的最短路径
     */
    public void dijkstra(AMGraph<String> amGraph, int fromIndex) {
        int len = amGraph.getVertexNum();
        // 存储从fromIndex到其他各顶点的最短路径下标
        int[] p = new int[len];
        // 存储从fromIndex到其他各顶点的最短路径的权值和
        int[] d = new int[len];
        // 标记求得了顶点fromIndex到其他各定点的最短路径
        boolean[] finded = new boolean[len];
        // 初始化数据
        for (int toIndex = 0; toIndex < len; toIndex++) {
            finded[toIndex] = false;
            d[toIndex] = amGraph.getWeight(fromIndex, toIndex);
            p[toIndex] = 0;
        }

        // fromIndex到自己的路径长度为0，并且不需要再求它的最短路径了
        d[fromIndex] = 0;
        finded[fromIndex] = true;

        int min = 0;
        int k = -1;
        // 求fromIndex到toIndex的最短路径
        for (int toIndex = 1; toIndex < len; toIndex++) {
            min = Integer.MAX_VALUE;
            // 寻找距离fromIndex最近的顶点
            for (int i = 0; i < len; i++) {
                if (!finded[i] && d[i] < min) {
                    // i 离 fromIndex最近
                    k = i;
                    min = d[i];
                }
            }

            // 找到了最近的点
            finded[k] = true;

            // 更新剩余顶点的距离值
            for (int i = 0; i < len; i++) {
                // 如果经过 k 之后的距离比直接到 i 的距离近，就更新距离
                if (!finded[i] && amGraph.getWeight(k, i) != Integer.MAX_VALUE && (min + amGraph.getWeight(k, i) < d[i])) {
                    d[i] = min + amGraph.getWeight(k, i);
                    p[i] = k;
                }
            }

            System.out.println();
            System.out.println();
            for (int i = 0; i < len; i++) {
                System.out.print(p[i] + "\t");
            }
        }
    }

    /**
     * 弗洛伊德算法
     *
     * @param amGraph
     */
    public void floyd(AMGraph<String> amGraph) {
        int len = amGraph.getVertexNum();
        int[][] d = new int[len][len];
        int[][] p = new int[len][len];

        // 初始化d和p数组
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                d[i][j] = amGraph.getWeight(i, j);
                p[i][j] = j;
            }
        }

        for (int k = 0; k < len; k++) {
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    if (d[i][k] != Integer.MAX_VALUE && d[k][j] != Integer.MAX_VALUE && d[i][j] > d[i][k] + d[k][j]) {
                        d[i][j] = d[i][k] + d[k][j];
                        p[i][j] = p[i][k];
                    }
                }
            }
        }

        printD(len, d);
        printP(len, p);
    }

    private void printD(int len, int[][] d) {
        System.out.println();
        System.out.println();
        System.out.println();
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                System.out.print(d[i][j] + "\t");
            }
            System.out.println();
        }
    }

    private void printP(int len, int[][] p) {
        System.out.println();
        System.out.println();
        System.out.println();
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                System.out.print(p[i][j] + "\t");
            }
            System.out.println();
        }
    }
}

/**
 * 邻接矩阵存储图
 * <p>
 * 以无向图为例子
 * <p>
 * AdjacencyMatrixGraph
 */
class AMGraph<T> {
    /**
     * 顶点集合
     */
    private List<T> vertexList;
    /**
     * 边集合
     */
    private int[][] edges;
    /**
     * 边的个数
     */
    private int edgeNum;

    public AMGraph(int n) {
        vertexList = new ArrayList<>(n);
        edges = new int[n][n];
        edgeNum = 0;
    }

    /**
     * 插入顶点
     *
     * @param vertex
     */
    public void insertVertex(T vertex) {
        vertexList.add(vertex);
    }

    /**
     * 插入边
     *
     * @param v1
     * @param v2
     */
    public void insertEdge(int v1, int v2) {
        insertEdge(v1, v2, 1);
    }

    /**
     * 插入边，有weight值
     *
     * @param v1
     * @param v2
     * @param weight
     */
    public void insertEdge(int v1, int v2, int weight) {
        edges[v1][v2] = weight;
        edges[v2][v1] = weight;
        edgeNum++;
    }

    public int getWeight(int v1, int v2) {
        int weight = edges[v1][v2];
        if (weight == 0) {
            weight = v1 == v2 ? 0 : Integer.MAX_VALUE;
        }
        return weight;
    }

    /**
     * 获取顶点个数
     *
     * @return
     */
    public int getVertexNum() {
        return vertexList.size();
    }

    public T getVertexByIndex(int index) {
        return vertexList.get(index);
    }

    /**
     * 获取边的个数
     *
     * @return
     */
    public int getEdgNum() {
        return edgeNum;
    }

    /**
     * 获取第一个邻接顶点
     *
     * @param index
     * @return
     */
    public int getFirstNeighbor(int index) {
        for (int i = 0; i < vertexList.size(); i++) {
            if (edges[index][i] > 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据前一个邻接结点的下标，获取下一个邻接结点
     *
     * @param v1Index 目标结点
     * @param v2Index 前一个邻接结点的下标
     * @return
     */
    public int getNextNeighbor(int v1Index, int v2Index) {
        for (int j = v2Index + 1; j < vertexList.size(); j++) {
            if (edges[v1Index][j] > 0) {
                return j;
            }
        }
        return -1;
    }
}