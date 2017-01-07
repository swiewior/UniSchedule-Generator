/**
 * Created by Dave on 2016-11-27.
 */

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Scanner;

public class Preferences {

    public String czyje_preferencje;

    private int[][] dni_tygodnia = new int[5][];

    public Preferences(String file) {
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            String czyje_preferencje = (String) jsonObject.get("czyje preferencje");
            System.out.println(czyje_preferencje);

            //if (isInteger(czyje_preferencje, nr_grupy));

            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("preferencje planu");
//            Iterator<String> iterator = msg.iterator();
//            while (iterator.hasNext()) {
//                System.out.println(iterator.next());
//            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean isInteger(String s, int radix) {
        Scanner sc = new Scanner(s.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }


}
