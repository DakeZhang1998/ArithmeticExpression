import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class Interface extends JFrame implements ActionListener
{

    JButton parser = new JButton("词法+语法");
    JLabel  sentence = new JLabel("表达式：");
    //JLabel result = new JLabel("结果：");
    JTextField JSentence= new JTextField(10);
    //JTextField JResult= new JTextField(10);
    JTextArea  LexerResult = new JTextArea(4,20);
    JTextArea  ParserResult = new JTextArea(4,20);
    JTextArea  treeResult = new JTextArea(4,30);
    public Interface()
    {
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout(FlowLayout.CENTER));
        //jp.setLayout(new GridLayout(3,2));  //3行2列的面板jp（网格布局）
        LexerResult.setLineWrap(true);
        LexerResult.setWrapStyleWord(true);
        ParserResult.setLineWrap(true);
        ParserResult.setWrapStyleWord(true);
        treeResult.setLineWrap(true);
        treeResult.setWrapStyleWord(true);
        sentence.setHorizontalAlignment(SwingConstants.RIGHT);  //设置该组件的对齐方式为向右对齐
        //result.setHorizontalAlignment(SwingConstants.RIGHT);

        jp.add(sentence);
        jp.add(JSentence);
        //jp.add(result);
        //jp.add(JResult);
        jp.add(parser);
        jp.add(LexerResult);
        jp.add(ParserResult);
        jp.add(treeResult);

        parser.addActionListener(this);

        this.add(jp,BorderLayout.CENTER);	//将整块面板定义在中间

        this.setTitle("算术表达式");
        this.setLocation(500,300);	//设置初始位置
        this.pack();  		//表示随着面板自动调整大小
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void actionPerformed(ActionEvent e)
    {
        String sentence = JSentence.getText();
        Lexer lexer = new Lexer(0,-1,sentence);
        ArrayList<TokenInformation> tokenInformations = null;
        try{
            tokenInformations = lexer.parser();
            String result = "";
            for(TokenInformation tokenInformation:tokenInformations){
                result+=tokenInformation.toString();
            }
            LexerResult.setText(result);
            //JResult.setText(result);
        }catch (Exception e1) {
            e1.printStackTrace();
            LexerResult.setText(e1.getMessage());
        }
        Parser parser = new Parser(tokenInformations);
        TreeNode root = parser.parser();
        ParserResult.setText(parser.productionStr);
        treeResult.setText(parser.treeStr);
    }


    public static void main(String[] args)
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new Interface();
    }
}


