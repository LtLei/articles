import java.util.*;

public class TopoSort {
    public static void main(String[] args) {
        // 拓扑排序
        ATGraph<String> atGraph = new ATGraph<>(9);
        atGraph.insertVertex(new ATVertex<>(0, "v0"));
        atGraph.insertVertex(new ATVertex<>(1, "v1"));
        atGraph.insertVertex(new ATVertex<>(1, "v2"));
        atGraph.insertVertex(new ATVertex<>(1, "v3"));
        atGraph.insertVertex(new ATVertex<>(1, "v4"));
        atGraph.insertVertex(new ATVertex<>(1, "v5"));
        atGraph.insertVertex(new ATVertex<>(2, "v6"));
        atGraph.insertVertex(new ATVertex<>(2, "v7"));
        atGraph.insertVertex(new ATVertex<>(2, "v8"));

        atGraph.insertEdge(0, 1);
        atGraph.insertEdge(1, 2);
        atGraph.insertEdge(1, 3);
        atGraph.insertEdge(1, 4);
        atGraph.insertEdge(1, 5);
        atGraph.insertEdge(2, 6);
        atGraph.insertEdge(3, 6);
        atGraph.insertEdge(4, 7);
        atGraph.insertEdge(5, 7);
        atGraph.insertEdge(6, 8);
        atGraph.insertEdge(7, 8);

        TopoSort topoSort = new TopoSort();
        boolean success = topoSort.topoSort(atGraph);
        System.out.println("成功："+success);

        // 关键路径

//        atGraph.insertVertex(new ATVertex<>(0, "v0"));
//        atGraph.insertVertex(new ATVertex<>(1, "v1"));
//        atGraph.insertVertex(new ATVertex<>(1, "v2"));
//        atGraph.insertVertex(new ATVertex<>(2, "v3"));
//        atGraph.insertVertex(new ATVertex<>(2, "v4"));
//        atGraph.insertVertex(new ATVertex<>(1, "v5"));
//        atGraph.insertVertex(new ATVertex<>(1, "v6"));
//        atGraph.insertVertex(new ATVertex<>(2, "v7"));
//        atGraph.insertVertex(new ATVertex<>(1, "v8"));
//        atGraph.insertVertex(new ATVertex<>(2, "v9"));
//
//        atGraph.insertEdge(0, 1, 3);
//        atGraph.insertEdge(0, 2, 4);
//
//        atGraph.insertEdge(1, 3, 5);
//        atGraph.insertEdge(1, 4, 6);
//
//        atGraph.insertEdge(2, 3, 8);
//        atGraph.insertEdge(2, 5, 7);
//
//        atGraph.insertEdge(3, 4, 3);
//
//        atGraph.insertEdge(4, 6, 9);
//        atGraph.insertEdge(4, 7, 4);
//
//        atGraph.insertEdge(5, 7, 6);
//        atGraph.insertEdge(6, 9, 2);
//        atGraph.insertEdge(7, 8, 5);
//        atGraph.insertEdge(8, 9, 3);
//
//        TopoSort topoSort = new TopoSort();
//
//        topoSort.criticalPath(atGraph);
    }

    public <T> boolean topoSort(ATGraph<T> atGraph) {
        int count = 0;
        Queue<ATVertex<T>> queue = new LinkedList<>();
        for (int i = 0; i < atGraph.getLen(); i++) {
            if (atGraph.getVertex(i).getIn() == 0) {
                queue.offer(atGraph.getVertex(i));
            }
        }
        while (!queue.isEmpty()) {
            ATVertex<T> vertex = queue.poll();
            System.out.print(vertex.getData() + "->");
            count++;
            ATEdge<T> next = vertex.getNext();
            while (next != null) {
                ATVertex<T> nextVertex = next.getVertex();
                nextVertex.setIn(nextVertex.getIn() - 1);
                if (nextVertex.getIn() == 0) {
                    queue.offer(nextVertex);
                }
                next = next.getNext();
            }
        }

        return count >= atGraph.getLen();
    }

    public <T> void criticalPath(ATGraph<T> atGraph){
        Stack<ATVertex<T>> stack2 = new Stack<>();
        int[] earlestTimeVertex = new int[atGraph.getLen()];
        int[] latestTimeVertex = new int[atGraph.getLen()];

        topoSort(atGraph,earlestTimeVertex,stack2);
        for (int i = 0; i < atGraph.getLen(); i++) {
            // 先将最晚发生时间都设置为最长时间
            latestTimeVertex[i] = earlestTimeVertex[atGraph.getLen()-1];
        }

        // 从后向前，更新每个顶点的最晚发生时间
        while (!stack2.isEmpty()){
            ATVertex<T> vertex = stack2.pop();
            ATEdge<T> next = vertex.getNext();
            while (next!=null){
                ATVertex<T> nextVertex = next.getVertex();
                int nextIndex = atGraph.getVertexIndex(nextVertex);
                int index = atGraph.getVertexIndex(vertex);
                if (latestTimeVertex[nextIndex]-next.getWeight()<latestTimeVertex[index]){
                    latestTimeVertex[index] = latestTimeVertex[nextIndex]-next.getWeight();
                }

                next = next.getNext();
            }
        }

        int ete,lte;
        for (int i = 0; i < atGraph.getLen(); i++) {
            ATVertex<T> vertex = atGraph.getVertex(i);
            ATEdge<T> next = vertex.getNext();
            while (next!=null){
                ATVertex<T> nextVertex = next.getVertex();
                int nextIndex = atGraph.getVertexIndex(nextVertex);
                lte = latestTimeVertex[nextIndex]-next.getWeight();
                ete = earlestTimeVertex[i];
                if (ete==lte){
                    System.out.println("路径："+atGraph.getVertex(i).getData()+"->"+atGraph.getVertex(nextIndex).getData()+", 长度："+ next.getWeight());
                }
                next = next.getNext();
            }
        }
    }

    public <T> boolean topoSort(ATGraph<T> atGraph,int[] earlestTimeVertex,Stack<ATVertex<T>> stack2) {
        int count = 0;
        Queue<ATVertex<T>> queue = new LinkedList<>();
        for (int i = 0; i < atGraph.getLen(); i++) {
            if (atGraph.getVertex(i).getIn() == 0) {
                queue.offer(atGraph.getVertex(i));
            }
        }
        while (!queue.isEmpty()) {
            ATVertex<T> vertex = queue.poll();
            System.out.print(vertex.getData() + "->");
            //将排序的数据push到stack2中
            stack2.push(vertex);
            count++;
            //获取第一条边
            ATEdge<T> next = vertex.getNext();
            while (next != null) {
                //获取
                ATVertex<T> nextVertex = next.getVertex();
                nextVertex.setIn(nextVertex.getIn() - 1);
                if (nextVertex.getIn() == 0) {
                    queue.offer(nextVertex);
                }

                // 计算每个顶点可以执行的最早时间
                // 获取弧尾顶点下标
                int topIndex = atGraph.getVertexIndex(vertex);
                // 获取弧头顶点下标
                int index = atGraph.getVertexIndex(nextVertex);
                // 更新当前顶点可以发生的最早时间
                if (earlestTimeVertex[topIndex] + next.getWeight() > earlestTimeVertex[index]) {
                    earlestTimeVertex[index] = earlestTimeVertex[topIndex] + next.getWeight();
                }
                next = next.getNext();
            }
        }

        return count >= atGraph.getLen();
    }
}

/**
 * 图的邻接表存储
 * AdjacencyTableGraph
 */
class ATGraph<T> {
    private List<ATVertex<T>> vertexList;
    private int len = 0;

    public ATGraph(int n) {
        vertexList = new ArrayList<>(n);
    }

    public void insertVertex(ATVertex<T> vertex) {
        vertexList.add(vertex);
        len++;
    }

    public void insertEdge(int v1, int v2, int weight) {
        if (v1 > len || v2 > len) {
            return;
        }
        ATVertex<T> vertex1 = vertexList.get(v1);
        ATVertex<T> vertex2 = vertexList.get(v2);
        ATEdge<T> edge = vertex1.getNext();
        if (edge == null) {
            vertex1.setNext(new ATEdge<>(vertex2, weight));
        } else {
            while (edge.getNext() != null) {
                edge = edge.getNext();
            }
            edge.setNext(new ATEdge(vertex2, weight));
        }
    }

    public void insertEdge(int v1, int v2) {
        insertEdge(v1, v2, 1);
    }

    public int getLen() {
        return len;
    }

    public ATVertex<T> getVertex(int i) {
        return vertexList.get(i);
    }

    public int getVertexIndex(ATVertex<T> vertex) {
        return vertexList.indexOf(vertex);
    }
}

class ATEdge<T> {
    private ATVertex<T> vertex;
    private int weight;
    private ATEdge<T> next;

    public ATEdge(ATVertex<T> vertex) {
        this.vertex = vertex;
    }

    public ATEdge(ATVertex<T> vertex, int weight) {
        this.vertex = vertex;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }


    public ATVertex<T> getVertex() {
        return vertex;
    }

    public void setVertex(ATVertex<T> vertex) {
        this.vertex = vertex;
    }

    public ATEdge<T> getNext() {
        return next;
    }

    public void setNext(ATEdge<T> next) {
        this.next = next;
    }
}

/**
 * AdjacencyTable数据结构，为了更好的完成拓扑排序，增加入度字段
 */
class ATVertex<T> {
    private int in = 0;
    private T data;
    private ATEdge<T> next;

    public ATVertex(int in, T data) {
        this.in = in;
        this.data = data;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ATEdge<T> getNext() {
        return next;
    }

    public void setNext(ATEdge<T> next) {
        this.next = next;
    }
}