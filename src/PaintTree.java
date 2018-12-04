import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * TODO 同一层结点过多有BUG，应该对每一层的所有结点都进行个数统计，之后才绘制。
 * @author John
 *
 */
class TreePanel extends JPanel {
    private TreeNode tree;				//保存整棵树
    private int gridWidth = 20;		//每个结点的宽度
    private int gridHeight = 20;	//每个结点的高度
    private int vGap = 30;			//每2个结点的垂直距离
    private int hGap = 30;			//每2个结点的水平距离

    private int startY = 10;		//根结点的Y，默认距离顶部10像素
    private int startX = 0;			//根结点的X，默认水平居中对齐

    private int childAlign;						//孩子对齐方式
    public static int CHILD_ALIGN_ABSOLUTE = 0;	//相对Panel居中
    public static int CHILD_ALIGN_RELATIVE = 1;	//相对父结点居中

    private Font font = new Font("微软雅黑", Font.BOLD,14);	//描述结点的字体

    private Color gridColor = Color.BLACK;		//结点背景颜色
    private Color linkLineColor = Color.BLACK;	//结点连线颜色
    private Color stringColor = Color.WHITE;	//结点描述文字的颜色


    /**
     * 根据传入的Node绘制树，以绝对居中的方式绘制
     * @param n 要绘制的树
     */
    public TreePanel(TreeNode n){
        this(n,CHILD_ALIGN_ABSOLUTE);
    }

    /**
     * 设置要绘制时候的对齐策略
     * @param childAlign 对齐策略
     * @see tree.TreePanel#CHILD_ALIGN_RELATIVE
     * @see tree.TreePanel#CHILD_ALIGN_ABSOLUTE
     */

    public TreePanel(int childAlign){
        this(null,childAlign);
    }

    /**
     * 根据孩子对齐策略childAlign绘制的树的根结点n
     * @param n 要绘制的树的根结点
     * @param childAlign 对齐策略
     */
    public TreePanel(TreeNode n, int childAlign){
        super();
        setTree(n);
        this.childAlign = childAlign;
    }

    /**
     * 设置用于绘制的树
     * @param n 用于绘制的树的
     */
    public void setTree(TreeNode n) {
        tree = n;
    }

    //重写而已，调用自己的绘制方法
    public void paintComponent(Graphics g){
        startX = - gridWidth/2 + (tree.colCount * (gridWidth + hGap) - hGap) / 2 + 20;
        // startX = (getWidth()-gridWidth)/2;
        super.paintComponent(g);
        g.setFont(font);
        drawAllNode(tree, startX, g);
    }

    /**
     * 递归绘制整棵树
     * @param n 被绘制的Node
     * @param xPos 根节点的绘制X位置
     * @param g 绘图上下文环境
     */
    public void drawAllNode(TreeNode n, int x, Graphics g){
        int y = n.layer * (vGap + gridHeight) + startY;
        int fontY = y + gridHeight - 5;		//5为测试得出的值，你可以通过FM计算更精确的，但会影响速度

        g.setColor(gridColor);
        g.fillRect(x, y, gridWidth, gridHeight);	//画结点的格子

        g.setColor(stringColor);
        g.drawString(n.curNode, x, fontY);		//画结点的名字

        if(n.children.size() != 0){
            ArrayList<TreeNode> c = n.children;
            int size = n.colCount;
            int tempPosx = childAlign == CHILD_ALIGN_RELATIVE
                    ? x + gridWidth/2 - (size * (gridWidth + hGap) - hGap) / 2
                    : (getWidth() - size*(gridWidth+hGap)+hGap)/2;

            ArrayList<Integer> columns = new ArrayList<Integer>();
            for(TreeNode node : c){
                columns.add(node.colCount);
                int i = 0;
                for (Integer in : columns)
                    i += in;
                double bias = i - node.colCount / 2.0;
                int newX = (int)(tempPosx+(gridWidth+hGap) * bias);	//孩子结点起始X
                g.setColor(linkLineColor);
                g.drawLine(x+gridWidth/2, y+gridHeight, newX+gridWidth/2, y+gridHeight+vGap);	//画连接结点的线
                drawAllNode(node, newX, g);
            }
        }
    }

    public Color getGridColor() {
        return gridColor;
    }

    /**
     * 设置结点背景颜色
     * @param gridColor 结点背景颜色
     */
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public Color getLinkLineColor() {
        return linkLineColor;
    }

    /**
     * 设置结点连接线的颜色
     * @param gridLinkLine 结点连接线的颜色
     */
    public void setLinkLineColor(Color gridLinkLine) {
        this.linkLineColor = gridLinkLine;
    }

    public Color getStringColor() {
        return stringColor;
    }

    /**
     * 设置结点描述的颜色
     * @param stringColor 结点描述的颜色
     */
    public void setStringColor(Color stringColor) {
        this.stringColor = stringColor;
    }

    public int getStartY() {
        return startY;
    }

    /**
     * 设置根结点的Y位置
     * @param startY 根结点的Y位置
     */
    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartX() {
        return startX;
    }

    /**
     * 设置根结点的X位置
     * @param startX 根结点的X位置
     */
    public void setStartX(int startX) {
        this.startX = startX;
    }


}


public class PaintTree extends JFrame{
    private TreeNode root;

    public PaintTree(TreeNode root){
        super("Graph of Tree");
        this.root = root;
        initComponents();
    }

    public void reprint(TreeNode root) {
        this.root = root;
        initComponents();
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input the arithmetic expression:");
        String sentence = scanner.nextLine();

        Lexer lexer = new Lexer(0,-1,sentence);
        ArrayList<TokenInformation> tokenInformations = null;
        try{
            tokenInformations = lexer.parser();
            String result = "";
            for(TokenInformation tokenInformation:tokenInformations){
                result+=tokenInformation.toString();
            }
        }catch (Exception e1) {
            e1.printStackTrace();
        }
        Parser parser = new Parser(tokenInformations);
        TreeNode root = parser.parser();

        PaintTree frame = new PaintTree(root);

        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void initComponents(){
        //n.printAllNode(n);	//输出树

        /*
         * 创建一个用于绘制树的面板并将树传入,使用相对对齐方式
         */
        TreePanel panel1 = new TreePanel(TreePanel.CHILD_ALIGN_RELATIVE);
        panel1.setTree(root);

        /*
         * 创建一个用于绘制树的面板并将树传入,使用绝对对齐方式
         */
//        TreePanel panel2 = new TreePanel(TreePanel.CHILD_ALIGN_ABSOLUTE);
//        panel2.setTree(root);
//        panel2.setBackground(Color.BLACK);
//        panel2.setGridColor(Color.WHITE);
//        panel2.setLinkLineColor(Color.WHITE);
//        panel2.setStringColor(Color.BLACK);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(1,1));
        contentPane.add(panel1);
//        contentPane.add(panel2);

        add(contentPane,BorderLayout.CENTER);
    }
}

