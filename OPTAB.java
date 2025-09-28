import java.util.*;

public class OPTAB {
    Map<String,String> optab;

    public OPTAB(){
        optab=new HashMap<>();
        optab.put("ADD", "3 18");
        optab.put("ADDF", "3 58");
        optab.put("ADDR", "2 90");
        optab.put("AND", "3 40");
        optab.put("CLEAR", "2 B4");
        optab.put("COMP", "3 28");
        optab.put("COMPF", "3 88");
        optab.put("COMPR", "2 A0");
        optab.put("DIV", "3 24");
        optab.put("DIVF", "3 64");
        optab.put("DIVR", "2 9C");
        optab.put("FIX", "1 C4");
        optab.put("FLOAT", "1 C0");
        optab.put("HIO", "1 F4");
        optab.put("J", "3 3C");
        optab.put("JEQ", "3 30");
        optab.put("JGT", "3 34");
        optab.put("JLT", "3 38");
        optab.put("JSUB", "3 48");
        optab.put("LDA", "3 00");
        optab.put("LDB", "3 68");
        optab.put("LDCH", "3 50");
        optab.put("LDF", "3 70");
        optab.put("LDL", "3 08");
        optab.put("LDS", "3 6C");
        optab.put("LDT", "3 74");
        optab.put("LDX", "3 04");
        optab.put("LPS", "3 D0");
        optab.put("MUL", "3 20");
        optab.put("MULF", "3 60");
        optab.put("MULR", "2 98");
        optab.put("NORM", "1 C8");
        optab.put("OR", "3 44");
        optab.put("RD", "3 D8");
        optab.put("RMO", "2 AC");
        optab.put("RSUB", "3 4C");
        optab.put("SHIFTL", "2 A4");
        optab.put("SHIFTR", "2 A8");
        optab.put("SIO", "1 F0");
        optab.put("SSK", "3 EC");
        optab.put("STA", "3 0C");
        optab.put("STB", "3 78");
        optab.put("STCH", "3 54");
        optab.put("STF", "3 80");
        optab.put("STI", "3 D4");
        optab.put("STL", "3 14");
        optab.put("STS", "3 7C");
        optab.put("STSW", "3 E8");
        optab.put("STT", "3 84");
        optab.put("STX", "3 10");
        optab.put("SUB", "3 1C");
        optab.put("SUBF", "3 5C");
        optab.put("SUBR", "2 94");
        optab.put("SVC", "2 B0");
        optab.put("TD", "3 E0");
        optab.put("TIO", "1 F8");
        optab.put("TIX", "3 2C");
        optab.put("TIXR", "2 B8");
        optab.put("WD", "3 DC");
    }

    public String getFormat(String key){
        return optab.get(key).substring(0,1);
    }

    public String getOPCode(String key){
        return optab.get(key).substring(2);
    }

    public boolean isInstruction(String key){
        return optab.containsKey(key);   
    }
}
