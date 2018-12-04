import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CalculatorGUI {
    static boolean isFirst = true;
    private PaintTree frame;
    private JPanel FrontPanel;
    private JPanel InputPanel;
    private JPanel TokensPanel;
    private JPanel ProductionPanel;
    private JButton RunButton;
    private JTextArea InputArea;
    private JTextArea ProductionDisplay;
    private JTextArea TokenDisplay;
    private JTextArea ResultDisplay;

    public CalculatorGUI() {
        RunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sentence = InputArea.getText();
                Lexer lexer = new Lexer(0,-1,sentence);
                ArrayList<TokenInformation> tokenInformations = null;
                try{
                    tokenInformations = lexer.parser();
                    String result = "";
                    for(TokenInformation tokenInformation:tokenInformations){
                        result+=tokenInformation.toString();
                    }
                    TokenDisplay.setText(result);
                }catch (Exception e1) {
                    e1.printStackTrace();
                    TokenDisplay.setText(e1.getMessage());
                }

                Parser parser = new Parser(tokenInformations);
                TreeNode root = parser.parser();
                ProductionDisplay.setText(parser.productionStr);
                ResultDisplay.setText(String.valueOf(root.val));

                if (isFirst) {
                    frame = new PaintTree(root);

                    frame.setSize(800, 600);
                    frame.setVisible(true);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                else {
                    frame.reprint(root);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CalculatorGUI");
        CalculatorGUI calculatorGUI = new CalculatorGUI();
        frame.setContentPane(calculatorGUI.FrontPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
