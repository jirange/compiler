package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.*;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.parser.table.Term;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.*;

// TODO: 实验三: 实现 IR 生成

/**
 * IRGenerator生成中间代码，收集value属性
 */
public class IRGenerator implements ActionObserver {


    /* 语义分析栈的数据结构*/
    private Stack<IRValue> irStack = new Stack<>();
    private Stack<Token> tokenStack = new Stack<>();
    private Stack<IrSymbol> irSymbols = new Stack<>();
    ArrayList<Instruction> Instructions = new ArrayList<>();
    Map<Term, IRValue> map = new HashMap<>();


    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO  把 token 存储在栈
        tokenStack.push(currentToken);


        if ("id".equals(currentToken.getKindId())) {
            irSymbols.push(new IrSymbol(currentToken.getKind(),IRVariable.named(currentToken.getText())));
            irStack.push(IRVariable.named(currentToken.getText()));
        } else if ("IntConst".equals(currentToken.getKindId())) {
            irStack.push(IRImmediate.of(Integer.parseInt(currentToken.getText())));
            irSymbols.push(new IrSymbol(currentToken.getKind(),IRImmediate.of(Integer.parseInt(currentToken.getText()))));
        }else {
            irSymbols.push(new IrSymbol(currentToken.getKind()));
        }

//todo        throw new NotImplementedException();

    }
//
//    @Override
//    public void whenReduce(Status currentStatus, Production production) {
//        // TODO 生成中间代码
////todo        throw new NotImplementedException();
//        /*再根据 production 的 index 来判断当前是哪条产生式，whenReduce 方法中编写该产生式的具体翻译动作。*/
//
//        switch (production.index()) {
//            case 6 -> {     // S -> id = E   {gencode(id.val = E.val);}
//                //生成一条赋值语句的三地址指令
////                Token pop1 = tokenStack.pop();
////                tokenStack.pop();
////                Token pop3 = tokenStack.pop();
//                IRValue irValue1 = irStack.pop();
//                IRValue irValue2 = irStack.pop();
//                //IL.add(Instruction.createMov((IRVariable) map.get(pop3),map.get(pop1)));
//                Instructions.add(Instruction.createMov((IRVariable)irValue2,irValue1));
//            }
//            case 7 -> {     // S -> return E;
////                Token pop = tokenStack.pop();
////                tokenStack.pop();
//                IRValue irValue1 = irStack.pop();
//
////                IL.add(Instruction.createRet(map.get(pop)));
//                Instructions.add(Instruction.createRet(irValue1));
//            }
//            case 8 -> {     // E -> E + A;
////                Token pop1 = tokenStack.pop();
////                tokenStack.pop();
////                Token pop3 = tokenStack.pop();
////                IRVariable temp = IRVariable.temp();
////                IL.add(Instruction.createAdd(temp,map.get(pop3),map.get(pop1)));
//
//                IRValue irValue1 = irStack.pop();
//                IRValue irValue2 = irStack.pop();
//                IRVariable temp = IRVariable.temp();
//                irStack.push(temp);
//                Instructions.add(Instruction.createAdd(temp,irValue2,irValue1));
//
//            }
//            case 9 -> {     // E -> E - A;
////                Token pop1 = tokenStack.pop();
////                tokenStack.pop();
////                Token pop3 = tokenStack.pop();
////                IRVariable temp = IRVariable.temp();
////                IL.add(Instruction.createSub(temp,map.get(pop3),map.get(pop1)));
//
//                IRValue irValue1 = irStack.pop();
//                IRValue irValue2 = irStack.pop();
//                IRVariable temp = IRVariable.temp();
//                irStack.push(temp);
//
//                Instructions.add(Instruction.createSub(temp,irValue2,irValue1));
//            }
//            case 11 -> {     // A -> A * B;
////                Token pop1 = tokenStack.pop();
////                tokenStack.pop();
////                Token pop3 = tokenStack.pop();
////                IRVariable temp = IRVariable.temp();
////                IL.add(Instruction.createMul(temp,map.get(pop3),map.get(pop1)));
//                IRValue irValue1 = irStack.pop();
//                IRValue irValue2 = irStack.pop();
//                IRVariable temp = IRVariable.temp();
//                irStack.push(temp);
//
//                Instructions.add(Instruction.createMul(temp,irValue2,irValue1));
//            }
//            case 1, 10, 12, 14, 15,2, 3, 4, 5, 13 -> {     //B -> IntConst {B.val = IntConst.lexval;}
//                //B 的 val 来自于 IntConst 这个常量的词法值。
////                for (int i = 0; i < production.body().size(); i++) {
////                    tokenStack.pop();
////                }
//            }
//            default -> {
//                throw new RuntimeException("unknown production index");
//            }
//        }
//
//    }


    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO 生成中间代码
//todo        throw new NotImplementedException();
        /*再根据 production 的 index 来判断当前是哪条产生式，whenReduce 方法中编写该产生式的具体翻译动作。*/
        IrSymbol pop3;
        IrSymbol pop1;
        IRVariable temp;

        switch (production.index()) {// S -> id = E   {gencode(id.val = E.val);}
//生成一条赋值语句的三地址指令
            case 6:
                pop1 = irSymbols.pop();
                irSymbols.pop();
                pop3 = irSymbols.pop();
                irSymbols.push(new IrSymbol(production.head()));
                Instructions.add(Instruction.createMov((IRVariable) pop3.getIrValue(), pop1.getIrValue()));
                break;
// S -> return E;
//tokenStack.pop();
            case 7:
                pop1 = irSymbols.pop();
                Instructions.add(Instruction.createRet(pop1.getIrValue()));
                break;
// E -> E + A;
            case 8:
                pop1 = irSymbols.pop();
                irSymbols.pop();
                pop3 = irSymbols.pop();
                temp = IRVariable.temp();
                irSymbols.push(new IrSymbol(production.head(),temp));
                Instructions.add(Instruction.createAdd(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;
// E -> E - A;
            case 9:
                pop1 = irSymbols.pop();
                irSymbols.pop();
                pop3 = irSymbols.pop();
                temp = IRVariable.temp();
                irSymbols.push(new IrSymbol(production.head(),temp));
                Instructions.add(Instruction.createSub(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;
// A -> A * B;
            case 11:
                pop1 = irSymbols.pop();
                irSymbols.pop();
                pop3 = irSymbols.pop();
                temp = IRVariable.temp();
                irSymbols.push(new IrSymbol(production.head(),temp));
                Instructions.add(Instruction.createMul(temp,pop3.getIrValue(), pop1.getIrValue()));
                break;

            case 13:   //B->(E)
                irSymbols.pop();
                pop1 = irSymbols.pop();
                irSymbols.pop();
                irSymbols.push(new IrSymbol(production.head(), pop1.getIrValue()));
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
            case 4:
            case 5:
                break;
            default:
                throw new RuntimeException("unknown production index");
        }

    }

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
//todo        throw new NotImplementedException();
        System.out.println("IRGenerator Accept\n");
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
//todo        throw new NotImplementedException();
    }

    public List<Instruction> getIR() {
        // TODO
//todo        throw new NotImplementedException();
        //return new ArrayList<Instruction>();
        return Instructions;
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}

