import java.util.*;
import java.io.*;

public class FileWR {

    private File file;

    public FileWR(String path) {
        this.file = new File(path);
    }

    public ArrayList<String[]> readCode() {

        ArrayList<String[]> res = new ArrayList<>();

        try {
            FileReader reader = new FileReader(file);
            BufferedReader data = new BufferedReader(reader);

            for (String line = data.readLine(); line != null; line = data.readLine()) {
                if (line.length() != 0) {
                    String[] temp = line.split("\\s+");
                    res.add(temp);
                } else {
                    String[] temp = { " " };
                    res.add(temp);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error");
        }

        return res;
    }

    public void writeCode(ArrayList<String> source) {

        try {
            FileWriter writer = new FileWriter(file);
            writer.write("");

            for (String line : source) {
                writer.append(line.toUpperCase() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error");
        }

    }
}