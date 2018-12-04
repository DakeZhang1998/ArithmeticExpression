import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.TreeSet;

class TreeNode {
    String curNode;
    TreeNode parent;
    ArrayList<TreeNode> children;
    public double val = 0;
    public int layer = 0;
    public int colCount = 0;           // Marks the number of columns used in painting the grammar tree.

    public TreeNode(String curNode, TreeNode parent) {
        this.curNode = curNode;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode node) {
        node.layer = this.layer + 1;
        setChildrenLayer(node);
        this.children.add(node);
    }

    private void setChildrenLayer(TreeNode n){
        if(n.children.size() != 0){
            ArrayList<TreeNode> c = n.children;
            for(TreeNode node : c){
                node.layer = node.layer + 1;
                setChildrenLayer(node);
            }
        }
    }
}

public class Parser {
    public ArrayList<TokenInformation> tokens;
    public int curIndex = 0;
    public String productionStr = "";
    public String treeStr = "";
    public double result = 0;

    public Parser(ArrayList<TokenInformation> tokens) {
        this.tokens = tokens;
    }

    public TreeNode E(TreeNode node) throws Exception{
        TreeNode curNode = new TreeNode("E",node);
        productionStr += "E->TE'" + "\n";
        TreeNode TNode = T(curNode);
        if (TNode != null) {
            curNode.addChild(TNode);
            TreeNode E2Node = E2(curNode);
            if (E2Node != null) {
                curNode.addChild(E2Node);
                curNode.val = TNode.val + E2Node.val;
                curNode.colCount = TNode.colCount + E2Node.colCount;
                return curNode;
            }
            return null;
        }
        return null;
    }

    public TreeNode E() throws Exception{
        TreeNode curNode = new TreeNode("E",null);
        productionStr += "E->TE'" + "\n";
        TreeNode TNode = T(curNode);
        if (TNode != null) {
            curNode.addChild(TNode);
            TreeNode E2Node = E2(curNode);
            if (E2Node != null) {
                curNode.addChild(E2Node);
                if(tokens.get(curIndex).type != TokenType.EOF){
                    String error_message = "Location " + (tokens.get(curIndex).index + 1) + " Error: There is an unmatched right bracket.";
                    throw new Exception(error_message);
                }
                curNode.val = TNode.val + E2Node.val;
                curNode.colCount = TNode.colCount + E2Node.colCount;
                return curNode;
            }
            return null;
        }
        return null;
    }

    public TreeNode E2(TreeNode node) throws Exception{
        TreeNode curNode = new TreeNode("E'",node);
        boolean isAdd = true;
        if (tokens.get(curIndex).type == TokenType.PLUS || tokens.get(curIndex).type == TokenType.MINUS) {
            if (tokens.get(curIndex).type == TokenType.PLUS) {
                productionStr += "E'->+TE'" + "\n";
                TreeNode plusNode = new TreeNode("+", curNode);
                plusNode.colCount = 1;
                curNode.addChild(plusNode);
            } else {
                TreeNode plusNode = new TreeNode("-", curNode);
                plusNode.colCount = 1;
                curNode.addChild(plusNode);
                productionStr += "E'->-TE'" + "\n";
                isAdd = false;
            }
            readNextToken();
            TreeNode TNode = T(node);
            if (TNode != null) {
                curNode.addChild(TNode);
                TreeNode E2Node = E2(node);
                if (E2Node != null) {
                    curNode.addChild(E2Node);
                    if (isAdd)
                        curNode.val = TNode.val + E2Node.val;
                    else
                        curNode.val = - TNode.val + E2Node.val;
                    curNode.colCount = TNode.colCount + E2Node.colCount + 1;
                    return curNode;
                }
                return null;
            }
            return null;
        }
        productionStr += "E'->e" + "\n";
        TreeNode nullNode = new TreeNode("e", curNode);
        nullNode.colCount = 1;
        curNode.addChild(nullNode);
        curNode.val = 0;
        curNode.colCount = 1;
        return curNode;
    }

    public TreeNode T(TreeNode node) throws Exception{
        TreeNode curNode = new TreeNode("T",node);
        productionStr += "T->FT'" + "\n";
        TreeNode FNode = F(curNode);
        if (FNode != null) {
            curNode.addChild(FNode);
            TreeNode T2Node = T2(curNode);
            if (T2Node != null) {
                curNode.addChild(T2Node);
                curNode.val = FNode.val * T2Node.val;
                curNode.colCount = FNode.colCount + T2Node.colCount;
                return curNode;
            }
            return null;
        }
        return null;
    }

    public TreeNode T2(TreeNode node) throws Exception{
        TreeNode curNode = new TreeNode("T'",node);
        boolean isMultiply = true;
        if (tokens.get(curIndex).type == TokenType.MULTIPLE || tokens.get(curIndex).type == TokenType.DIVIDE) {
            if (tokens.get(curIndex).type == TokenType.MULTIPLE) {
                productionStr += "T'->*FT'" + "\n";
                TreeNode plusNode = new TreeNode("*", curNode);
                plusNode.colCount = 1;
                curNode.addChild(plusNode);
            } else {
                productionStr += "T'->/FT'" + "\n";
                TreeNode plusNode = new TreeNode("/", curNode);
                plusNode.colCount = 1;
                curNode.addChild(plusNode);
                isMultiply = false;
            }
            if (readNextToken()) {
                TreeNode FNode = F(curNode);
                if (FNode != null) {
                    curNode.addChild(FNode);
                    TreeNode T2Node = T2(curNode);
                    if (T2Node != null) {
                        curNode.addChild(T2Node);
                        if (isMultiply)
                            curNode.val = FNode.val * T2Node.val;
                        else
                            curNode.val = 1 / FNode.val * T2Node.val;
                        curNode.colCount = FNode.colCount + T2Node.colCount + 1;
                        return curNode;
                    }
                    return null;
                }
                return null;
            }
            return null;
        }
        productionStr += "T'->e" + "\n";
        TreeNode nullNode = new TreeNode("e", curNode);
        nullNode.colCount = 0;
        curNode.addChild(nullNode);
        curNode.val = 1;
        curNode.colCount = 1;
        return curNode;
    }

    public TreeNode F(TreeNode node) throws Exception {
        TreeNode curNode = new TreeNode("F",node);
        if (tokens.get(curIndex).type == TokenType.MINUS) {
            productionStr += "F->-F'" + "\n";
            TreeNode plusNode = new TreeNode("-", curNode);
            plusNode.colCount = 1;
            curNode.addChild(plusNode);
            if (readNextToken()) {
                TreeNode F2Node = F2(curNode);
                if (F2Node != null) {
                    curNode.addChild(F2Node);
                    curNode.val = - F2Node.val;
                    curNode.colCount = 1 + F2Node.colCount;
                    return curNode;
                }
                return null;
            }
            return null;
        }
        else {
            productionStr += "F->F'" + "\n";
            TreeNode F2Node = F2(curNode);
            if (F2Node != null) {
                curNode.addChild(F2Node);
                curNode.val = F2Node.val;
                curNode.colCount = F2Node.colCount;
                return curNode;
            }
            return null;
        }
    }

    public TreeNode F2(TreeNode node) throws Exception{
        TreeNode curNode = new TreeNode("F'",node);
        if (tokens.get(curIndex).type == TokenType.LEFTBRACKET) {
            productionStr += "F'->(E)" + "\n";
            TreeNode leftBracketNode = new TreeNode("(", curNode);
            leftBracketNode.colCount = 1;
            curNode.addChild(leftBracketNode);
            if (readNextToken()) {
                TreeNode ENode = E(curNode);
                if (ENode != null) {
                    curNode.addChild(ENode);
                    if (tokens.get(curIndex).type == TokenType.RIGHTBRACKET) {
                        TreeNode rightBracketNode = new TreeNode(")", curNode);
                        rightBracketNode.colCount = 1;
                        curNode.addChild(rightBracketNode);
                        if (readNextToken()) {
                            curNode.val = ENode.val;
                            curNode.colCount = 2 + ENode.colCount;
                            return curNode;
                        }
                        else {
                            curIndex++;
                            curNode.val = ENode.val;
                            curNode.colCount = 2 + ENode.colCount;
                            return curNode;
                        }
                    }
                    String error_message = "Location " + (tokens.get(curIndex).index + 1) + " Error: Missing a right bracket.";
                    throw new Exception(error_message);
                    //System.out.println("Location " + (tokens.get(curIndex).index + 1) + " Error: Missing a right bracket.");
                }
                return null;
            }
        }
        else if (tokens.get(curIndex).type == TokenType.IDENTIFIER || tokens.get(curIndex).type == TokenType.INT
                || tokens.get(curIndex).type == TokenType.DOUBLE) {
            productionStr += "F'->i" + "\n";
            TreeNode iNode = new TreeNode("i", curNode);
            curNode.addChild(iNode);
            TreeNode valNode = new TreeNode(tokens.get(curIndex).information, iNode);
            valNode.colCount = 1;
            iNode.addChild(valNode);
            curNode.val = Double.valueOf(tokens.get(curIndex).information);
            curNode.colCount = 1;
            readNextToken();
            return curNode;
        }
        String error_message = "Location " + (tokens.get(curIndex).index + 1) + " Error: wrong usage of operator.";
        throw new Exception(error_message);
        //System.out.println("Location " + (tokens.get(curIndex).index + 1) + " Error: wrong usage of operator.");
        //return null;
    }

    public boolean readNextToken() {
        if (curIndex < tokens.size()-1) {
            curIndex++;
            return true;
        } else {
            //curIndex++;
            return false;
        }
    }

    public TreeNode parser() {
        TreeNode root = null;
        try{
            root =  E();
        }catch (Exception e){
            System.out.println(e.getMessage());
            productionStr += e.getMessage() + "\n";
        }
        if(root!=null){
            treeStr += "- E\n";
            printTreeNode(root, 1);
        }
        return root;
    }

    public void printTreeNode(TreeNode node, int level) {
        String preStr = "";
        for(int i = 0; i < level; i++) {
            preStr += "     ";
        }

        for(int i = 0; i < node.children.size(); i++) {
            TreeNode t = node.children.get(i);
            treeStr += preStr + "- " + t.curNode + "\n";
            // System.out.println(preStr + "- " + t.curNode);

            if(!t.children.isEmpty()) {
                printTreeNode(t, level + 1);
            }
        }
    }
}
