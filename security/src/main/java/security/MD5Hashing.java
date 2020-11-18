package security; /**
 * Created by S_Mall on 29.06.14.
 */

import org.openjdk.jmh.annotations.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

@State(Scope.Benchmark)
public class MD5Hashing
{

    private MessageDigest md;
    protected JFrame mainFrame;
    static Dimension buttonDimension = new Dimension(95, 30);
    static Dimension smallButtonDimension = new Dimension(25, 20);
    private Dimension minDimension = new Dimension(300, 200);
    private static StringBuffer logString = new StringBuffer("");
    protected static String filePath = "";
    JTextField columnsToHash = new JTextField("1, 3");
    private JTextField delimeterTextField = new JTextField("|");

    public MD5Hashing() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logln("No algorithm defined for MD5 on this operating system");
            e.printStackTrace();
        }
    }

    public String getHashedString(String stringToHash) {
        md.update(stringToHash.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format
        StringBuilder sb = new StringBuilder();
        for (byte b: byteData) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    private void initializeGUI() {
        this.mainFrame = new JFrame("Accounts");
        mainFrame.setSize(1200, 600);
        mainFrame.setLocation(200, 100);
        mainFrame.setMinimumSize(minDimension);
        //mainFrame.setIconImage(new ImageIcon());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        Container mainContainer = mainFrame.getContentPane();
        mainContainer.setLayout(new GridBagLayout());

        JButton runButton = new JButton(new AbstractAction("Run!") {
            public void actionPerformed(ActionEvent e) {
                try {
                    runMD5Process();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//                showAddEditGroupDialog(false, "");
            }
        });
        runButton.setPreferredSize(buttonDimension);
        runButton.setMinimumSize(buttonDimension);

        JButton closeButton = new JButton(new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        closeButton.setToolTipText("Exit");
        closeButton.setPreferredSize(buttonDimension);
        closeButton.setMinimumSize(buttonDimension);
        JButton logButton = new JButton(new AbstractAction("LOG") {
            public void actionPerformed(ActionEvent e) {
                showLogDialog("LOG", getLog().toString(), true);
            }
        });
        logButton.setPreferredSize(buttonDimension);
        logButton.setMinimumSize(buttonDimension);
        logButton.setToolTipText("Show LOG");
        JLabel statusLabel = new JLabel("...");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        JPanel groupsPanel = new JPanel();
        groupsPanel.setLayout(new GridBagLayout());
        groupsPanel.setBorder(BorderFactory.createEtchedBorder());
        final JTextField fileField = new JTextField();
        JLabel label = new JLabel("Исходный файл ");
        JButton openFileButton = new JButton(new AbstractAction("...") {
            public void actionPerformed(ActionEvent e) {
                //Handle open button action.
                final JFileChooser fc = new JFileChooser(new File("").getAbsolutePath());
                int returnVal = fc.showOpenDialog(mainFrame);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    logln("Opening: " + file.getName());
                    fileField.setText(file.getAbsolutePath());
                    filePath = file.getAbsolutePath();
                } else {
                    logln("Open command cancelled by user.");
                }
            }
        });
        openFileButton.setSize(smallButtonDimension);
        openFileButton.setMaximumSize(smallButtonDimension);
        openFileButton.setMinimumSize(smallButtonDimension);
        openFileButton.setPreferredSize(smallButtonDimension);

        groupsPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(fileField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(openFileButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(new JLabel("Результирующий файл будет лежать рядом с исходным с префиксом MD5"), new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(new JLabel("Колонки MD5 "), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(columnsToHash, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(new JLabel("Разделитель "), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 1, 0, 0), 0, 0));
        groupsPanel.add(delimeterTextField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 1, 0, 0), 0, 0));

        mainContainer.add(groupsPanel, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 1, 0, 0), 0, 0));
        mainContainer.add(runButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 1, 5), 0, 0));
        mainContainer.add(logButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 1, 5), 0, 0));
        mainContainer.add(closeButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 1, 1), 0, 0));
        mainContainer.add(statusLabel, new GridBagConstraints(0, 3, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 1, 1, 1), 0, 0));
        //mainFrame.pack();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    private int[] getColumnsToHash() {
        String str[] = columnsToHash.getText().split(",");
        int i = 0;
        int[] ints = new int[str.length];
        for(String s : str) {
            ints[i++] = Integer.valueOf(s.trim());
            //log(s.trim()+" ");
        }
        //logln("");
        return ints;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void runMD5Process() throws IOException {
        //File sample = new File(new File("").getAbsolutePath() + "\\test_file.txt");
        if (filePath.trim().isEmpty()) {
            logln("Select valid file first!!!");
            return;
        }
        File sample = new File(filePath);
        logln(sample.getName());
        logln(sample.canRead());
        InputStream fis;
        String line;
        fis = new FileInputStream(sample.getName());
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        while ((line = br.readLine()) != null) {
            logln(line);
            String converted = this.convertToMD5(line, delimeterTextField.getText().trim(), getColumnsToHash());
            logln(converted);
            logString.append(converted);
            writeToFile(converted);

            // Deal with the line
        }

// Done with the file
        br.close();
        br = null;
        fis = null;
    }

    private void showLogDialog(String title, String content, boolean enableClearButton) {
        final JDialog logDialog = new JDialog(mainFrame, title, true);
        logDialog.getContentPane().setLayout(new GridBagLayout());
        final JEditorPane editor = new JEditorPane();
        editor.setText(content);
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setBorder(BorderFactory.createEtchedBorder());
        JButton closeButton = new JButton(new AbstractAction("Close") {
            public void actionPerformed(ActionEvent e) {
                logDialog.setVisible(false);
            }
        });
        closeButton.setToolTipText("Close LOG");
        JButton clearButton = new JButton(new AbstractAction("Clear") {
            public void actionPerformed(ActionEvent e) {
                logString = new StringBuffer("");
                editor.setText("");
            }
        });
        clearButton.setToolTipText("Clear LOG");
        closeButton.setPreferredSize(buttonDimension);
        closeButton.setMinimumSize(buttonDimension);
        clearButton.setPreferredSize(buttonDimension);
        clearButton.setMinimumSize(buttonDimension);
        logDialog.getContentPane().add(scroll, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
        logDialog.getContentPane().add(closeButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        if (enableClearButton)
            logDialog.getContentPane().add(clearButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        logDialog.setSize(mainFrame.getSize());
        logDialog.setLocation(mainFrame.getLocation());
        logDialog.setMinimumSize(minDimension);
        logDialog.setVisible(true);
    }

    private static StringBuffer getLog() {
        return logString;
    }

    private void exit() {
        System.exit(0);
    }


    public static void writeToFile(String text) throws IOException {
        File resultFile = new File(new File("").getAbsolutePath() + "\\MD5_"+new File(filePath).getName());
/*
        resultFile.delete();
        resultFile.createNewFile();
*/
        if(!resultFile.exists()) {
            resultFile.createNewFile();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile, true))) {
            bw.write(text);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

/*
    @Setup(Level.Invocation)
    public void setUp() {
        //murmur3 = Hashing.murmur3_128().newHasher();
    }
*/

    private String convertToMD5(String input, String token, int... lines) {
        StringBuilder convertedLine = new StringBuilder("");
        StringTokenizer st = new StringTokenizer(input, token);
        //int n = st.countTokens();
        String [] strings = input.split("["+token+"]", 5);
        int n = strings.length;
        for(int i = 0; i < n; i++) {
//            String tok = st.nextToken();
            String tok = strings[i];

            for(int line : lines) {
                if(!tok.isEmpty() && i+1 == line)
                    tok = getHashedString(tok);
            }
            convertedLine.append(tok).append(delimeterTextField.getText().trim());
        }
        return convertedLine.substring(0, convertedLine.length() - 1);
    }

    public static void log(Object log) {
        System.out.print(log);
        getLog().append(log);
    }

    public static void logln(Object log) {
        log(log + "\n");
    }

    public static void main(String[] args)throws Exception
    {
        var t = 1;
        System.out.println("T is: " + t);
        MD5Hashing md5Hashing = new MD5Hashing();
        md5Hashing.initializeGUI();
        //org.openjdk.jmh.Main.main(args);
    }
}
