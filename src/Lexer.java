import java.util.ArrayList;

enum TokenType {
    INT,           //整数
    DOUBLE,          //浮点数
    IDENTIFIER,   //标识符
    PLUS,          //加
    MINUS,         //减
    MULTIPLE,      //乘
    DIVIDE,        //除
    LEFTBRACKET,  //左括号
    RIGHTBRACKET,  //右括号
    ERROR,         //报错
    EOF            // Marks the end of tokens.
}

class TokenInformation {
    TokenType type;                    //记录token类型
    String information;               //记录token的具体信息比如一个double值为2.55
    int index;                        // Index of current token

    public TokenInformation(TokenType type, String information, int index) {
        this.type = type;
        this.information = information;
        this.index = index;
    }

    @Override
    public String toString() {
        String result = "";
        result = result + " type:" + type + " information:" + information + " "+"\n";
        return result;
    }
}


public class Lexer {
    int curIndex;      //记录当前正在处理的字符
    int lastIndex;     //记录上一个处理好的字符
    char[] chars;      //要分析的字符串

    public Lexer(int curIndex, int lastIndex, String sentence) {
        this.curIndex = curIndex;
        this.lastIndex = lastIndex;
        String str = sentence.replaceAll(" ", "");
        this.chars = str.toCharArray();
    }

    public boolean readNextChar() {
        if (curIndex < chars.length) {
            curIndex++;
            if (curIndex == chars.length)
                return false;
            return true;
        }
        return false;
    }

    public ArrayList<TokenInformation> parser() throws Exception {
        ArrayList<TokenInformation> tokenInformations = new ArrayList<>();
        while (curIndex < chars.length) {
            char c = chars[curIndex];
            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_' || c == '$') {
                TokenInformation tokenInformation = isIndentifier();
                tokenInformations.add(tokenInformation);
            } else if ('0' <= c && c <= '9') {
                //如果为-，可能返回一个operator
                TokenInformation tokenInformation = isIntegerOrDouble();
                tokenInformations.add(tokenInformation);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')') {
                TokenInformation tokenInformation = isOperator();
                tokenInformations.add(tokenInformation);
            } else {
                throw new Exception("invalid character");
            }
        }
        TokenInformation tokenInformation = new TokenInformation(TokenType.EOF, "END", curIndex+1);
        tokenInformations.add(tokenInformation);
        return tokenInformations;
    }

    public TokenInformation isIntegerOrDouble() throws Exception {
        //readNextChar();   //规定：读取下一个字符的过程在函数中进行而不是在parser中进行
        char c = chars[curIndex];
        boolean isDouble = false;
        TokenInformation tokenInformation = null;

        if ('0' <= c && c <= '9') {
            if (c == '0') {
                if (readNextChar()) {
                    c = chars[curIndex];
                    if (c == '.')
                        curIndex--;
                    else if ('0' <= c && c <= '9') {
                        throw new Exception("invalid expression of numbers");
                    }
                    else {
                        tokenInformation = new TokenInformation(TokenType.INT, "0", curIndex);
                        return tokenInformation;
                    }
                } else {
                    tokenInformation = new TokenInformation(TokenType.INT, "0", curIndex);
                    return tokenInformation;
                }
            }
            if (!readNextChar()) {
                String information = getString(lastIndex + 1, curIndex - 1);
                if (isDouble)
                    tokenInformation = new TokenInformation(TokenType.DOUBLE, information, curIndex);
                else
                    tokenInformation = new TokenInformation(TokenType.INT, information, curIndex);
                return tokenInformation;
            } else
                curIndex--;
            c = chars[curIndex];
            int dotCount = 0;
            while (curIndex < chars.length && (('0' <= c && c <= '9') || c == '.')) {
                if (c == '.' && dotCount > 0) {
                    throw new Exception("more than one dots");   // 错误：两个小数点
                } else if (c == '.') {
                    dotCount++;
                    isDouble = true;
                    readNextChar();
                    if (curIndex < chars.length)
                        c = chars[curIndex];
                } else {
                    readNextChar();
                    if (curIndex < chars.length)
                        c = chars[curIndex];
                }
            }
            if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_') {
                throw new Exception("invalid expression of numbers");
            }
            String information = getString(lastIndex + 1, curIndex - 1);
            if (isDouble)
                tokenInformation = new TokenInformation(TokenType.DOUBLE, information, curIndex);
            else
                tokenInformation = new TokenInformation(TokenType.INT, information, curIndex);

            lastIndex = curIndex - 1;
            return tokenInformation;
        } else
            throw new Exception("unknown error");
    }

    public TokenInformation isIndentifier() throws Exception {
        if (!readNextChar()) {
            String information = getString(lastIndex + 1, curIndex - 1);
            TokenInformation tokenInformation = new TokenInformation(TokenType.IDENTIFIER, information, curIndex);
            return tokenInformation;
        }
        char c = chars[curIndex];
        while (curIndex < chars.length && ('a' <= c && c <= 'z') || ('0' <= c && c <= '9') || ('A' <= c && c <= 'Z') || c == '_' || c == '$') {
            if (!readNextChar()) {
                String information = getString(lastIndex + 1, curIndex - 1);
                TokenInformation tokenInformation = new TokenInformation(TokenType.IDENTIFIER, information, curIndex);
                return tokenInformation;
            }
            c = chars[curIndex];
        }
        String information = getString(lastIndex + 1, curIndex - 1);
        TokenInformation tokenInformation = new TokenInformation(TokenType.IDENTIFIER, information, curIndex);
        lastIndex = curIndex - 1;
        return tokenInformation;
    }

    public String getString(int start, int end) {
        char[] chars_part = new char[end - start + 1];
        for (int i = 0; i <= end - start; i++) {
            chars_part[i] = chars[start + i];
        }
        String s = String.copyValueOf(chars_part);
        return s;
    }

    public TokenInformation isOperator() throws Exception {
        TokenInformation tokenInformation = null;
        char c = chars[curIndex];
        switch (c) {
            case '+':
                tokenInformation = new TokenInformation(TokenType.PLUS, null, curIndex);
                break;
            case '-':
                tokenInformation = new TokenInformation(TokenType.MINUS, null, curIndex);
                break;
            case '*':
                tokenInformation = new TokenInformation(TokenType.MULTIPLE, null, curIndex);
                break;
            case '/':
                tokenInformation = new TokenInformation(TokenType.DIVIDE, null, curIndex);
                break;
            case '(':
                tokenInformation = new TokenInformation(TokenType.LEFTBRACKET, null, curIndex);
                break;
            case ')':
                tokenInformation = new TokenInformation(TokenType.RIGHTBRACKET, null, curIndex);
                break;
        }
        lastIndex = curIndex;
        readNextChar();
        return tokenInformation;
    }
}
