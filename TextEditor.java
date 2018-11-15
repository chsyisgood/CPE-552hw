//import javafx.scene.control.TextInputDialog;
/*
    Author: Siyu Chen
    CWID: 10424481
    Title: TextEditor in Java
    Comment: Please compile and run in WINDOWS OS!!! ALL the functions are completed,
    including Create a New .java file in selected directories, Choose and open a file
    Save, Quit, Compile and Run. You need to change the variable "final String envp" to
    your own JDK path in Windows.
    1. Please compile and run in WINDOWS OS
    2. You need to change the variable "final String envp" to your own JDK path in Windows.
    3. !!!Now!!! We can catch the error message and Press F4 to skip to the line !!!
    4.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import java.lang.*;

public class TextEditor extends JFrame{
    final String envp = "Path=C:\\Program Files\\Java\\jdk-11.0.1\\bin";
    final String currentPath = System.getProperty("user.dir");
    private Container ctn;
    private Font ft1;
    private Font ft2;
    private JMenuBar menu_bar;
    private JMenu menu_0;
    private JMenu menu_1;
    private JMenuItem menu_item;
    private JTextArea text_area;
    private JTextArea text_area_result;
    private JFileChooser chooser;
    private JTextField text_field;
    private String str_pkg;       // Absolute path of file, eg: "C:\GraduateStudy\C++\CPEjava\session06\MyClass.java"
    private String str_before;    // string in text_area, used to judge whether file content is changed
    private String str_path;      // getParent() of file, eg:"C:\GraduateStudy\C++\CPEjava\session06"
    private String str_filename1; // Name of file, eg:"MyClass.java"
    private String str_filename2; // Name of file excludes file class, eg:"MyClass"
    private StringBuilder strb_content;    // a string builder
    private JScrollPane text_area_scroll;  // scroll
    private DialogFrame dialog_frame;
    private List<String> regexSplitLine;  // All the matched part of error message using regex
    private List<Integer> regexSplitLineInt;   // All the num of matched error message rows
    private int cursorLineNum = 0;  // Record the rows of error line

    public TextEditor(){
        super("Text Editor");
        setSize(800,600);
        ctn = getContentPane();
        ft1 = new Font("Times",Font.BOLD,18);
        ft2 = new Font("Times",Font.PLAIN,15);
        strb_content = new StringBuilder("// Java Test Editor: \n");
        str_before = "// Java Test Editor: \n";

        text_field = new JTextField("File :");
        text_field.setFont(ft2);
        ctn.add(text_field,BorderLayout.NORTH);

        text_area = new JTextArea(strb_content.toString());
        text_area.setFont(ft2);
        ctn.add(text_area,BorderLayout.CENTER);
        text_area_result = new JTextArea("Result :");
        text_area_result.setFont(ft2);
        ctn.add(text_area_result,BorderLayout.SOUTH);

        text_area_scroll = new JScrollPane(text_area);
        text_area_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        text_area_scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ctn.add(text_area_scroll);

        menu_bar = new JMenuBar();
        setJMenuBar(menu_bar);

        menu_0 = new JMenu("File");
        menu_0.setFont(ft1);
        menu_bar.add(menu_0);
        menu_1 = new JMenu("Build");
        menu_1.setFont(ft1);
        menu_bar.add(menu_1);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        regexSplitLine = new ArrayList<>();
        regexSplitLineInt = new ArrayList<>();

        text_area.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F4:{
                        if(!regexSplitLineInt.isEmpty()){
                            showNextErrorLine();
                        }
                        break;
                    }
                }
            }
        });
        text_area_result.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F4:{
                        if(!regexSplitLineInt.isEmpty()){
                            showNextErrorLine();
                        }
                        break;
                    }
                }
            }
        });
        text_field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_F4:{
                        if(!regexSplitLineInt.isEmpty()){
                            showNextErrorLine();
                        }
                        break;
                    }
                }
            }
        });

        String s_name[] = {"New","Open","Save","Quit","Compile","Run"};
        for(int i = 0; i < 6; i++){
            menu_item = new JMenuItem(s_name[i]);
            menu_item.setFont(ft1);
            if(i < 4)
                menu_0.add(menu_item);
            else
                menu_1.add(menu_item);
            if(i == 0)
                menu_item.addActionListener(new ActionListener_New());
            if(i == 1)
                menu_item.addActionListener(new ActionListener_Choose_File());
            if(i == 2)
                menu_item.addActionListener(new ActionListener_Save());
            if(i == 3)
                menu_item.addActionListener(new ActionListener_Quit());
            if(i == 4)
                menu_item.addActionListener(new ActionListener_Compile());
            if(i == 5)
                menu_item.addActionListener(new ActionListener_Run());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void textReader()throws FileNotFoundException,IOException{
        BufferedReader br = new BufferedReader(new FileReader(str_pkg));
        String line;
        strb_content = new StringBuilder();
        while ((line = br.readLine())!= null){
            strb_content.append(line+"\n");
        }
        text_field.setText(str_pkg);
        text_area.setText(strb_content.toString());
        str_before = strb_content.toString();
    }
    public void textWriter()throws IOException{
        PrintWriter p = new PrintWriter(new FileWriter(str_pkg));
        String s = text_area.getText();
        p.print(s);
        str_before = s;
        p.close();
    }
    public void callQuitDialog(){
        int result = JOptionPane.showConfirmDialog(
                this, "Do you want to SAVE before quit？",
                "Dialog", JOptionPane.YES_NO_CANCEL_OPTION
        );
        // result = 0 -> SAVE and QUIT    result = 1 -> QUIT WITHOUT SAVE
        // result = -1 , 2 -> CANCEL
        if(result == 0){
            try {
                textWriter();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            System.exit(0);
        }
        if(result == 1)
            System.exit(0);
    }

    public void showNextErrorLine(){
        try{
            int lineNum = regexSplitLineInt.get(cursorLineNum) - 1;
            int selectionStart = text_area.getLineStartOffset(lineNum);
            int selectionEnd = text_area.getLineEndOffset(lineNum);
            text_area.requestFocus();
            text_area.setSelectionStart(selectionStart);
            text_area.setSelectionEnd(selectionEnd);
            cursorLineNum++;
            if(cursorLineNum >= regexSplitLineInt.size())
                cursorLineNum = 0;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void regexSplit(String line){
        String regex = "(([a-zA-Z])\\w+.java:[0-9]+)";
        regexSplitLine.clear();
        Pattern ptn = Pattern.compile(regex);
        Matcher matcher = ptn.matcher(line);
        while(matcher.find()){
            regexSplitLine.add(matcher.group());
        }
        if(!regexSplitLine.isEmpty()){
            regexSplitLineInt.clear();
            for(String s : regexSplitLine){
                String sNum = s.substring(s.lastIndexOf(':') + 1, s.length());
                regexSplitLineInt.add(Integer.valueOf(sNum));
                System.out.println(sNum);
            }
        }
        //System.out.println(regexSplitLine);
    }
    public void CompileNRun(){
        try {
            String arr[] = {"CLASSPATH=" + str_path, envp};
            String cmd = "cmd /c javac " + str_filename2 + ".java && java " + str_filename2;
            Process proc1 = Runtime.getRuntime().exec(cmd, arr, new File(str_path));
            System.out.println(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            boolean isCompiled = false;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                isCompiled = true;
            }
            br = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            if(isCompiled)
                text_area_result.setText("Result(Compiling then Running): \n" + sb.toString());
            else{
                regexSplit(sb.toString());
                text_area_result.setText("Result(Existing Errors): \n" + sb.toString() + "" +
                        "\n Compile Error: Please press F4 to skip to the error line");
            }
            //System.out.println(sb.toString());
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void Run(){
        try {
            String arr[] = {"CLASSPATH=" + str_path, envp};
            String cmd = "java " + str_filename2;
            Process proc1 = Runtime.getRuntime().exec(cmd, arr, new File(str_path));
            System.out.println(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            boolean isCompiled = false;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                isCompiled = true;
            }
            br = new BufferedReader(new InputStreamReader(proc1.getErrorStream()));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            if(isCompiled)
                text_area_result.setText("Result(Compiling then Running): \n" + sb.toString());
            else{
                regexSplit(sb.toString());
                text_area_result.setText("Result(Existing Errors): \n" + sb.toString() + "" +
                        "\n Compile Error: Please press F4 to skip to the error line");
            }
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    class DialogFrame extends JFrame{
        JPanel jp;
        JTextField jtf1;
        JTextField jtf2;
        JButton jb1;
        JButton jb2;
        String text_directories;
        String text_filename;
        public DialogFrame() {
            jp = new JPanel();
            JLabel jl1 = new JLabel("Please enter a file name");
            JLabel jl2 = new JLabel("File Name");
            jtf1 = new JTextField("MyClass.java",40);
            jtf2 = new JTextField(currentPath,30);
            jb1 = new JButton("Choose Directories");
            jb2 = new JButton("Confirm");
            jp.add(jl1);
            jp.add(jtf1);
            jp.add(jl2);
            jp.add(jtf2);
            jp.add(jb1);
            jp.add(jb2);
            add(jp);
            jb1.addActionListener(new ActionListener_Choose_Directories());
            jb2.addActionListener(new ActionListener_ConfirmDialog());
            //
            setTitle("Create a new .java file");//
            setSize(500, 200);//
            setLocationRelativeTo(null);//
            setVisible(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);//
        }
        class ActionListener_Choose_Directories implements ActionListener {
            public void actionPerformed(ActionEvent e){
                chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setCurrentDirectory(new File(currentPath));
                chooser.showDialog(new JLabel(), "Choose Directories");
                File file = chooser.getSelectedFile();
                text_directories =  file.getAbsoluteFile().toString();
                jtf2.setText(text_directories);
                //text_field.setText(text_directories);
            }
        }
        class ActionListener_ConfirmDialog implements ActionListener{
            public void actionPerformed(ActionEvent e){
                text_directories = jtf2.getText();
                text_filename = jtf1.getText();
                File file = new File(text_directories, text_filename);
                str_pkg = file.getAbsolutePath();
                System.out.println(file.exists());
                System.out.println(text_directories + " " + text_filename);
                try {
                    if(!file.exists()) {
                        file.createNewFile();
                    }
                    else
                        ;
                    str_pkg = file.getAbsolutePath();
                    str_path = file.getParent();
                    str_filename1 = file.getName();
                    str_filename2 = str_filename1.substring(0, str_filename1.lastIndexOf("."));
                    textReader();
                    setVisible(false);
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    class ActionListener_New implements ActionListener {
        public void actionPerformed(ActionEvent e){
            dialog_frame = new DialogFrame();
        }
    }
    class ActionListener_Choose_File implements ActionListener {
        public void actionPerformed(ActionEvent e){
            chooser = new JFileChooser();
            if(str_path != null && str_path !="")
                chooser.setCurrentDirectory(new File(str_path));
            else
                chooser.setCurrentDirectory(new File(currentPath));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.showDialog(new JLabel(), "Choose File");
            File file = chooser.getSelectedFile();
            try {
                str_pkg = file.getAbsoluteFile().toString();
                str_path = file.getParent();
                str_filename1 = file.getName();
                str_filename2 = str_filename1.substring(0, str_filename1.lastIndexOf("."));
                textReader();
                System.out.println(str_path+"\\"+str_filename2) ;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    class ActionListener_Save implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                String s = text_area.getText();
                if(!s.equals(str_before)){
                    textWriter();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    class ActionListener_Quit implements ActionListener {
        public void actionPerformed(ActionEvent e){
            String s = text_area.getText();
            if(s.equals(str_before)){
                System.exit(0);
            }
            else{
                callQuitDialog();
            }
        }
    }
    class ActionListener_Compile implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                if (str_pkg != null && str_pkg != "") {
                    String s = text_area.getText();
                    if (!s.equals(str_before)) {    // Check whether the file content is changed
                        textWriter();
                    }
                    System.out.println(str_path);
                    CompileNRun();
                } else
                    text_area_result.setText("Result: \nNo file Opened!!!");
            }
            catch (Exception e4) {
                e4.printStackTrace();
            }
        }
    }
    class ActionListener_Run implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try {
                if (str_pkg != null && str_pkg != "") {
                    File file1 = new File(str_pkg);
                    File file2 = new File(str_path + "\\" + str_filename2 + ".class");
                    if (file2.exists()) {    // judge whether file is compiled
                        String s = text_area.getText();
                        if (!s.equals(str_before)) {    // Check whether the file content is changed
                            textWriter();
                        }
                        if(file1.lastModified() <= file2.lastModified())    // Check the lastModified time
                            Run();
                        else
                            CompileNRun();
                    } else
                        CompileNRun();
                } else
                    text_area_result.setText("Result: \nNo file Opened!!!");
            }
            catch (Exception e4) {
                e4.printStackTrace();
            }
        }
    }
    public static void main(String[] args)throws IOException{
        TextEditor t_e = new TextEditor();
    }
}
