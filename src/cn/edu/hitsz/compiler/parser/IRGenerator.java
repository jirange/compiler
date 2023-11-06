package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.*;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.*;

// TODO: 实验三: 实现 IR 生成

/**
 * IRGenerator生成中间代码，收集value属性
 */
public class IRGenerator implements ActionObserver {


    /* 语义分析栈的数据结构*/

    private Stack<IRElement> irStack = new Stack<>();
    ArrayList<Instruction> Instructions = new ArrayList<>();


    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO  把 token 存储在栈

        if ("id".equals(currentToken.getKindId())) {
            irStack.push(new IRElement(currentToken.getKind(),IRVariable.named(currentToken.getText())));
        } else if ("IntConst".equals(currentToken.getKindId())) {
            irStack.push(new IRElement(currentToken.getKind(),IRImmediate.of(Integer.parseInt(currentToken.getText()))));
        }else {
            irStack.push(new IRElement(currentToken.getKind()));
        }

//        throw new NotImplementedException();

    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO 生成中间代码
//        throw new NotImplementedException();
        /*再根据 production 的 index 来判断当前是哪条产生式，whenReduce 方法中编写该产生式的具体翻译动作。*/
        IRElement pop3;
        IRElement pop1;
        IRVariable temp;

        switch (production.index()) {
            //生成一条赋值语句的三地址指令
            // S -> id = E   {gencode(id.val = E.val);}
            case 6:
                pop1 = irStack.pop();
                irStack.pop();
                pop3 = irStack.pop();
                irStack.push(new IRElement(production.head()));
                Instructions.add(Instruction.createMov((IRVariable) pop3.getIrValue(), pop1.getIrValue()));
                break;
            // S -> return E;
            case 7:
                pop1 = irStack.pop();
                Instructions.add(Instruction.createRet(pop1.getIrValue()));
                break;
            // E -> E + A;
            case 8:
                pop1 = irStack.pop();
                irStack.pop();
                pop3 = irStack.pop();
                temp = IRVariable.temp();
                irStack.push(new IRElement(production.head(),temp));
                Instructions.add(Instruction.createAdd(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;
            // E -> E - A;
            case 9:
                pop1 = irStack.pop();
                irStack.pop();
                pop3 = irStack.pop();
                temp = IRVariable.temp();
                irStack.push(new IRElement(production.head(),temp));
                Instructions.add(Instruction.createSub(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;
            // A -> A * B;
            case 11:
                pop1 = irStack.pop();
                irStack.pop();
                pop3 = irStack.pop();
                temp = IRVariable.temp();
                irStack.push(new IRElement(production.head(),temp));
                Instructions.add(Instruction.createMul(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;
            //B->(E)
            case 13:
                irStack.pop();
                pop1 = irStack.pop();
                irStack.pop();
                irStack.push(new IRElement(production.head(), pop1.getIrValue()));
                break;
            //S -> D id 但是好像有没有都无所谓
            case 4:
                irStack.pop();
                irStack.pop();
                irStack.push(new IRElement(production.head()));
                break;
            case 15:
            case 14:
//                System.out.println(irSymbols.peek());
//                break;
            case 1:
            case 10:
            case 12:
            case 2:
            case 3:
            case 5:
                break;
            default:
                throw new RuntimeException("unknown production index");
        }

    }

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
//        throw new NotImplementedException();
        System.out.println("IRGenerator Accept\n");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
//todo        throw new NotImplementedException();
    }

    public List<Instruction> getIR() {
        // TODO
//        throw new NotImplementedException();
        //return new ArrayList<Instruction>();
        return Instructions;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

