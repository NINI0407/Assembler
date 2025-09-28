import java.util.*;

public class Main {
    public static void main(String[] args) {
        FileWR f = new FileWR("Figure2.15.txt");
        ArrayList<String[]> rawData = f.readCode();

        Pass1 p1 = new Pass1();
        rawData = p1.litteral(rawData);
        p1.locctr(rawData);

        Pass2 p2 = new Pass2(rawData, p1.getLocRes(), p1.getFormat(), p1.getSymTab());
        p2.createObj();

        int c = 1;
        ArrayList<String> loc = p1.getLocRes();

        for (int i = 0; i < p2.getObjecCode().size(); i++) {
            String s = "";
            for (int j = 0; j < rawData.get(i).length; j++) {
                if (rawData.get(i).length >= 4 && rawData.get(i)[1].charAt(0) == '.' && j >= 2) {
                    s = s + rawData.get(i)[j] + " ";
                } else if (j == rawData.get(i).length - 1) {
                    s += String.format("%-16s", rawData.get(i)[j]);
                } else {
                    s += String.format("%-8s", rawData.get(i)[j]);
                }
            }
            if (rawData.get(i).length == 2) {
                s += "        ";
            }
            if (c <= loc.size()) {
                System.out.printf("%-3s%-8s", c - 1, loc.get(c - 1));
                c++;
            }
            System.out.print(s);
            System.out.printf("%-15s", p2.getObjecCode().get(i));
            System.out.println();
        }

        // Object Program

        // count section number
        ArrayList<Integer> csect = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            if (rawData.get(i).length >= 2
                    && (rawData.get(i)[1].equals("CSECT") || rawData.get(i)[1].equals("START"))) {
                csect.add(i);
            }
        }
        csect.add(rawData.size());

        for (int i = 0; i < csect.size() - 1; i++) {
            // header record
            System.out.print("H" + rawData.get(csect.get(i))[0] + "   ");
            System.out.printf("%06X",
                    rawData.get(csect.get(i))[1].equals("START") ? Integer.parseInt(rawData.get(csect.get(i))[2]) : 0);
            System.out.print("00"+p2.getCSECTLen().get(i));
            System.out.println();

            // define record
            if (i == 0) {
                System.out.print("D");
                for (int j = 0; j < p2.getExtdef().get(i).size(); j++) {
                    System.out.print(
                            p2.getExtdef().get(i).get(j) + "00" + p1.getSymTab().get(p2.getExtdef().get(i).get(j)));
                }
                System.out.println();
            }

            // refer record
            System.out.print("R");
            for (int j = 0; j < p2.getExtref().get(i).size(); j++) {
                System.out.print(p2.getExtref().get(i).get(j));
            }
            System.out.println();

            // text record
            ArrayList<String> obj = p2.getObjecCode();
            String s = "",text="T00";
            ArrayList<String> textRec=new ArrayList<>();
            for(int j=csect.get(i);j< csect.get(i+1);j++){
                if(rawData.get(j).length>=2 && (rawData.get(j)[1].equals("RESW") || rawData.get(j)[1].equals("RESW") || rawData.get(j)[1].equals("LTORG") || s.length()+obj.get(j).length()>60)){
                    if(s.length()!=0){
                        text+=String.format("%02X",s.length()/2);
                        text+=s;
                        textRec.add(text);
                        s="";
                        text="T00";
                    }
                    
                }
                if(!obj.get(j).equals("")){
                    if(s.length()==0){
                        text+=loc.get(j);
                    }
                    s+=obj.get(j);
                }
            }
            text+=String.format("%02X",s.length()/2);
            text+=s;
            textRec.add(text);
            for(String d:textRec){
                System.out.println(d);
            }

            // modification record
            for (int j = 0; j < p2.getModRec().size(); j++) {
                if (p2.getModRecSec().get(j) == i) {
                    System.out.println(p2.getModRec().get(j));
                }
            }

            // end record
            if (i == 0) {
                System.out.println("E" + String.format("%06X", Integer.parseInt(rawData.get(csect.get(i))[2])));
            } else {
                System.out.println("E");
            }

            System.out.println();
        }

    }
}
