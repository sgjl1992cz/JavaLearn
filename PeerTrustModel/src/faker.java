import java.util.Random;

public class faker {

	public static void main(String[] args) {
		Random random = new Random();
		int base = 1*735/(1+1+1+1+1+1);
		System.out.println("base:"+base);
		System.out.println(base*9/10+random.nextInt(base*2/10));

	}

}
