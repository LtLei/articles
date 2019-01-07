public class KMPSample{
    public static void main(String[] args) {
        char[] origin = "hello world, i love java".toCharArray();
        char[] target = "ld".toCharArray();
        int index = indexOf(origin, target);
        System.out.println("Find at index : " + index);

        char[] target1 = "abcadc".toCharArray();
        
        index = indexOfKMP(origin, target);
        System.out.println("Find at index : " + index);
    }
    
    /**
     * 暴力匹配算法查询目标字符串在当前字符串首次出现位置
     */
    private static int indexOf(char[] origin, char[] target){
        int originLen = origin.length;
        int targetLen = target.length;

        if(originLen==0 || targetLen == 0 || originLen<targetLen){
            return -1;
        }

        if(origin == target) return 0;

        int i=0,j=0;
        while (i<originLen && j<targetLen) {
            if(origin[i] == target[j]){
                // 如果相同就一一比较
                i++;
                j++;
            }else{
                // 不相同，i指向上次匹配第一个字符的下一位，j清零
                i = i-j+1;
                j=0;
            }
        }

        if(j>targetLen-1) return i-targetLen;
        else return -1;
    }

    private static int indexOfKMP(char[] origin, char[] target){
        int originLen = origin.length;
        int targetLen = target.length;

        if(originLen==0 || targetLen == 0 || originLen<targetLen){
            return -1;
        }

        if(origin == target) return 0;

        int i=0,j=0;
        int[] next = getNext(target);

        while (i<originLen && j<targetLen) {
            if(j==-1 || origin[i] == target[j]){
                // 如果相同就一一比较,j=0表示不需要比较
                i++;
                j++;
            }else{
                // j返回到合适的位置，i不再需要回溯
                j = next[j];
            }
        }

        if(j>targetLen-1) return i-targetLen;
        else return -1;
    }

    private static int[] getNext(char[] target){
        int len = target.length;
        if(len==0){
            return new int[]{-1};
        }
        
        int[] next = new int[target.length];

        // j表示当前位置，k表示子串需比较的第一位。
        int j=0,k=-1;
        next[0] = -1;
        while (j<len-1) {
            if(k==-1 || target[j]==target[k]){
                j++;
                k++;
                next[j]=k;
            }else{
                k=next[k];
            }
        }

        return next;
    }
}