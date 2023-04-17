import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        parseFile();

    }

    public static Map<String, String> parseFile() throws FileNotFoundException {

        ArrayList<String> degiskenler = new ArrayList<String>();
        String fileName = "karnaugh2.txt";

        Scanner scanner = new Scanner(new File(fileName));
        System.out.println(fileName + " dosyasi okundu.");

        while (scanner.hasNext()) {

            degiskenler.add(scanner.next());

        }
        int degiskenIndex = degiskenler.indexOf("degiskenler:");
        int diyagramIndex = degiskenler.indexOf("diyagram:");

        List<String> degiskenler1 = degiskenler.subList(degiskenIndex + 1, diyagramIndex);
        List<String> diyagram = degiskenler.subList(diyagramIndex + 1, degiskenler.size());

        for (int i = 0; i < degiskenler1.size(); i++) {
            if (degiskenler1.get(i).contains(",")) {
                degiskenler1.set(i, degiskenler1.get(i).replace(",", ""));
            }
        }

        Map<String, String> karnoughMap = new HashMap<>();
        String[] cols = { "00", "01", "11", "10" };
        String[] rows;
        if (diyagram.size() == 8) {
            rows = new String[] { "0", "1" };
        } else {
            rows = new String[] { "00", "01", "11", "10" };
        }

        String[] boolCoords = new String[diyagram.size() == 8 ? 8 : 16];

        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < cols.length; j++) {
                boolCoords[i * cols.length + j] = rows[i] + cols[j];
            }
        }
        for (int i = 0; i < boolCoords.length; i++) {
            karnoughMap.put(boolCoords[i], diyagram.get(i));
        }
        dogrulukTablosu(karnoughMap, degiskenler1);
        System.out.println("Fonsksiyon ifadeleri");
        findTerm(true, false, karnoughMap, degiskenler1);
        findTerm(false, false, karnoughMap, degiskenler1);
        findTerm(true, true, karnoughMap, degiskenler1);
        findTerm(false, true, karnoughMap, degiskenler1);

        return karnoughMap;

    }

    public static void dogrulukTablosu(Map<String, String> karnaughMap, List<String> degiskenler) {

        List<String> sortedKeys = new ArrayList<>(karnaughMap.keySet());
        Collections.sort(sortedKeys);
        System.out.println("Dogruluk Tablosu");
        for (String x : degiskenler) {
            System.out.print(x);
        }
        System.out.println(" F");

        for (String key : sortedKeys) {
            System.out.println(key + " " + karnaughMap.get(key));
        }

    }

    public static void findTerm(boolean isMinterm, boolean isCompliment, Map<String, String> karnaughMap,
            List<String> degiskenler) {
        List<String> mintermCoords = new ArrayList<String>();
        List<String> mintermFunction = new ArrayList<String>();
        List<String> boolString = new ArrayList<String>();
        ArrayList<ArrayList<String>> boolStringList = new ArrayList<ArrayList<String>>();

        String minOrMaxterm = isMinterm ? "1" : "0";

        if (isCompliment) {

            for (String key : karnaughMap.keySet()) {
                karnaughMap.replace(key, karnaughMap.get(key).equals("1") ? "0" : "1");
            }
        }

        for (String key : karnaughMap.keySet()) {

            if (karnaughMap.get(key).equals(minOrMaxterm)) {
                mintermCoords.add(key);
            }
        }
        Collections.sort(mintermCoords);

        for (String coord : mintermCoords) {

            char[] bools = coord.toCharArray();
            for (int i = 0; i < bools.length; i++) {
                boolString.add(String.valueOf(bools[i]));
            }
            boolStringList.add(new ArrayList<>() {
                {
                    addAll(boolString);
                }

            });
            boolString.clear();
        }
        for (ArrayList<String> row : boolStringList) {
            String func = "";
            if (isMinterm) {

                for (int i = 0; i < row.size(); i++) {
                    String funcTerm = row.get(i).equals(minOrMaxterm) ? degiskenler.get(i)
                            : compliment(degiskenler.get(i));

                    func = func + funcTerm;

                }
                mintermFunction.add(func);
                func = "";

            } else {
                for (int i = 0; i < row.size(); i++) {
                    String funcTerm = row.get(i).equals(minOrMaxterm) ? degiskenler.get(i)
                            : compliment(degiskenler.get(i));

                    func = func + funcTerm + " + ";

                }
                func = func.substring(0, func.length() - 3);
                mintermFunction.add("(" + func + ")");
                func = "";
            }
        }
        String f = isCompliment ? "F'" : "F";
        if (isMinterm) {

            System.out.println(f + " = " + String.join(" + ", mintermFunction));
        } else {

            System.out.println(f + " = " + String.join(".", mintermFunction));
        }

        if (isCompliment) {

            for (String key : karnaughMap.keySet()) {
                karnaughMap.replace(key, karnaughMap.get(key).equals("1") ? "0" : "1");
            }
        }
    }

    public static String compliment(String value) {

        return value + "'";
    }
}
