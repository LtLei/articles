import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphSearchSample {
    public static void main(String[] args) {
        // 邻接矩阵存储下的DFS和BFS，对应的图见文章
        AMGraph<String> amGraph = new AMGraph(9);
        amGraph.insertVertex("A");
        amGraph.insertVertex("B");
        amGraph.insertVertex("C");
        amGraph.insertVertex("D");
        amGraph.insertVertex("E");
        amGraph.insertVertex("F");
        amGraph.insertVertex("G");
        amGraph.insertVertex("H");
        amGraph.insertVertex("I");

        amGraph.insertEdge(0, 1);
        amGraph.insertEdge(0, 5);
        amGraph.insertEdge(1, 2);
        amGraph.insertEdge(1, 8);
        amGraph.insertEdge(1, 6);
        amGraph.insertEdge(5, 6);
        amGraph.insertEdge(5, 4);
        amGraph.insertEdge(2, 3);
        amGraph.insertEdge(8, 3);
        amGraph.insertEdge(6, 3);
        amGraph.insertEdge(6, 7);
        amGraph.insertEdge(4, 3);
        amGraph.insertEdge(4, 7);
        amGraph.insertEdge(3, 7);

        amGraph.DFS();

        System.out.println();
        System.out.println();

        amGraph.BFS();
    }

}

/**
 * 邻接矩阵存储图
 * 
 * 以无向图为例子
 * 
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
    /**
     * 访问数组
     */
    private boolean[] visited;

    public AMGraph(int n) {
        vertexList = new ArrayList<>(n);
        visited = new boolean[n];
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

    private void DFS(int i) {
        // 标记当前元素已经访问
        visited[i] = true;
        System.out.println("当前访问顶点：" + getVertexByIndex(i));

        int next = getFirstNeighbor(i);

        while (next != -1) {
            if (!visited[next]) {
                DFS(next);
            }
            next = getNextNeighbor(i, next);

        }
    }

    /**
     * 深度优先搜索
     */
    public void DFS() {
        for (int i = 0; i < vertexList.size(); i++) {
            visited[i] = false;
        }
        // 非连通图，不同的连通分量要单独进行DFS
        for (int i = 0; i < vertexList.size(); i++) {
            if (!visited[i]) {
                DFS(i);
            }
        }
    }

    private void BFS(int i) {
        // 标记当前元素已经访问
        visited[i] = true;
        System.out.println("当前访问顶点：" + getVertexByIndex(i));

        int cur, next;
        LinkedList<Integer> queue = new LinkedList<>();
        queue.addLast(i);
        while (!queue.isEmpty()) {
            cur = queue.removeFirst();
            next = getFirstNeighbor(cur);
            while (next != -1) {
                if (!visited[next]) {
                    // 标记当前元素已经访问
                    visited[next] = true;
                    System.out.println("当前访问顶点：" + getVertexByIndex(next));
                    queue.addLast(next);
                }
                next = getNextNeighbor(cur, next);
            }
        }
    }

    /**
     * 广度优先搜索
     */
    public void BFS() {
        for (int i = 0; i < vertexList.size(); i++) {
            visited[i] = false;
        }
        for (int i = 0; i < vertexList.size(); i++) {
            if (!visited[i]) {
                BFS(i);
            }
        }
    }
}