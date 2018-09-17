import java.util.Arrays;

/**
 * 查找的演示
 */
public class SearchSample{
    public static void main(String[] args) {
        // 线性表查找
        int[] linearTable = {1,30,4,9,2,18,10,29,20,0,25};
        int searchData = 10;

        int pos = searchLinear1(linearTable, searchData);
        System.out.println("Find data at " + pos);

        System.out.println("----------------------------");

        // 优化，位置0哨兵
        int[] linearTable2 = {0,1,30,4,9,2,18,10,29,20,0,25};
        int searchData2 = 10;
        pos = searchLinear2(linearTable2, searchData2);
        System.out.println("Find data at " + pos);

        System.out.println("----------------------------");

        int[] orderedTable = {0, 2, 3, 5, 7, 10, 19,37, 50};
        pos = searchOrdered(orderedTable,5);
        System.out.println("Find data at " + pos);

        int[] fibonacciTable = {5,13,29,46,68,72,85,98,101};
        pos = searchFibonacci(fibonacciTable, 72);
        System.out.println("Find data at " + pos);
    }

    /**
     * 普通未优化查找。
     */
    private static int searchLinear1(int[] table, int data){
        // len一定要先计算出来
        int len = table.length;

        for (int i = 0; i < len; i++) {
            if(table[i] == data){
                return i;
            }    
        }
        return -1;
    }

    /**
     * 位置0作为哨兵，不存储实际数据，减少 i 与 len 的比较
     */
    private static int searchLinear2(int[] table, int data){
        int len = table.length;

        table[0] = data;
    
        int i = len-1;
        while (table[i]!=data) {
            i--;
        }

        return i;
    }

    /**
     * 折半（二分）法查找
     */
    private static int searchOrdered(int[] table, int data){
        int low, high, mid;

        low = 0;
        high = table.length-1;
        
        while(low<=high){
            mid = (low+high)/2;
            if(data<table[mid]){
                high = mid-1;
            }else if(data>table[mid]){
                low = mid+1;
            }else{
                return mid;
            }
        }

        return -1;
    }

    //生成斐波那契数列
    private static int[] fibonacci() {
        int maxsize = 20;
        int[] f=new int[maxsize];
        f[0]=0;
        f[1]=1;
        for (int i = 2; i < maxsize; i++) {
            f[i]=f[i-1]+f[i-2];
        }
        return f;
    }

    /**
     * 斐波那契查找
     */
    private static int searchFibonacci(int[] a,int key) {
        int low=0;
        int high=a.length-1;
        int k=0; //斐波那契分割数值下标
        int mid=0;
        int f[]=fibonacci(); //获得斐波那契数列
        //获得斐波那契分割数值下标
        while (high>f[k]-1) {
            k++;
        }

        //利用Java工具类Arrays 构造新数组并指向 数组 a[]
        int[] temp=Arrays.copyOf(a, f[k]);

        //对新构造的数组进行 元素补充
        for (int i = high+1; i < temp.length; i++) {
            temp[i]=a[high];
        }

        while (low<=high) {
            //由于前面部分有f[k-1]个元素
            mid=low+f[k-1]-1;
            if (key<temp[mid]) {//关键字小于切割位置元素 继续在前部分查找
                high=mid-1;
                /*全部元素=前部元素+后部元素
                 * f[k]=f[k-1]+f[k-2]
                 * 因为前部有f[k-1]个元素,所以可以继续拆分f[k-1]=f[k-2]+f[k-3]
                 * 即在f[k-1]的前部继续查找 所以k--
                 * 即下次循环 mid=f[k-1-1]-1
                 * */
                k--;
            }
            else if (key>temp[mid]) {//关键字大于切个位置元素 则查找后半部分
                low=mid+1;
                /*全部元素=前部元素+后部元素
                 * f[k]=f[k-1]+f[k-2]
                 * 因为后部有f[k-2]个元素,所以可以继续拆分f[k-2]=f[k-3]+f[k-4]
                 * 即在f[k-2]的前部继续查找 所以k-=2
                 * 即下次循环 mid=f[k-1-2]-1
                 * */
                k-=2;
            }
            else {
                if (mid<=high) {
                    return mid;
                }
                else {
                    return high;
                }
            }
        }
        return -1;
    }
}

