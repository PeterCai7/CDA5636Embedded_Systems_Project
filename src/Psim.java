/* On my honor, I have neither given nor received unauthorized aid on this assignment
* In this version, I have reached the reading files and writting files.
* And DAM is sorted;
* The final problem is that RGF is not sorted.
* */
import java.nio.file.Paths;
import java.util.*;
import java.io.*;


public class Psim {

    public static void main(String[] args) throws IOException{
        FileWriter  writer      = new FileWriter("simulation.txt");
        PrintWriter printWriter = new PrintWriter(writer);
        Queue<Ii> INM = new LinkedList<>();
        LinkedList<Di> DAM = new LinkedList<>();
        Di damFromS = null;
        LinkedList<Xi> RGF = new LinkedList<>();
        Xi rgfFromW = null;
        Queue<InbTokens> INB = new LinkedList<>();
        InbTokens inbFromD = null;
        Queue<InbTokens> AIB = new LinkedList<>();
        InbTokens aibFromI = null;
        Queue<InbTokens> SIB = new LinkedList<>();
        InbTokens sibFromI = null;
        Queue<InbTokens> PRB = new LinkedList<>();
        InbTokens prbFromM = null;
        Queue<AdbTokens> ADB = new LinkedList<>();
        AdbTokens adbFromA = null;
        Queue<Xi> REB = new LinkedList<>();
        Xi rebFromA = null;
        Xi rebFromM = null;
        int step = 0;
        boolean NoTransitionCanBeFired = false;
        Scanner in = new Scanner(Paths.get("instructions.txt"));
        Scanner re = new Scanner(Paths.get("registers.txt"));
        Scanner da = new Scanner(Paths.get("datamemory.txt"));
        while(in.hasNextLine()){
            String line = in.nextLine();
            String[] strarray = line.split("<|,|>");
            INM.add(new Ii(strarray[1], strarray[2], strarray[3], strarray[4]));
        }
        while(re.hasNextLine()){
            String line = re.nextLine();
            String[] strarray = line.split("<|,|>");
            RGF.add(new Xi(strarray[1], Integer.parseInt(strarray[2])));
        }
        while(da.hasNextLine()){
            String line = da.nextLine();
            String[] strarray = line.split("<|,|>");
            DAM.add(new Di(Integer.parseInt(strarray[1]),Integer.parseInt(strarray[2])));
        }



        do {
            printWriter.println("STEP "+ step + ":");
            printWriter.print("INM:");
            Iterator<Ii> iter1 = INM.iterator();
            while (iter1.hasNext()) {
                Ii element = iter1.next();
                printWriter.print("<" + element.getOpcode() + "," + element.getDestinationRegister() + ",");
                printWriter.print(element.getFirstSourceOperand() + "," + element.getSecondSourceOperand() + ">");
                if (iter1.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("INB:");
            Iterator<InbTokens> iter2 = INB.iterator();
            while (iter2.hasNext()) {
                InbTokens element = iter2.next();
                printWriter.print("<" + element.getOpcode() + "," + element.getDestinationRegister() + ",");
                printWriter.print(element.getFirstSourceOperand() + "," + element.getSecondSourceOperand() + ">");
                if (iter2.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("AIB:");
            iter2 = AIB.iterator();
            while (iter2.hasNext()) {
                InbTokens element = iter2.next();
                printWriter.print("<" + element.getOpcode() + "," + element.getDestinationRegister() + ",");
                printWriter.print(element.getFirstSourceOperand() + "," + element.getSecondSourceOperand() + ">");
                if (iter2.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("SIB:");
            iter2 = SIB.iterator();
            while (iter2.hasNext()) {
                InbTokens element = iter2.next();
                printWriter.print("<" + element.getOpcode() + "," + element.getDestinationRegister() + ",");
                printWriter.print(element.getFirstSourceOperand() + "," + element.getSecondSourceOperand() + ">");
                if (iter2.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("PRB:");
            iter2 = PRB.iterator();
            while (iter2.hasNext()) {
                InbTokens element = iter2.next();
                printWriter.print("<" + element.getOpcode() + "," + element.getDestinationRegister() + ",");
                printWriter.print(element.getFirstSourceOperand() + "," + element.getSecondSourceOperand() + ">");
                if (iter2.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("ADB:");
            Iterator<AdbTokens> iter3 = ADB.iterator();
            while (iter3.hasNext()) {
                AdbTokens element = iter3.next();
                printWriter.print("<" + element.getRegisterName() + "," + element.getDataMemoryAddress() + ">");
                if (iter3.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("REB:");
            Iterator<Xi> iter4 = REB.iterator();
            while (iter4.hasNext()) {
                Xi element = iter4.next();
                printWriter.print("<" + element.getRegisterName() + "," + element.getRegisterValue() + ">");
                if (iter4.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("RGF:");
            XiComparator xc = new XiComparator();
            Collections.sort(RGF,xc);
            iter4 = RGF.iterator();
            while (iter4.hasNext()) {
                Xi element = iter4.next();
                printWriter.print("<" + element.getRegisterName() + "," + element.getRegisterValue() + ">");
                if (iter4.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.print("DAM:");
            DiComparator dc = new DiComparator();
            Collections.sort(DAM,dc);
            Iterator<Di> iter5 = DAM.iterator();
            while (iter5.hasNext()) {
                Di element = iter5.next();
                printWriter.print("<" + element.getAddress() + "," + element.getValue() + ">");
                if (iter5.hasNext()) printWriter.print(",");
            }
            printWriter.println("");
            printWriter.println("");
            NoTransitionCanBeFired = true;

            // below are Decode and Read Transition
            if (INM.peek()!=null) {
                Ii ins = INM.poll();
                String first = ins.getFirstSourceOperand();
                String second = ins.getSecondSourceOperand();
                int firstValue = 0;
                int secondValue = 0;
                boolean firstFound = false;
                boolean secondFound = false;
                Iterator<Xi> iter = RGF.iterator();
                while (iter.hasNext() && !(firstFound && secondFound)) {
                    Xi element = iter.next();
                    if (first.equals(element.getRegisterName())) {
                        firstValue = element.getRegisterValue();
                        firstFound = true;
                    }
                    if (second.equals(element.getRegisterName())) {
                        secondValue = element.getRegisterValue();
                        secondFound = true;
                    }
                }
                if (firstFound && secondFound) {
                    inbFromD = new InbTokens(ins.getOpcode(), ins.getDestinationRegister(), firstValue, secondValue);
                }
                else if ("ST".equals(ins.getOpcode())&& firstFound) {
                    inbFromD = new InbTokens("ST", ins.getDestinationRegister(), firstValue, Integer.parseInt(second));
                }
            }

            //below are ISSUE1 & ISSUE2 transition
            if (INB.peek()!=null) {
                InbTokens inbPresent = INB.poll();
                String ins = inbPresent.getOpcode();
                // ISSUE1
                if ("ADD".equals(ins) || "SUB".equals(ins) || "MUL".equals(ins)) {
                    aibFromI = inbPresent;
                }
                // ISSUE2
                else if ("ST".equals(ins)) {
                    sibFromI = inbPresent;
                }
            }

            //below are ASU & MUL1 transition
            if (AIB.peek()!=null) {
                InbTokens aibPresent = AIB.poll();
                String ins = aibPresent.getOpcode();
                //ASU
                if ("ADD".equals(ins)) {
                    int r = aibPresent.getFirstSourceOperand() + aibPresent.getSecondSourceOperand();
                    rebFromA = new Xi(aibPresent.getDestinationRegister(), r);
                }
                else if ("SUB".equals(ins)) {
                    int r = aibPresent.getFirstSourceOperand() - aibPresent.getSecondSourceOperand();
                    rebFromA = new Xi(aibPresent.getDestinationRegister(), r);
                }
                //MUL1
                else if ("MUL".equals(ins)) {
                    prbFromM = aibPresent;
                }
            }

            //Below are MUL2 transition
            if (PRB.peek()!=null) {
                InbTokens prbPresent = PRB.poll();
                int r = prbPresent.getFirstSourceOperand() * prbPresent.getSecondSourceOperand();
                rebFromM = new Xi(prbPresent.getDestinationRegister(), r);
            }

            //below are Write Transition
            if (REB.peek()!=null) {
                rgfFromW = REB.poll();
            }

            //below are ADDR transition
            if (SIB.peek()!=null) {
                InbTokens sibPresent = SIB.poll();
                int addr = sibPresent.getFirstSourceOperand() + sibPresent.getSecondSourceOperand();
                adbFromA = new AdbTokens(sibPresent.getDestinationRegister(), addr);
            }

            // below are Store transition
            if (ADB.peek()!=null) {
                AdbTokens adbPresent = ADB.poll();
                String r = adbPresent.getRegisterName();
                int a = adbPresent.getDataMemoryAddress();
                int v = 0;
                boolean Found = false;
                Iterator<Xi> iter = RGF.iterator();
                while (iter.hasNext()) {
                    Xi element = iter.next();
                    if (r.equals(element.getRegisterName())) {
                        v = element.getRegisterValue();
                        Found = true;
                        break;
                    }
                }
                if (Found) {
                    damFromS = new Di(a,v);
                }
            }


            // Update tokens placing after this time step
            step++;
            if (inbFromD != null) {
                INB.add(inbFromD);
                inbFromD = null;
                NoTransitionCanBeFired = false;
            }
            else if (INM.peek()!=null) NoTransitionCanBeFired = false;

            if (aibFromI != null) {
                AIB.add(aibFromI);
                aibFromI = null;
                NoTransitionCanBeFired = false;
            }
            else if (sibFromI != null) {
                SIB.add(sibFromI);
                sibFromI = null;
                NoTransitionCanBeFired = false;
            }
            else if (INB.peek()!=null) NoTransitionCanBeFired = false;

            //Add results from MUL2 before those from ASU
            if (rebFromM != null) {
                REB.add(rebFromM);
                rebFromM = null;
                NoTransitionCanBeFired = false;
            }
            else if (PRB.peek()!=null) NoTransitionCanBeFired = false;

            if (rebFromA != null){
                REB.add(rebFromA);
                rebFromA = null;
                NoTransitionCanBeFired = false;
            }
            else if (prbFromM != null) {
                PRB.add(prbFromM);
                prbFromM = null;
                NoTransitionCanBeFired = false;
            }
            else if (AIB.peek()!=null) NoTransitionCanBeFired = false;

            if (rgfFromW != null) {
                boolean Found = false;
                Iterator<Xi> iter = RGF.iterator();
                while (iter.hasNext()) {
                    Xi element = iter.next();
                    if (element.getRegisterName().equals(rgfFromW.getRegisterName())) {
                        element.setRegisterValue(rgfFromW.getRegisterValue());
                        Found = true;
                        break;
                    }
                }
                if (!Found) {
                    RGF.add(rgfFromW);
                }
                rgfFromW = null;
            }
            if (REB.peek()!=null) NoTransitionCanBeFired = false;

            if (adbFromA != null) {
                ADB.add(adbFromA);
                adbFromA = null;
                NoTransitionCanBeFired = false;
            }
            else if (SIB.peek()!=null) NoTransitionCanBeFired = false;

            if (damFromS != null) {
                boolean Found = false;
                Iterator<Di> iter = DAM.iterator();
                while (iter.hasNext()) {
                    Di element = iter.next();
                    if (element.getAddress() == damFromS.getAddress()) {
                        element.setValue(damFromS.getValue());
                        Found = true;
                        break;
                    }
                }
                if (!Found) {
                    DAM.add(damFromS);
                }
                damFromS = null;
            }
            if (ADB.peek()!=null) NoTransitionCanBeFired = false;
        } while (!NoTransitionCanBeFired);
        printWriter.close();
    }
}

//Instruction Tokens in INM
class Ii {
    private String Opcode;
    private String DestinationRegister;
    private String FirstSourceOperand;
    private String SecondSourceOperand;

    public Ii(String o, String d, String f, String s){
        Opcode = o;
        DestinationRegister = d;
        FirstSourceOperand = f;
        SecondSourceOperand = s;
    }
    public String getOpcode(){
        return Opcode;
    }
    public String getDestinationRegister(){
        return DestinationRegister;
    }
    public String getFirstSourceOperand(){
        return FirstSourceOperand;
    }
    public String getSecondSourceOperand(){
        return SecondSourceOperand;
    }
}

//RegisterValue Tokens in RGF,REB
class Xi {
    private String RegisterName;
    private int RegisterValue;

    public Xi(String name, int value){
        RegisterName = name;
        RegisterValue = value;
    }
    public String getRegisterName(){
        return RegisterName;
    }
    public int getRegisterValue(){
        return RegisterValue;
    }
    public void setRegisterValue(int newV) {
        RegisterValue = newV;
        return;
    }
}
class XiComparator implements Comparator<Xi>{
    @Override
    public int compare(Xi first, Xi second) {
        String firstName = first.getRegisterName();
        String secondName = second.getRegisterName();
        String firstNum = firstName.substring(1);
        String secondNum = secondName.substring(1);
        int firstnumber = Integer.parseInt(firstNum);
        int secondnumber = Integer.parseInt(secondNum);
        return firstnumber - secondnumber;
    }
}

//Data values in Memory in DAM
class Di {
    private int address;
    private int value;

    public Di(int a, int v){
        address = a;
        value = v;
    }
    public int getAddress(){
        return address;
    }
    public int getValue(){
        return value;
    }
    public void setValue(int v) {
        value = v;
        return;
    }
}
class DiComparator implements Comparator<Di>{
    @Override
    public int compare(Di first, Di second) {
        return first.getAddress() - second.getAddress();
    }
}
//Instruction Tokens in InstructionBuffer(INB), AIB, SIB, PRB
class InbTokens {
    private String Opcode;
    private String DestinationRegister;
    private int FirstSourceOperand;
    private int SecondSourceOperand;

    public InbTokens(String o, String d, int f, int s){
        Opcode = o;
        DestinationRegister = d;
        FirstSourceOperand = f;
        SecondSourceOperand = s;
    }
    public String getOpcode(){
        return Opcode;
    }
    public String getDestinationRegister(){
        return DestinationRegister;
    }
    public int getFirstSourceOperand(){
        return FirstSourceOperand;
    }
    public int getSecondSourceOperand(){
        return SecondSourceOperand;
    }
}

//address calculation tokens in ADB
class AdbTokens {
    private String RegisterName;
    private int DataMemoryAddress;

    public AdbTokens(String r, int d){
        RegisterName = r;
        DataMemoryAddress = d;
    }
    public String getRegisterName(){
        return RegisterName;
    }
    public int getDataMemoryAddress(){
        return DataMemoryAddress;
    }
}
