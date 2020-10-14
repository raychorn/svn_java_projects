/* fool with arrays and pass by reference variables */

class PassByReference {
  int OnetoZero (int arg[]) {
    int count = 0;

    for (int i = 0; i < arg.length; i++) {
      if (arg[i] == 1) {
	count++;
	arg[i] = 0;
      }
    }
    return count;
  }

  public static void main (String arg[]) {
    int arr[] = { 1, 3, 4, 5, 1, 1, 7 };
    PassByReference test = new PassByReference();
    int numOnes;
    
    System.out.print("Values of the array: [ ");
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println("]");

    numOnes = test.OnetoZero(arr);
    System.out.println("Number of Ones = " + numOnes);
    System.out.print("New values of the array: [");
    for (int i = 0; i < arr.length; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println(" ]");

  }
}


