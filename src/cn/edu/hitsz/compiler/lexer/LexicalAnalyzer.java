package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.symtab.SymbolTableEntry;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;
/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private List<Integer> tokenKindStrList = new ArrayList<>();
    private List<String> tokenStrList = new ArrayList<>();
    private Map<Integer, String> tokenKindStrMap = new HashMap<>();
    private String codeStr = "";
    private final int INTCONST_MID = 22;
    private final int INTCONST_FIN = 52;
    private final int ID_MID = 21;
    private final int ID_FIN = 51;

    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                codeStr = codeStr + " " + line.trim();
                //todo trim（）有必要吗
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        throw new NotImplementedException();
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        Map<Character, Integer> dMap = new HashMap<>();
        dMap.put('=',3);
        dMap.put(',',4);
        dMap.put(';',5);
        dMap.put('+',6);
        dMap.put('-',7);
        dMap.put('*',8);
        dMap.put('/',9);
        dMap.put('(',10);
        dMap.put(')',11);

        // TODO: 自动机实现的词法分析过程
        int status = 0;
        Integer bbb;

        boolean flag = false;
        String values = "";
        char[] chars = codeStr.toCharArray();
        for (char aChar : chars) {
            bbb = dMap.get(aChar);

            // 状态机 DFA

            flag = false; //状态机运行/终止
            switch (status) {
                case INTCONST_MID:
                    if (Character.isDigit(aChar)) {
                        status = INTCONST_MID;
                    } else {
                        status = INTCONST_FIN;
                        flag = true;
                    }
                    break;
                case ID_MID:
                    if (Character.isDigit(aChar)) {
                        status = ID_MID;
                    } else if (Character.isLetter(aChar)) {
                        status = ID_MID;
                    } else {
                        status = ID_FIN;
                        flag = true;
                    }
                    break;
                case 0:
                    if (Character.isDigit(aChar)) {
                        status = INTCONST_MID;
                    } else if (Character.isLetter(aChar)) {
                        status = ID_MID;
                    } else {
                        status = 0;
                    }
                    break;
                default:
                    System.out.println("status and aChar" + status + aChar);
            }
            if (flag) {
                tokenKindStrList.add(status);
                tokenStrList.add(status+"@"+values);
//                tokenStrList.add(values);
                int i = tokenKindStrList.size()-1;
                tokenKindStrMap.put(i, values);
                //把achar之前的处理
                if (bbb != null) {
                    //把achar之后的处理
                    tokenKindStrList.add(bbb);
                    tokenStrList.add(Character.toString(aChar));
                }
                status = 0;
                values = "";
            } else if (status == ID_MID) {
                values = values + aChar;

            } else if (status == INTCONST_MID) {
                if (values.length()>0){
                    values = String.valueOf(Integer.parseInt(values) + Integer.parseInt(String.valueOf(aChar)));
                }else {
                    values = String.valueOf(aChar);
                }

            } else if (bbb != null) {
                tokenKindStrList.add(bbb);
                tokenStrList.add(Character.toString(aChar));
                status = 0;
                values = "";
            }

        }
//        throw new NotImplementedException();
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        List<Token> tokenList = new ArrayList<>();

        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可

        System.out.println(tokenKindStrList);
        System.out.println(tokenStrList);
        for (String s : tokenStrList) {
            if (s.equals(";")){
                tokenList.add(Token.simple("Semicolon"));
            } else if ("52".equals(s.split("@")[0])) {
                tokenList.add(Token.normal("IntConst", s.split("@")[1]));
            } else if ("51".equals(s.split("@")[0])) {
                String ss =s.split("@")[1];
                if (ss.equals("int") || ss.equals("return"))
                {
                    tokenList.add(Token.simple(ss));

                } else {
                    if (!symbolTable.has(ss)){
                        symbolTable.add(ss);
                    }
                    tokenList.add(Token.normal("id" , ss));
                }
            }
            else {
                tokenList.add(Token.simple(s));

            }

        }

        /*for (int i = 0; i < tokenKindStrList.size(); i++) {
            Integer integer = tokenKindStrList.get(i);
            if (integer == 51) {
                //如果是变量 判断死不是int/return/
                String value = tokenKindStrMap.get(i);
                if ("int".equals(value)) {
                    integer = 1;
                    tokenList.add(Token.simple("int"));

                } else if ("return".equals(value)) {
                    integer = 2;
                    tokenList.add(Token.simple("return"));
                } else {
                    if (!symbolTable.has(value)){
                        symbolTable.add(value);
                    }
                    tokenList.add(Token.normal("id", value));
                }
            } else if (integer == 5) {
                //如果是分号，则变为semicolon
                tokenList.add(Token.simple("Semicolon"));
            } else if (integer == 52) {
                //整数
                String value = tokenKindStrMap.get(i);
                tokenList.add(Token.normal("IntConst", value));
            } else {
                String str = switch (integer) {
                    case 3 -> "=";
                    case 4 -> ",";
                    case 6 -> "+";
                    case 7 -> "-";
                    case 8 -> "*";
                    case 9 -> "/";
                    case 10 -> "(";
                    case 11 -> ")";
                    default -> "";
                };

                if (str.length() > 0) {
                    tokenList.add(Token.simple(str));
                }
            }
        }*/
        //在你处理完所有输入之后, 请生成一个作为 EOF 的词法单元
        tokenList.add(Token.eof());
        return tokenList;
//        throw new NotImplementedException();
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
                path,
                StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
