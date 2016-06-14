package de.l3s.utils.tools;
import java.util.Random;
/**
 * @author http://vingrad.ru/@sergej.z
 * Generator of human readable passwords.
 * Examples:
 * 11wzG9V, Hp6cE, yI0GL, LMIojMQ
 */
public class PasswordGenerator {
/**
 * Generated password consists only of letters and/or numbers.
 * Ex: 6n3k0ZjY
 * @param from : minimum letters
 * @param to : maximum letters
 * @return Password of length to-from letters
 */
public static String generate(int from, int to)
{
    String pass="";
    Random ran = new Random();
    int cntchars = from + ran.nextInt(to - from+1);
    
    for (int i = 0; i < cntchars; i++) {
        char next=0;
        int range=10;
        switch(ran.nextInt(3))
        {
        case 0:{next='0';range=10;}break;
        case 1:{next='a';range=26;}break;
        case 2:{next='A';range=26;}break;
        }
        pass+=(char)((ran.nextInt(range))+next);
    }
    return pass;
}
public static void main(String argv[])
{
    // 5 - 10 digits
    String t=generate(5,10);
    System.out.println("".toString()+t);
}

}