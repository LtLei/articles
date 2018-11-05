import java.util.ArrayList;
import java.util.List;

public class MinimumSpanningTree {
    private static final int INFINITE = Integer.MAX_VALUE;

    public static void main(String[] args) {
        MinimumSpanningTree mst = new MinimumSpanningTree();

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

        amGraph.insertEdge(0, 1, 10);
        amGraph.insertEdge(0, 5, 11);
        amGraph.insertEdge(1, 2, 18);
        amGraph.insertEdge(1, 6, 16);
        amGraph.insertEdge(1, 8, 12);
        amGraph.insertEdge(2, 8, 8);
        amGraph.insertEdge(2, 3, 22);
        amGraph.insertEdge(3, 8, 21);
        amGraph.insertEdge(3, 6, 24);
        amGraph.insertEdge(3, 7, 16);
        amGraph.insertEdge(3, 4, 20);
        amGraph.insertEdge(4, 7, 7);
        amGraph.insertEdge(4, 5, 26);
        amGraph.insertEdge(5, 6, 17);
        amGraph.insertEdge(6, 7, 19);

//        mst.prim(amGraph);

        EdgesGraph<String> eg = new EdgesGraph<>(amGraph);
        Edge[] edges = eg.getEdges();
        mst.kruskal(edges);
    }

    public <T> void prim(AMGraph<T> graph) {
        int len = graph.getVertexNum();
        int min = 0;
        // 相关顶点的坐标
        int[] adjvex = new int[len];
        // 最小代价
        int[] lowcost = new int[len];
        // 将位置0的顶点加入生成树，设置lowcost为0
        lowcost[0] = 0;
        adjvex[0] = 0;

        for (int i = 1; i < len; i++) {
            // 和v0相连的顶点的权值存入数组
            lowcost[i] = graph.getWeight(0, i);
            // 全部坐标都初始化为v0下标
            adjvex[i] = 0;
        }

        for (int i = 1; i < len; i++) {
            // INFINITE是一个不可能的值，这里设置为Int的最大值
            min = INFINITE;
            int j = 1, k = 0;
            while (j < len) {
                // 循环剩下的全部顶点，寻找lowcoast
                if (lowcost[j] != 0 && lowcost[j] < min) {
                    min = lowcost[j];
                    k = j;
                }
                j++;
            }

            System.out.println("当前顶点中最小权值的边是：(" + adjvex[k] + ", " + k + ")" + "最小值为：" + min);

            // 把此顶点的权值设为0
            lowcost[k] = 0;
            for (j = 1; j < len; j++) {
                // 把当前的k顶点加入已选列表，并更新剩余顶点的权值
                if (lowcost[j] != 0 && graph.getWeight(k, j) < lowcost[j]) {
                    lowcost[j] = graph.getWeight(k, j);
                    adjvex[j] = k;
                }
            }
        }

    }

    public void kruskal(Edge[] edges) {
        int len = edges.length;
        // 定义一个数组，保存每个顶点的父结点，也就是它所在的树结构中的父结点
        int[] parent = new int[len];
        for (int i = 0; i < len; i++) {
            parent[i] = 0;
        }

        int begin,end;
        for (int i = 0; i < len; i++) {
            // begin顶点所在树的根结点
            begin = find(parent,edges[i].getBegin());
            // end顶点所在树的根结点
            end = find(parent,edges[i].getEnd());
            // 不在同一棵树上
            if (end != begin){
                parent[end] = begin;
                System.out.println("加入边：(" + edges[i].getBegin()+", "+edges[i].getEnd() +") , weight = "+edges[i].getWeight());
            }
        }
    }

    private int find(int[] parent, int find){
        // 找到这棵树的根结点
        while (parent[find]>0){
            find = parent[find];
        }
        return find;
    }
}

class Edge {
    private int begin;
    private int end;
    private int weight;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

class EdgesGraph<T> {
    private int len = 0;
    private Edge[] mEdges;

    public EdgesGraph(AMGraph<T> amGraph) {
        int graphLen = amGraph.getVertexNum();
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < graphLen; i++) {
            for (int j = i; j < graphLen; j++) {
                int weight = amGraph.getWeight(i, j);
                if (weight != Integer.MAX_VALUE) {
                    Edge edge = new Edge();
                    edge.setBegin(i);
                    edge.setEnd(j);
                    edge.setWeight(weight);
                    edges.add(edge);
                    len++;
                }
            }
        }

        // 为edges排序
        mEdges = new Edge[len];
        edges.toArray(mEdges);
        bubbleSort(mEdges);
    }

    public Edge[] getEdges() {
        return mEdges;
    }

    private void bubbleSort(Edge[] arr) {
        int len = arr.length;

        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (arr[j].getWeight() > arr[j + 1].getWeight()) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    private void swap(Edge[] arr, int i, int j) {
        Edge temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
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

