import java.util.Random;

public class Dice {

    public static int[] throwDices(){
        int[] dices = new int[2];

        for(int i = 0; i<2; i++){
            Random rand = new Random();
            dices[i] = rand.nextInt(6) + 1;
        }
        return dices;
    }
}
