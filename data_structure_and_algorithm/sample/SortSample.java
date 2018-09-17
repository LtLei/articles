public class SortSample{
    public static void main(String[] args) {
        int[] arr = {3,1,10,8,2,6,7,4,9,5,0};
        // int[] arr = {8,2,9,3,10,15,4,7,9,20,1,5,11,22,13,28,29,0,16,17,26,21,6,31,29,19,27,28,32,12};
        // int[] arr = {1,3,9,2,4,6,7,5};
        System.out.println("Origin arr : ");
        for (int var : arr) {
            System.out.print(var + " ");
        }

        System.out.println();

        System.out.println("Sorted arr : ");
        // bubbleSort(arr);
        // selectSort(arr);
        // insertSort(arr);
        // shellSort(arr);
        // heapSort(arr);
        // mergeSort(arr);
        // mergeSort2(arr);
        // quickSort(arr);
        countingSort(arr);
        for (int var : arr) {
            System.out.print(var + " ");
        }
    }

    private static void print(int[] arr){
        for (int var : arr) {
            System.out.print(var + " ");
        }
    }

    private static void swap(int[] arr, int m, int n){
        int temp = arr[m];
        arr[m] = arr[n];
        arr[n] = temp;
    }

    /**
     * 冒泡排序
     */
    private static void bubbleSort(int[] arr){
        int len = arr.length;

        for (int i = 0; i < len-1; i++) {
            for (int j=0; j < len-1-i; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr, j, j+1);
                }
            }

            System.out.println("After " + i + " times sort, arr is :");
            print(arr);
            System.out.println();
        }
    }

    /**
     * 冒泡排序改进
     */
    private static void bubbleSort1(int[] arr){
        int len = arr.length;
        boolean flag = false;
        for (int i = 0; i < len-1; i++) {
            flag = false;
            for (int j=0; j < len-1-i; j++) {
                if(arr[j]>arr[j+1]){
                    swap(arr, j, j+1);
                    flag = true;
                }
            }

            System.out.println("After " + i + " times sort, arr is :");
            print(arr);
            System.out.println();
        }
    }

    /**
     * 简单选择排序
     */
    private static void selectSort(int[] arr){
        int len = arr.length;
        int min;
        for (int i = 0; i < len; i++) {
            min = i;
            for (int j = i+1; j < len; j++) {
                if(arr[min]>arr[j]){
                    min = j;
                }
            }
            if(i!=min){
                swap(arr,i,min);
            }
        }
    }

    /**
     * 直接插入排序
     */
    private static void insertSort(int[] arr){
        int len = arr.length;
        int temp;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < i; j++) {
                if(arr[i]<arr[j]){
                    temp = arr[i];
                    // j及以后的记录向后移动一位，然后把当前值存放在j的位置
                    System.arraycopy(arr,j,arr,j+1,i-j);
                    arr[j] = temp;
                }
            }
        }
    }

    /**
     * 希尔排序
     */
    private static void shellSort(int[] arr){
        int len = arr.length;
        int inc = len;
        // 设置间隔值
        for (inc=len/2; inc>0; inc/=2) {
            // i 从inc走到len，j正好可以把所有子数组遍历一次
            // j会先比较每个子数组的第一个值，再第二个值，这样横向进行遍历
            for (int i = inc; i < len; i++) {
                for (int j = i; j>=inc && arr[j]<arr[j-inc]; j-=inc) {
                    swap(arr,j,j-inc);
                }
                print(arr);
                System.out.println();
            }
        }
    }
    
    /**
     * 希尔排序，间隔3*inc+1
     */
    private static void shellSort1(int[] arr) {
        //首先根据数组的长度确定增量的最大值
        int inc=1;
        // 按h * 3 + 1得到增量序列的最大值
        while(inc <= arr.length / 3)
            inc = inc * 3 + 1;
        //进行增量查找和排序
        while(inc>=1){           
            for(int i=inc;i<arr.length;i++){
                for(int j=i;j >= inc && arr[j] < arr[j-inc];j -= inc){
                    swap(arr,j,j-inc);
                }
                print(arr);
                System.out.println();
            }
            inc = inc/3;
        } 
    }

    /**
     * 堆排序
     */
    private static void heapSort(int[] arr){
        int len = arr.length;

        // 从最后一个有孩子的结点开始，逐一进行堆的调整
        for (int i = (len-2)/2; i>=0; i--) {
            heapAdjust(arr,i,len);
        }


        // 对于一个堆，最大值一定在根结点，也就是在数组位置0，把它换到数组最后，然后对剩余的数据再进行一次堆的调整
        for (int i = len-1; i>0; i--) {
            System.err.println(""+i);
            // 把最大值放在数组的最后
            swap(arr,0,i);

            // 剩余的值进行堆的调整
            heapAdjust(arr,0,i);
        }
    }

    /**
     * 堆的调整
     * root：子树的根结点位置
     * len：当前排序数组的长度
     */
    private static void heapAdjust(int[] arr, int root, int len){
        if(len<=0)return;
        int temp;
        // 根结点的值先保存
        temp = arr[root];
        // i是这个结点的左孩子，或者是它孩子的左孩子
        for (int i=2*root+1; i<len; i=2*i+1) {
            if(i<len-1 && arr[i]<arr[i+1]){
                // 寻找到两个孩子的较大者
                i++;
            }
            // 根结点的值比两个孩子都大，就不需要再调整了
            if(temp>=arr[i]){
                break;
            }
            // 把根结点的值记为这个较大的孩子的值
            arr[root] = arr[i];
            // 再向下一级子树遍历
            root=i;
        }
        // 最后把temp的值存放在空置的位置
        arr[root] = temp;
    }

    /**
     * 归并排序
     */
    private static void mergeSort(int[] arr){
        int[] temp = new int[arr.length];
        mergeSort(arr,temp,0,arr.length-1);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right){
        if(left<right){
            int mid = (left+right)/2;
            mergeSort(arr,temp,left,mid);
            mergeSort(arr,temp,mid+1,right);
            merge(arr,temp,left,mid,right);
        }
    }

    /**
     * 子数组归并算法，归并完成结果拷贝至原数组。
     * 
     * @param arr 原数组
     * @param temp 交换数组
     * @param left 最左坐标值
     * @param mid 中点坐标值
     * @param right 最右坐标值
     */
    private static void merge(int[] arr, int[] temp, int left, int mid, int right){
        int i = left;
        int j = mid+1;
        int k = 0;
        while(i<=mid && j<=right){
            if(arr[i]<arr[j]){
                temp[k++] = arr[i++];
            }else{
                temp[k++] = arr[j++];
            }
        }

        while(i<=mid){
            temp[k++] = arr[i++];
        }
        while(j<=right){
            temp[k++] = arr[j++];
        }

        k=0;
        while (left<=right) {
            arr[left++] = temp[k++];
        }
    }

    /**
     * 非递归实现归并排序
     */
    private static void mergeSort2(int[] arr){
        int len = arr.length;
        int[] temp = new int[len];
        int k=1;

        System.err.println("len = " + len);

        while (k<len) {
            mergePass(arr,temp,k,len);
            k*=2;
            mergePass(temp,arr,k,len);
            k*=2;
        }

    }

    /**
     * @param s 表示要合并的每个子数组的长度 
     */
    private static void mergePass(int[] arr, int[] temp, int s, int len) {
        int i=0;
        // 合并后数组left = i, mid = i+s-1, right = i+2s-1
        // 最后有可能还有一组数据或者不到两组数据
        while (i<len-2*s) {
            merge2(arr,temp,i,i+s-1,i+2*s-1);
            i+=2*s;
        }
        // 最后还有两组数据
        if (i<len-s) {
            merge2(arr, temp, i, i+s-1, len-1);
        }else{
            // 只有最后一组数据
            for (int j = i; j < len; j++) {
                temp[j] = arr[j];
            }
        }
    }

    /**
     * 子数组归并算法，归并完成结果保留在当前的temp数组中，且下标从left开始。
     * 
     * @param arr 原数组
     * @param temp 交换数组
     * @param left 最左坐标值
     * @param mid 中点坐标值
     * @param right 最右坐标值
     */
    private static void merge2(int[] arr, int[] temp, int left, int mid, int right){
        int i = left;
        int j = mid+1;
        int k = left;
        while(i<=mid && j<=right){
            if(arr[i]<arr[j]){
                temp[k++] = arr[i++];
            }else{
                temp[k++] = arr[j++];
            }
        }

        while(i<=mid){
            temp[k++] = arr[i++];
        }
        while(j<=right){
            temp[k++] = arr[j++];
        }
    }

    /**
     * 快速排序
     */
    private static void quickSort(int[] arr){
        qSort(arr,0,arr.length-1);
    }

    private static void qSort(int[] arr, int low, int high) {
        int pivot;
        // 递归
        // if(low<high){
        //     pivot = partition(arr,low,high);
        //     qSort(arr,low,pivot-1);
        //     qSort(arr,pivot+1,high);
        // }
        // 迭代代替递归
        while(low<high){
            pivot = partition(arr,low,high);
            qSort(arr,low,pivot-1);
            low = pivot+1;
        }
    }

    private static int partition(int[] arr, int low, int high) {
        // 三数取中法,把中间值存放在low中
        int mid = low + (high-low)/2;
        if (arr[low]>arr[high]) {
            swap(arr, low, high);
        }
        if (arr[mid]>arr[high]) {
            swap(arr,mid,high);
        }
        if (arr[low]>arr[mid]) {
            swap(arr,low,mid);
        }

        int pivotKey = arr[low];

        // 暂存关键字
        int temp = pivotKey;

        while (low<high) {
            while (low<high&&arr[high]>=pivotKey) {
                high--;
            }
            arr[low] = arr[high];
            //swap(arr,low,high);
            while (low<high&&arr[low]<=pivotKey) {
                low++;
            }
            arr[high] = arr[low];
            // swap(arr,low,high);
        }
        // 恢复关键字
        arr[low] = temp;
        return low;
    }

    /** 
     * 计数排序
     */
    private static void countingSort(int[] arr){
        int len = arr.length;
        // 获取最大值
        int max = arr[0];
        for (int i = 1; i < len; i++) {
            if(max<arr[i]){
                max = arr[i];
            }
        }
        // 创建max+1个桶，从0-max
        int[] bucket = new int[max+1];
        for (int i = 0; i < len; i++) {
            // 每获取一个数，就把它放在编号和其一致的桶中
            bucket[arr[i]]++;
        }
        int j = 0;
        for (int i = 0, bLen = bucket.length; i < bLen; i++) {
            // 遍历桶，按顺序恢复每条数据
            for (int k = bucket[i]; k > 0; k--) {
                arr[j++] = i;
            }
        }
    }
}

