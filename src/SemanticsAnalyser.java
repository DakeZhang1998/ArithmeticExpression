import sun.reflect.generics.tree.Tree;

public class SemanticsAnalyser {
    TreeNode root;
    double curResult;

    public SemanticsAnalyser(TreeNode root) {
        this.root = root;
    }

    public double calculate(TreeNode root) {
        return curResult;
    }
}
