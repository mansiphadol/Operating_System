import java.io.*;
import java.util.Arrays;

public class Main {
    static BufferedReader ifile;
    static BufferedWriter ofile;
    static String[] memory = new String[100];
    static String data = "";

    public static void READ(String[] memory, String IR) {
        int end = data.indexOf('\n');
        if (end == -1) {
            end = data.length(); // If no newline is found, use the entire data
        }
        int start = Integer.parseInt(IR.substring(2));
        int p = 0;
    
        if (end > 0) {
            String string = data.substring(0, end);
    
            while (p < end) {
                try {
                    memory[start] = string.substring(p, p + 4);
                } catch (Exception e) {
                    memory[start] = string.substring(p, end);
                }
                p = p + 4;
                start = start + 1;
            }
            data = data.substring(end + 1);
        }
    }
    

    public static void WRITE(String[] memory, String IR) throws IOException {
        int start = Integer.parseInt(IR.substring(2));
        String put = String.join("", Arrays.copyOfRange(memory, start, start + 10)).replace("0000", "");
        ofile.write(put);
        ofile.newLine();
    }

    public static void TERMINATE() throws IOException {
        ofile.newLine();
        ofile.newLine();
    }

    public static void MASTER_MODE(int SI, String[] memory, String IR) throws IOException {
        if (SI == 1) {
            READ(memory, IR);
        } else if (SI == 2) {
            WRITE(memory, IR);
        } else if (SI == 3) {
            TERMINATE();
        }
    }

    public static void SLAVE_MODE() throws IOException {
        int IC = 0;
        int Register = 0;
        boolean TF = false;

        while (true) {
            String IR = memory[IC];
            if (IR == null) {
                break;
            }
            int SI = 0;

            if (IR.equals("H")) {
                SI = 3;
                MASTER_MODE(SI, memory, IR);
                break;
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("GD")) {
                SI = 1;
                MASTER_MODE(SI, memory, IR);
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("PD")) {
                SI = 2;
                MASTER_MODE(SI, memory, IR);
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("LR")) {
                int start = Integer.parseInt(IR.substring(2));
                Register = Integer.parseInt(memory[start]);
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("SR")) {
                int start = Integer.parseInt(IR.substring(2));
                memory[start] = String.valueOf(Register);
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("CR")) {
                int start = Integer.parseInt(IR.substring(2));
                if (memory[start].equals(String.valueOf(Register))) {
                    TF = true;
                }
            } else if (IR.length() >= 2 && IR.substring(0, 2).equals("BT")) {
                if (TF) {
                    int start = Integer.parseInt(IR.substring(2));
                    IC = start - 1;
                }
            }
            IC = IC + 1;
        }
    }

    public static void LOAD() throws IOException {
        memory = new String[100];
        data = "";
        int n = 0;
        String job = "";
        boolean isDataCard = false; // Flag to track whether we are processing the data card
    
        String line;
        while ((line = ifile.readLine()) != null) {
            System.out.println("Line: " + line); // Print the line being read
            if (line.startsWith("$AMJ")) {
                System.out.println("Starting...");
                line = line.substring(0, line.length() - 1);
                System.out.println("AMJ: " + line);
            } else if (line.startsWith("$DTA")) {
                n = 1;
                isDataCard = true; // Set the flag when encountering $DTA card
                // Continue processing the rest of the loop without resetting memory or data
                continue;
            } else if (line.startsWith("$END")) {
                SLAVE_MODE();
                System.out.println("Memory: ");
                System.out.println(String.join("  ", memory));
                System.out.println("End.");
                System.out.println("------------------------------------------------------");
                memory = new String[100];
                data = "";
                n = 0;
                isDataCard = false; // Reset the flag when encountering $END card
            } else if (n == 1 && isDataCard) {
                data += line + "\n"; // Append line with newline
                System.out.println("Data: " + data); // Print the current data
                ofile.write(line); // Write the data to the output file
                ofile.newLine(); // Write a newline character
            } else {
                job = line.substring(0, line.length() - 1);
                System.out.println("JOB: " + job);
                int k = 0;
                while (k < job.length()) {
                    if (job.charAt(k) == 'H') {
                        memory[k] = String.valueOf(job.charAt(k));
                        k = k + 1;
                        break;
                    }
                    memory[k] = job.substring(k, Math.min(k + 4, job.length()));
                    k = k + 4;
                }
                while (k < 100) {
                    memory[k] = "0000";
                    k++;
                }
            }
        }
    }
    
    public static void printPattern(BufferedWriter ofile, int repetitions) throws IOException {
        String pattern = "*";
        for (int i = 1; i <= repetitions; i++) {
            ofile.write(pattern);
            ofile.newLine();
            pattern += "*";
        }
        for (int i = repetitions - 1; i >= 1; i--) {
            pattern = pattern.substring(0, i);
            ofile.write(pattern);
            ofile.newLine();
        }
    }
    

    public static void main(String[] args) throws IOException {
        ifile = new BufferedReader(new FileReader("input_Phase1.txt"));
        ofile = new BufferedWriter(new FileWriter("F:\\VIT 3rd YEAR\\5th Semester\\OS\\OS PHASE 1\\output_Phase1.txt"));
        LOAD();
    
        // Print the desired pattern
        printPattern(ofile,3);
    
        ofile.close();
        ifile.close();
    }
}
