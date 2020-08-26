/* On my honor, I have neither given nor received unauthorized aid on this assignment
*
* */
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
public class SIM {
    public static void main(String[] args) throws IOException{
        if (args.length <= 0) {
            System.out.println("Please input a parameter!");
        }
        else {
            if (args[0].equals("1")) {
                Compressing();
            }
            else if (args[0].equals("2")) {
                DeCompressing();
            }
            else {
                System.out.println("Invalid parameter!");
            }
        }
    }
    private static void DeCompressing() throws IOException {
        Scanner input = new Scanner(Paths.get("compressed.txt"));
        String[] dictionary = new String[8];
        String sequence = "";
        while(input.hasNextLine()){
            String line = input.nextLine();
            if (line.equals("xxxx")) {
                break;
            }
            else {
                sequence = sequence + line;
            }
        }
        int count = 0;
        while(input.hasNextLine() && count < 8){
            String line = input.nextLine();
            dictionary[count] = line;
            //System.out.println(dictionary[count]);
            count++;
        }
        FileWriter writer = new FileWriter("dout.txt");
        PrintWriter printWriter = new PrintWriter(writer);
        int firstThree = 3;
        DeCompression decompressor= new DeCompression(sequence, dictionary);
        while (firstThree < sequence.length()) {
            String original = decompressor.doCompressing(firstThree);
            if (original.length() > 32) {
                for (int i = original.length() / 32; i > 0; i--) {
                    printWriter.println(original.substring(0, 32));
                }
            }
            else if (!original.equals("")) {
                printWriter.println(original);
            }
            firstThree = decompressor.MoveToNextIns(firstThree);
        }
        printWriter.close();
    }

    private static void Compressing() throws IOException {
        Scanner input = new Scanner(Paths.get("original.txt"));
        Instructions original = new Instructions();
        MyDictionary d = new MyDictionary();
        while(input.hasNextLine()){
            String line = input.nextLine();
            original.insertIns(line);
            d.frequencyCalculating(line);
        }
        String[] p = d.setEntry();
        Compression compressor= new Compression(original, p);
        compressor.doCompressing();
        Iterator<String> iter = compressor.getReselts();
        FileWriter writer = new FileWriter("cout.txt");
        PrintWriter printWriter = new PrintWriter(writer);
        String sequence = "";
        while (iter.hasNext()) {
            String ins = iter.next();
            sequence = sequence + ins;
        }
        String[] seqArr = sequence.split("");
        int i = 0;
        while (i < seqArr.length) {
            if ((i + 1) % 32 == 0) {
                printWriter.println(seqArr[i]);
            }
            else {
                printWriter.print(seqArr[i]);
            }
            i++;
        }
        i = 32 - (i % 32);
        for (int j = 0; j < i; j++) {
            printWriter.print("1");
        }
        printWriter.println("");
        printWriter.println("xxxx");
        for (String e : p) {
            printWriter.println(e);
        }
        printWriter.close();
    }
}
class DeCompression {
    private String sequence;
    private String lastIns;
    private String[] dic;
    DeCompression(String sequence, String[] dictionary) {
        this.sequence = sequence;
        dic = dictionary;
        lastIns = "";
    }
    public String doCompressing(int endIndex) {
        String opt = sequence.substring(endIndex - 3, endIndex);
        if (opt.equals("000")) {
            lastIns = DeRLE(endIndex);
        }
        if (opt.equals("001")) {
            lastIns = DeBitMask(endIndex);
        }
        if (opt.equals("010")) {
            lastIns = DeOneMis(endIndex);
        }
        if (opt.equals("011")) {
            lastIns = DeTwoConsec(endIndex);
        }
        if (opt.equals("100")) {
            lastIns = DeTwoAw(endIndex);
        }
        if (opt.equals("101")) {
            lastIns = DeDM(endIndex);
        }
        if (opt.equals("110")) {
            lastIns = sequence.substring(endIndex, endIndex + 32);
        }
        if (opt.equals("111")) {
            return "";
        }
        return lastIns;
    }
    public String DeBitMask(int start) {
        String location = sequence.substring(start, start + 5);
        String pattern = sequence.substring(start + 5, start + 9);
        String entry = sequence.substring(start + 9, start + 12);
        int l = Integer.parseInt(location,2);
        int e = Integer.parseInt(entry,2);
        String[] patternArr = pattern.split("");
        String res = dic[e];
        for (String i : patternArr) {
            if(i.equals("1")) {
                res = recover(res,l);
            }
            l++;
        }
        return res;
    }
    public  String DeDM(int start) {
        String entry = sequence.substring(start, start + 3);
        int e = Integer.parseInt(entry,2);
        return dic[e];
    }
    public String DeTwoAw(int start){
        String locationOne = sequence.substring(start, start + 5);
        String locationTwo = sequence.substring(start + 5, start + 10);
        String entry = sequence.substring(start + 10, start + 13);
        int l1 = Integer.parseInt(locationOne,2);
        int l2 = Integer.parseInt(locationTwo,2);
        int e = Integer.parseInt(entry,2);
        String pre = recover(dic[e], l1);
        return recover(pre, l2);
    }
    public String recover(String entry, int index){
        String[] a = entry.split("");
        if (a[index].equals("1")) {
            a[index] = "0";
        }
        else if(a[index].equals("0")) {
            a[index] = "1";
        }
        String res = "";
        for (String i : a) {
            res = res + i;
        }
        return res;
    }
    public String DeTwoConsec(int start){
        String location = sequence.substring(start, start + 5);
        String entry = sequence.substring(start + 5, start + 8);
        int l = Integer.parseInt(location,2);
        int e = Integer.parseInt(entry,2);
        String pre = recover(dic[e],l);
        return recover(pre, l + 1);
    }
    public String DeOneMis(int start){
        String location = sequence.substring(start, start + 5);
        String entry = sequence.substring(start + 5, start + 8);
        int l = Integer.parseInt(location,2);
        int e = Integer.parseInt(entry,2);
        return recover(dic[e], l);
    }
    public String DeRLE(int start){
        String binaryString = sequence.substring(start, start + 2);
        int decimal = Integer.parseInt(binaryString,2);
        String res = "";
        decimal += 1;
        while(decimal > 0) {
            res = res + lastIns;
            decimal--;
        }
        return res;
    }
    public int MoveToNextIns(int endIndex) {
        String opt = sequence.substring(endIndex - 3, endIndex);
        if (opt.equals("000")) {
            return endIndex + 5;
        }
        if (opt.equals("001")) {
            return endIndex + 15;
        }
        if (opt.equals("010")) {
            return endIndex + 11;
        }
        if (opt.equals("011")) {
            return endIndex + 11;
        }
        if (opt.equals("100")) {
            return endIndex + 16;
        }
        if (opt.equals("101")) {
            return endIndex + 6;
        }
        if (opt.equals("110")) {
            return endIndex + 35;
        }
        if (opt.equals("111")) {
            return sequence.length();
        }
        // exception
        return -1;
    }
}

class Compression {
    private Instructions original;
    private String[] dictionary;
    private Instructions insAfterPro;

    Compression(Instructions original, String[] dictionary){
        this.original = original;
        this.dictionary = dictionary;
        insAfterPro = new Instructions();
    }
    public void doCompressing() {
        Iterator<String> iter = original.getIterator();
        String lastIns = "";
        int consecutiveAppearance = 0;
        String compressedIns = "";
        while (iter.hasNext()) {
            String ins = iter.next();
            //check whether we can use RLE
            while (ins.equals(lastIns)) {
                consecutiveAppearance++;
                compressedIns = RLE(consecutiveAppearance);
                ins = iter.next();
            }
            if (consecutiveAppearance > 0) {
                insAfterPro.insertIns(compressedIns);
                consecutiveAppearance = 0;
            }
            // DM
            compressedIns = DirectMatching(ins);
            if(!compressedIns.equals("")) {
                insAfterPro.insertIns(compressedIns);
                lastIns = ins;
                continue;
            }
            // One Bit Mismatch
            compressedIns = OneBitMismatch(ins);
            if(!compressedIns.equals("")) {
                insAfterPro.insertIns(compressedIns);
                lastIns = ins;
                continue;
            }
            // Two Consecutive Mismatches
            compressedIns = TwoBitConsecutiveMismatches(ins);
            if(!compressedIns.equals("")) {
                insAfterPro.insertIns(compressedIns);
                lastIns = ins;
                continue;
            }
            // BitMask
            compressedIns = BitMask(ins);
            if(!compressedIns.equals("")) {
                insAfterPro.insertIns(compressedIns);
                lastIns = ins;
                continue;
            }
            // Two bits mis anywhere
            compressedIns = TwoBitMismatchesAnywhere(ins);
            if(!compressedIns.equals("")) {
                insAfterPro.insertIns(compressedIns);
                lastIns = ins;
                continue;
            }

            compressedIns = Original(ins);
            insAfterPro.insertIns(compressedIns);
            lastIns = ins;
        }
    }
    public String RLE(int times) {
        String compressedCode;
        switch (times) {
            default:
                return "exception";
            case 1:
                compressedCode = "00";
                break;
            case 2:
                compressedCode ="01";
                break;
            case 3:
                compressedCode = "10";
                break;
            case 4:
                compressedCode = "11";
                break;
        }
        return "000" + compressedCode;
    }
    public String BitMask(String ins) {
        char[] in = ins.toCharArray();
        int firstposition = -1;
        int lastposition = -1;
        char[] bitmaskPattern = new char[4];
        for (int k = 0; k < 8; k++) {
            char[] d = dictionary[k].toCharArray();
            int j;
            for (j = 0; j < 32; j++) {
                if (in[j] == d[j]) {
                    continue;
                }
                else {
                    if (firstposition == -1) {
                        firstposition = j;
                    }
                    lastposition = j;
                }
                if (lastposition - firstposition > 3) {
                    break;
                }
            }
            if (firstposition == lastposition && firstposition == -1) {
                continue;
            }
            if (lastposition - firstposition <= 3) {
                if (firstposition + 3 < 32) {
                    bitmaskPattern[0] = '1';
                    for (int i = 1; i < 4; i++) {
                        if (in[firstposition + i] == d[firstposition + i]) {
                            bitmaskPattern[i] = '0';
                        } else {
                            bitmaskPattern[i] = '1';
                        }
                    }
                    return "001" + decimalToBinary(firstposition, 5) + new String(bitmaskPattern) + decimalToBinary(k, 3);
                }
                for (int i = 28; i < 32; i++) {
                    if (in[i] == d[i]) {
                        bitmaskPattern[i] = '0';
                    }
                    bitmaskPattern[i] = '1';
                }
                return "001" + decimalToBinary(firstposition, 5) + new String(bitmaskPattern) + decimalToBinary(k, 3);
            }
            firstposition = -1;
            lastposition = -1;
        }
        return "";
    }
    public String OneBitMismatch(String ins) {
        char[] in = ins.toCharArray();
        int count = 0;
        int position = -1;
        for (int k = 0; k < 8; k++) {
            char[] d = dictionary[k].toCharArray();
            for (int j = 0; j < 32; j++) {
                if (in[j] == d[j]) {
                    continue;
                }
                else {
                    position = j;
                    count++;
                }
                if (count > 1) {
                    break;
                }
            }
            if (position > -1 && count == 1) {
                return "010" + decimalToBinary(position, 5) + decimalToBinary(k, 3);
            }
            count = 0;
            position = -1;
        }
        return "";
    }
    public String TwoBitConsecutiveMismatches(String ins) {
        char[] in = ins.toCharArray();
        int count = 0;
        int position = -2;
        boolean consecutive = false;
        for (int k = 0; k < 8; k++) {
            char[] d = dictionary[k].toCharArray();
            int j;
            for (j = 0; j < 32; j++) {
                if (in[j] == d[j]) {
                    continue;
                }
                else {
                    if (position == j - 1) {
                        consecutive = true;
                        count++;
                    }
                    else {
                        consecutive = false;
                        position = j;
                        count++;
                    }
                }
                if (count > 2) {
                    break;
                }
            }
            if (consecutive && position > -1 && count == 2) {
                return "011" + decimalToBinary(position, 5) + decimalToBinary(k, 3);
            }
            count = 0;
            position = -2;
            consecutive = false;
        }
        return "";
    }
    public String TwoBitMismatchesAnywhere(String ins) {
        char[] in = ins.toCharArray();
        int count = 0;
        int positionOne = -1;
        int positionTwo = -1;
        for (int k = 0; k < 8; k++) {
            char[] d = dictionary[k].toCharArray();
            int j;
            for (j = 0; j < 32; j++) {
                if (in[j] == d[j]) {
                    continue;
                }
                else {
                    if (positionOne == -1) {
                        positionOne = j;
                        count++;
                    }
                    else {
                        positionTwo = j;
                        count++;
                    }
                }
                if (count > 2) {
                    break;
                }
            }
            if (count == 2) {
                return "100" + decimalToBinary(positionOne, 5) + decimalToBinary(positionTwo, 5) + decimalToBinary(k, 3);
            }
            count = 0;
            positionOne = -1;
            positionTwo = -1;
        }
        return "";
    }
    public String DirectMatching(String ins) {
        String compressedCode = "";
        int i;
        for (i = 0; i < 8; i++) {
            if (dictionary[i].equals(ins)) {
                switch (i) {
                    default:
                        return "exception";
                    case 0:
                        compressedCode = "000";
                        break;
                    case 1:
                        compressedCode ="001";
                        break;
                    case 2:
                        compressedCode = "010";
                        break;
                    case 3:
                        compressedCode = "011";
                        break;
                    case 4:
                        compressedCode = "100";
                        break;
                    case 5:
                        compressedCode = "101";
                        break;
                    case 6:
                        compressedCode = "110";
                        break;
                    case 7:
                        compressedCode = "111";
                        break;
                }
                break;
            }
        }
        if (!compressedCode.equals("")) {
            return "101" + compressedCode;
        }
        return compressedCode;
    }
    public String Original(String ins) {
        return "110" + ins;
    }
    public Iterator<String> getReselts() {
        return insAfterPro.getIterator();
    }

    public String decimalToBinary(int x, int len) {
        if (len > 0) {
            return String.format("%" + len + "s", Integer.toBinaryString(x)).replaceAll(" ", "0");
        }
        return null;
    }
}
//A class used to store instructions before amd after compression
class Instructions {
    private List<String> instructionsArray;
    Instructions() {
        instructionsArray = new ArrayList<>();
    }
    public void insertIns(String instruction) {
        instructionsArray.add(instruction);
    }
    public Iterator<String> getIterator() {
        return instructionsArray.iterator();
    }
}
class MyDictionary {
    private String[] entry;
    private Map<String, Record> calculator;
    private int order;
    MyDictionary() {
        entry = new String[8];
        calculator = new HashMap<>();
        order = 1;
    }
    public String[] setEntry() {
        int size = calculator.size();
        List<Record> sortingArray = new ArrayList<>();
        for (String i : calculator.keySet()) {
            Record r = calculator.get(i);
            r.insertIns(i);
            sortingArray.add(r);
        }
        RecordComparator rc = new RecordComparator();
        Collections.sort(sortingArray, rc);
        for (int i = 0; i < 8; i++) {
            entry[i] = sortingArray.get(i).getIns();
        }
        return Arrays.copyOf(entry, 8);
    }
    public void frequencyCalculating (String instruction) {
        if (calculator.containsKey(instruction)) {
            calculator.get(instruction).addTimes();
        }
        else {
            Record firstArrive = new Record(order);
            calculator.put(instruction, firstArrive);
            order++;
        }
    }

}
class Record {
    private int times;
    private int firstAppear;
    private String ins;

    Record(int firstAppear) {
        times = 1;
        this.firstAppear = firstAppear;
        ins = "";
    }
    public void insertIns(String ins){
        this.ins = ins;
    }
    public void addTimes() {
        times++;
    }
    public int getTimes() {return times;}
    public int getFirstAppear() {return firstAppear;}
    public String getIns() {return ins;}
}

class RecordComparator implements Comparator<Record>{
    @Override
    public int compare(Record first, Record second) {
        if (first.getTimes() == second.getTimes()) {
            return first.getFirstAppear() - second.getFirstAppear();
        }
        return second.getTimes() - first.getTimes();
    }
}