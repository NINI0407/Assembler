import java.util.*;

public class Pass1 {
    public ArrayList<String> format;
    public OPTAB optab;
    public ArrayList<String> locRes;
    public Map<String, String> symtab;

    public Pass1() {
        format = new ArrayList<>();
        optab = new OPTAB();
        locRes = new ArrayList<>();
        symtab = new HashMap<>();
    }

    public ArrayList<String> getFormat() {
        return format;
    }

    public ArrayList<String> getLocRes() {
        return locRes;
    }

    public Map<String, String> getSymTab() {
        return symtab;
    }

    public ArrayList<String[]> litteral(ArrayList<String[]> data) {
        ArrayList<String[]> res = new ArrayList<>();
        ArrayList<String[]> lit = new ArrayList<>();

        for (String[] d : data) {
            res.add(d);

            if (d.length >= 2 && (d[1].equals("LTORG") || d[1].equals("END"))) {
                for (int i = 0; i < lit.size(); i++) {
                    res.add(lit.get(i));
                    lit.remove(i);
                }
            } else if (d.length > 2 && d[2].charAt(0) == '=') {
                String[] t = new String[3];
                t[0] = "*";
                t[1] = d[2];
                t[2] = "";
                lit.add(t);
            }
        }
        return res;
    }

    // 0 => [], 1 => format 1, 2 => foramt 2, 3 => format 3, 4 => format 4
    // 5 => BYTEX, 6 => BYTEC, 7 => WORD, 8 => =X, 9 => =C, 10 => RSUB, 11 => EQU

    public void locctr(ArrayList<String[]> data) {
        String loc = "0000";

        for (String[] d : data) {
            loc = String.format("%04X", Integer.parseInt(loc, 16)).toUpperCase();

            if (!d[0].equals("")) {
                if(d[0].equals("*")){
                    symtab.put(d[1],loc);
                }
                else{
                    symtab.put(d[0], loc);
                }
                
            }
            if (d.length >= 2 && (d[1].equals("START") || d[1].equals("CSECT"))) {
                loc = "0000";
                locRes.add(loc);
                format.add("0");
                if (!d[0].equals("")) {
                    symtab.put(d[0], loc);
                }
            } else if (d.length >= 2 && d[1].charAt(0) == '+') { // format4
                locRes.add(loc);
                loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + 4), 16));
                format.add("4");
            } else if (d.length >= 2 && optab.isInstruction(d[1])) {// format2 3
                int f = Integer.parseInt(optab.getFormat(d[1]));
                locRes.add(loc);
                loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + f), 16));
                format.add(optab.getFormat(d[1]));

            } else if (d.length >= 2 && d[1].equals("RESW")) {
                locRes.add(loc);
                loc = String.format("%s",
                        Integer.toString((Integer.parseInt(loc, 16) + Integer.parseInt(d[2]) * 3), 16));
                format.add("0");
            } else if (d.length >= 2 && d[1].equals("RESB")) {
                locRes.add(loc);
                loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + Integer.parseInt(d[2])), 16));
                format.add("0");
            } else if (d.length >= 2 && d[1].equals("BYTE")) {
                String w = d[2].substring(1);
                if (d[2].charAt(0) == 'X') {
                    locRes.add(loc);
                    loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + (w.length() - 2) / 2), 16));
                    format.add("5");
                }
                if (d[2].charAt(0) == 'C') {
                    locRes.add(loc);
                    loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + w.length() - 2), 16));
                    format.add("6");
                }
            } else if (d.length >= 2 && d[1].equals("WORD")) {
                locRes.add(loc);
                loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + 3), 16));
                format.add("7");
            } else if (d.length >= 2 && d[1].charAt(0) == '=') {
                locRes.add(loc);
                if (d[1].charAt(1) == 'C') {
                    loc = String.format("%s", Integer.toString((Integer.parseInt(loc, 16) + d[1].length() - 4), 16));
                    format.add("9");
                }
                if (d[1].charAt(1) == 'X') {
                    loc = String.format("%s",
                            Integer.toString((Integer.parseInt(loc, 16) + (d[1].length() - 4) / 2), 16));
                    format.add("8");
                }
            } else if (d.length >= 2 && d[1].equals("EQU")) {

                if (d[2].equals("*")) {
                    locRes.add(loc);
                    format.add("11");
                } else {
                    String[] value = d[2].split("-");
                    ArrayList<String> sign = new ArrayList<>();
                    boolean absolute = false, relative = false;

                    for (int i = 0; i < d[2].length(); i++) {
                        if (d[2].charAt(i) == '-') {
                            sign.add("-");
                        } else if (d[2].charAt(i) == '+') {
                            sign.add("+");
                        }
                    }
                    int add = 1, sub = 0;
                    for (int i = 0; i < sign.size(); i++) {
                        if (sign.get(i).equals("+")) {
                            add++;
                        } else {
                            sub++;
                        }
                    }

                    if (add == sub) {
                        absolute = true;
                    }
                    if (add == sub + 1) {
                        relative = true;
                    }

                    if (absolute || relative) {
                        loc = symtab.get(value[0]);
                        for (int i = 1; i < value.length; i++) {
                            if (sign.get(i - 1).equals("+")) {
                                loc = String.format("%s",
                                        Integer.toString(
                                                (Integer.parseInt(loc, 16)
                                                        + Integer.parseInt(symtab.get(value[i]), 16)),
                                                16));
                            } else if (sign.get(i - 1).equals("-")) {
                                loc = String.format("%s",
                                        Integer.toString(
                                                (Integer.parseInt(loc, 16)
                                                        - Integer.parseInt(symtab.get(value[i]), 16)),16));
                            }
                        }
                    }
                    locRes.add(loc);
                    format.add("11");
                    symtab.put(d[0], loc);
                }
            } else {
                // System.out.println(Arrays.toString(d));
                locRes.add("");
                format.add("0");
            }
        }
    }
}
