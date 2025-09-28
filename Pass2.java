import java.util.*;

public class Pass2 {
    ArrayList<String> gObjCode;
    ArrayList<String[]> pass1Data;
    ArrayList<String> pass1Loc;
    ArrayList<String> pass1Format;
    Map<String, String> pass1SymTab;
    ArrayList<ArrayList<String>> extdef = new ArrayList<>();
    ArrayList<ArrayList<String>> extref = new ArrayList<>();
    OPTAB optab = new OPTAB();
    Register register = new Register();
    ArrayList<String> modRec = new ArrayList<>();
    ArrayList<Integer> modRecSec = new ArrayList<>();
    int section = 0;
    ArrayList<String> csectLen = new ArrayList<>();

    public Pass2(ArrayList<String[]> pass1Data, ArrayList<String> pass1Loc, ArrayList<String> pass1Format,
            Map<String, String> pass1SymTab) {
        gObjCode = new ArrayList<>();
        this.pass1Data = pass1Data;
        this.pass1Format = pass1Format;
        this.pass1Loc = pass1Loc;
        this.pass1SymTab = pass1SymTab;
    }

    public ArrayList<String> getObjecCode() {
        return gObjCode;
    }

    public ArrayList<ArrayList<String>> getExtdef() {
        return extdef;
    }

    public ArrayList<ArrayList<String>> getExtref() {
        return extref;
    }

    public ArrayList<String> getModRec() {
        return modRec;
    }

    public ArrayList<Integer> getModRecSec() {
        return modRecSec;
    }

    public ArrayList<String> getCSECTLen() {
        return csectLen;
    }

    public void createObj() {

        // 0 => [], 1 => format 1, 2 => foramt 2, 3 => format 3, 4 => format 4
        // 5 => BYTEX, 6 => BYTEC, 7 => WORD, 8 => =X, 9 => =C, 10 => RSUB, 11 => EQU

        for (int r = 0; r < pass1Format.size(); r++) {

            int ni = 0;
            String x = "0", b = "0", p = "0", e = "0", disp = "0000";
            String[] temp = pass1Data.get(r);
            String f = pass1Format.get(r);
            String objc = "";


            if (temp.length >= 2 && temp[1].equals("RSUB")) {
                gObjCode.add("4F0000");
            }

            else if (f.equals("0")) {
                gObjCode.add("");
                if (temp[0] != " ") {
                    do_EXT(temp);
                    if (temp[1].equals("CSECT")) {
                        section++;
                    }
                }

            }

            else if (f.equals("1")) {
                String op = optab.getOPCode(temp[1]);
                gObjCode.add(op);
            }

            else if (f.equals("2")) {
                if (temp[2].length() == 1) {
                    objc = optab.getOPCode(temp[1]) + register.getReg(temp[2].substring(0, 1)) + "0";
                } else if (temp[2].length() == 3) {
                    objc = optab.getOPCode(temp[1]) + register.getReg(temp[2].substring(0, 1))
                            + register.getReg(temp[2].substring(2));
                }
                gObjCode.add(objc);
            }

            else if (f.equals("3")) {
                // op nixbpe disp
                // op ni

                if (temp.length >= 3 && temp[2].charAt(0) == '#') { // i
                    ni = 1;
                } else if (temp.length >= 3 && temp[2].charAt(0) == '@') { // n
                    ni = 2;
                } else {
                    ni = 3;
                }
                objc += String.format("%02X", (Integer.parseInt(optab.getOPCode(temp[1]), 16) + ni), 16);

                // x
                if (temp.length >= 3 && temp[2].contains(",")) {
                    x = "1";
                } else {
                    x = "0";
                }

                // bp disp
                if (temp.length >= 3 && temp[2].charAt(0) == '#') {
                    b = "0";
                    p = "0";
                    disp = String.format("%04X", Integer.parseInt(temp[2].substring(1)));
                } else {
                    if (x.equals("1")) { // array
                        String target = pass1SymTab.get(temp[2].substring(0, temp[2].indexOf(",")));
                        disp = String.format("%04X",
                                Integer.parseInt(target, 16) - Integer.parseInt(pass1Loc.get(r + 1), 16));
                    } else {
                        String target = pass1SymTab.get(temp[2]);
                        if (temp[2].charAt(0) == '#' || temp[2].charAt(0) == '@') {
                            target = pass1SymTab.get(temp[2].substring(1));
                        }
                        // System.out.println(target+" "+pass1Loc.get(r + 1));
                        disp = String.format("%04X",
                                Integer.parseInt(target, 16) - Integer.parseInt(pass1Loc.get(r + 1), 16));
                    }

                    if (disp.length() < 5 || disp.charAt(0) == 'F') {
                        b = "0";
                        p = "1";
                    }
                }
                // e
                e = "0";

                int dec = Integer.parseInt(x + b + p + e, 2);
                String hex = Integer.toHexString(dec);
                objc += hex + disp.substring(disp.length() - 3);
                gObjCode.add(objc);
            }

            else if (f.equals("4")) {
                // op nixbpe disp
                // op ni
                if (temp[2].charAt(0) == '#') { // i
                    ni = 1;
                } else if (temp[2].charAt(0) == '@') { // n
                    ni = 2;
                } else {
                    ni = 3;
                }
                objc += String.format("%02X", (Integer.parseInt(optab.getOPCode(temp[1].substring(1)), 16) + ni), 16);

                // x
                if (temp[2].contains(",")) {
                    x = "1";
                } else {
                    x = "0";
                }

                // bp disp
                b = "0";
                p = "0";
                if (x.equals("1")) {
                    String t = temp[2].substring(0, temp[2].indexOf(","));
                    if (check_EXT(t, section)) {
                        disp = "0000";
                        String m = "M";
                        m += String.format("%06X", Integer.parseInt(pass1Loc.get(r), 16) + 1);
                        m += "05";
                        m = m + "+" + temp[2].substring(0, temp[2].indexOf(","));
                        modRec.add(m);
                        modRecSec.add(section);
                    } else {
                        disp = pass1SymTab.get(t);
                    }
                } else {
                    if (check_EXT(temp[2], section)) {
                        disp = "0000";
                        String m = "M";
                        m += String.format("%06X", Integer.parseInt(pass1Loc.get(r), 16) + 1);
                        m += "05";
                        m = m + "+" + temp[2];
                        modRec.add(m);
                        modRecSec.add(section);
                    } else {
                        disp = pass1SymTab.get(temp[2]);
                    }
                }

                // e
                e = "1";

                int dec = Integer.parseInt(x + b + p + e, 2);
                String hex = Integer.toHexString(dec);
                objc += hex + "0" + disp;
                gObjCode.add(objc);
            }

            else if (f.equals("5")) { // BYTEX
                objc = temp[2].substring(2, temp[2].length() - 1);
                gObjCode.add(objc);
            }

            else if (f.equals("6")) { // BYTEC
                String word = temp[2].substring(2, temp[2].length() - 1);
                String ascii_value = "";
                for (int k = 0; k < word.length(); k++) {
                    ascii_value += String.format("%02X", word.charAt(k));
                }
                gObjCode.add(ascii_value);
            }

            else if (f.equals("7")) { // WORD
                String[] value = temp[2].split("-");
                String val = "000000";
                ArrayList<String> sign = new ArrayList<>();

                for (int i = 0; i < temp[2].length(); i++) {
                    if (temp[2].charAt(i) == '-') {
                        sign.add("-");
                    } else if (temp[2].charAt(i) == '+') {
                        sign.add("+");
                    }
                }
                for (int k = 0; k < value.length; k++) {
                    if (!check_EXT(value[k], section)) {
                        if (k == 0) {
                            val = String.format("%06X", Integer.parseInt(pass1SymTab.get(value[0]), 16));
                        } else {
                            for (int s = 1; s < value.length; s++) {
                                if (sign.get(s - 1).equals("+")) {
                                    val = String.format("%06X", Integer.parseInt(val, 16)
                                            + Integer.parseInt(pass1SymTab.get(value[s]), 16));
                                } else {
                                    val = String.format("%06X", Integer.parseInt(val, 16)
                                            - Integer.parseInt(pass1SymTab.get(value[s]), 16));
                                }
                            }
                        }
                    } else {
                        String m = "M";
                        if (k == 0) {
                            m = m + pass1Loc.get(r) + "06" + "+" + value[0];
                            modRec.add(m);
                            modRecSec.add(section);
                        } else {
                            m = m + pass1Loc.get(r) + "06" + sign.get(k - 1) + value[k];
                            modRec.add(m);
                            modRecSec.add(section);
                        }

                    }
                }
                gObjCode.add(val);
            }

            else if (f.equals("8")) { // =X
                objc = temp[1].substring(3, temp[1].length() - 1);
                gObjCode.add(objc);
            }

            else if (f.equals("9")) { // =C
                String word = temp[1].substring(3, temp[1].length() - 1);
                String ascii_value = "";
                for (int k = 0; k < word.length(); k++) {
                    ascii_value += String.format("%02X", (int) word.charAt(k));
                }
                gObjCode.add(ascii_value);
            }

            else {
                gObjCode.add("");
            }
        }
        
        int tot=0;
        for(int i=0;i<gObjCode.size();i++){
            String d=gObjCode.get(i);
            
            if(!d.equals("")){
                tot+=d.length()/2;
            }
            if(pass1Data.get(i).length>=2 && pass1Data.get(i)[1].equals("RESB")){
                tot+=Integer.parseInt(pass1Data.get(i)[2]);
            }
            if(pass1Data.get(i).length>=2 && pass1Data.get(i)[1].equals("RESW")){
                tot+=Integer.parseInt(pass1Data.get(i)[2])*3;
            }

            if(pass1Data.get(i).length>=2 && (pass1Data.get(i)[1].equals("CSECT") || i==gObjCode.size()-1)){
                csectLen.add(String.format("%04X",tot));
                tot=0;
            }
        }
        

    }

    public void do_EXT(String[] temp) {
        if (temp[1].equals("EXTDEF")) {
            String[] t = temp[2].split(",");
            ArrayList<String> gObjCode = new ArrayList<>();

            for (int j = 0; j < t.length; j++) {
                gObjCode.add(t[j]);
            }
            extdef.add(gObjCode);
        }
        if (temp[1].equals("EXTREF")) {
            String[] t = temp[2].split(",");
            ArrayList<String> gObjCode = new ArrayList<>();

            for (int j = 0; j < t.length; j++) {
                gObjCode.add(t[j]);
            }
            extref.add(gObjCode);
        }
    }

    public boolean check_EXT(String ref, int section) {
        return extref.get(section).contains(ref);
    }
}
