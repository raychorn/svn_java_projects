public class  VectorTest {
    public int  array[];

    public int  sum() {
        int[]  localArray = array;
        int    sum        = 0;

        for (int  i = localArray.length;  --i >= 0;  )
            sum += localArray[i];
        return sum;
    }
}
