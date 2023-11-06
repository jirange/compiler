package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Term;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author leng
 */
// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {

    /* 语义分析栈的数据结构*/
    private Stack<Token> tokenStack = new Stack<>();//符号栈 token+null
    Map<Term,SourceCodeType> typeMap = new HashMap<>();
    SymbolTable symbolTable;

    @Override
    public void whenAccept(Status currentStatus) {
        // : 该过程在遇到 Accept 时要采取的代码动作
//        throw new NotImplementedException();
//        System.out.println("SemanticAnalyzer Accept");
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        // todo 更新符号表标识符的type属性
        // todo 根据production的index来判断当前是哪一条产生式，并在whenReduce方法中编写该产生式的具体翻译动作

        // D -> int啥也不干  S -> D id  symbolTable.get(tokenStack.pop().getText()).setType(SourceCodeType.Int);
        switch (production.index()) {
            case 1, 2, 3, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> {     //P->S_list
                // 普遍是：弹出相应长度的token串，压入null
                for (int i = 0; i < production.body().size(); i++) {
                    tokenStack.pop();
                }
                tokenStack.push(null);
            }
            case 4 -> {     //S -> D id;    {p = lookup(id.name); if p != nil then enter(id.name, D.type) else error}
            //lookup 函数查找符号表是否存在 id 这个标识符，如果有更新符号表里 id 这个标识符的 type 属性，id 的 type 属性来自于 D 的 type 属性
                Token peek = tokenStack.peek();
                symbolTable.get(peek.getText()).setType(typeMap.get(production.body().get(0)));
                tokenStack.pop();
                tokenStack.pop();
                tokenStack.push(null);

            }
            case 5 -> {     //D -> int;   {D.type = int;}   D -> int啥也不干
                //D 的 type 属性是 int；
                typeMap.put(production.head(),SourceCodeType.Int);
                //TokenKind 同时还作为 Term 的子类，代表文法中的非终结符详。见实验二的指导
            }

            default -> {
                throw new RuntimeException("unknown production index");
            }
        }


//todo        throw new NotImplementedException();
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        // todo 将token存储到语义分析栈中
        tokenStack.push(currentToken);
        //typeMap.put(currentToken,null);
//todo        throw new NotImplementedException();
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        symbolTable = table;
//todo        throw new NotImplementedException();
    }
}

