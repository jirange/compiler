package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.ir.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {
    //寄存器的名称


    Map<IRValue, Integer> lastUse = new HashMap<>();

    // 寄存器的占用信息
    BMap<IRValue,Reg> bMap = new BMap<>();
    List<Instruction> predInstructions = new LinkedList<>();

    List<RVInstruction> RVInstructions = new LinkedList<>();

    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */
    public void loadIR(List<Instruction> originInstructions) {
        // TODO: 读入前端提供的中间代码并生成所需要的信息
//todo        throw new NotImplementedException();
        // 预处理
        for (Instruction originInstruction : originInstructions) {
            // System.out.println(originInstruction);
            InstructionKind kind = originInstruction.getKind();
            if (kind.isUnary()) {
                //对于 UnaryOp(一个操作数的指令)：


                predInstructions.add(originInstruction);
                int index = predInstructions.indexOf(originInstruction);
                if (kind.isReturn()) {
                    // 根据语言规定，当遇到 Ret 指令后直接舍弃后续指令。
                    lastUse.put(originInstruction.getReturnValue(), index);
                    break;
                }
                lastUse.put(originInstruction.getResult(), index);
                if (originInstruction.getFrom().isIRVariable()) {
                    lastUse.put(originInstruction.getFrom(), index);
                }

            } else if (kind.isBinary()) {
                //对于 BinaryOp(两个操作数的指令)：
                IRValue lhs = originInstruction.getLHS();
                IRValue rhs = originInstruction.getRHS();
                IRVariable result = originInstruction.getResult();
                // 将操作两个立即数的 BinaryOp 直接进行求值得到结果，然后替换成 MOV
                if (lhs.isImmediate() && rhs.isImmediate()) {
                    switch (kind) {
                        case ADD -> {
                            IRImmediate value = IRImmediate.of(Integer.parseInt(lhs.toString()) + Integer.parseInt(rhs.toString()));
                            predInstructions.add(Instruction.createMov(result, value));
                        }
                        case MUL -> {
                            IRImmediate value = IRImmediate.of(Integer.parseInt(lhs.toString()) * Integer.parseInt(rhs.toString()));
                            predInstructions.add(Instruction.createMov(result, value));
                        }
                        case SUB -> {
                            IRImmediate value = IRImmediate.of(Integer.parseInt(lhs.toString()) - Integer.parseInt(rhs.toString()));
                            predInstructions.add(Instruction.createMov(result, value));
                        }
                        default -> {
                            System.out.println("我是 " + originInstruction);
                        }
                    }
                    int index = predInstructions.indexOf(originInstruction);
                    lastUse.put(result, index);
                } else if (lhs.isImmediate() || rhs.isImmediate()) {
                    if (kind == InstructionKind.MUL || (kind == InstructionKind.SUB && lhs.isImmediate())) {

                        IRVariable a = IRVariable.temp();
                        if (lhs.isImmediate()) {
                            // 将操作一个立即数的乘法和左立即数减法调整，前插一条 MOV a，imm，
                            predInstructions.add(Instruction.createMov(a, lhs));
                            //用 a 替换原立即数，将指令调整为无立即数指令。
                            predInstructions.add(Instruction.createMul(result, a, rhs));

                        } else {
                            predInstructions.add(Instruction.createMov(a, rhs));
                            predInstructions.add(Instruction.createMul(result, lhs, a));
                        }


                    } else {
                        // 将操作一个立即数的指令 (除了乘法和左立即数减法) 进行调整，使之满足
                        //a := b op imm 的格式
                        // 那就只能是 左右立即数加法 右立即数减法
                        // 只有左立即数才需要变换一下顺序，即只有左立即数加法
                        if (lhs.isImmediate()) {
                            predInstructions.add(Instruction.createAdd(result, rhs, lhs));

                        } else {
                            predInstructions.add(originInstruction);
                        }
                    }
                    // 维护 lastUse
                    int index = predInstructions.indexOf(originInstruction);
                    if (lhs.isIRVariable()) {
                        lastUse.put(lhs, index);
                    }
                    if (rhs.isIRVariable()) {
                        lastUse.put(rhs, index);
                    }
                    lastUse.put(result, index);
                    System.out.println(index);


                } else {
                    // 左右皆无立即数 不用做预处理
                    predInstructions.add(originInstruction);
                    int index = predInstructions.indexOf(originInstruction);
                    lastUse.put(lhs, index);
                    lastUse.put(rhs, index);
                    lastUse.put(result, index);
                }

            }


        }
        System.out.println(lastUse);
    }

    Reg registerSelection() {
        // todo 寄存器选择算法
        for (Reg reg : Reg.values()) {
            if (reg == Reg.a0) {
                continue;
            }
            if (!bMap.containsValue(reg)) {
                //1. 如果有空闲寄存器，选择空闲寄存器；
                System.out.println("选择空闲寄存器"+reg);
                return reg;
            }
        }
        //2. 否则，夺取不再使用的变量所占的寄存器
        // todo 如何知道一个变量是否不再使用呢  lastUse
        for (IRValue irValue : lastUse.keySet()) {
            if (lastUse.get(irValue) == -1) {
                // 这个变量 后续不再使用了
                System.out.println("夺取不再使用的变量"+irValue+"所占的寄存器"+bMap.getByKey(irValue));
                return  bMap.getByKey(irValue);
            }
        }
        System.out.println("ERROR: 寄存器满了，挤不出来了");
        return Reg.t0;
    }
    Reg registerAllocation(IRValue variable){
        System.out.println("正在给 "+variable+"分配寄存器");
        if (bMap.containsKey(variable)){
            return bMap.getByKey(variable);
        }
        Reg reg = registerSelection();
        bMap.replace(variable,reg);
        System.out.println("正在给 "+variable+"分配新的寄存器"+reg);
        return reg;
    }

    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
    public void run() {
        // TODO: 执行寄存器分配与代码生成
//todo        throw new NotImplementedException();
        // 对每个形如（op，result,lhs,rhs）的三地址语句


        for (Instruction predInstruction : predInstructions) {
            System.out.println(predInstruction);


//            //2. 如果左操作数lhs已经在寄存器中，使用当前寄存器，右操作数rhs同理。
//            IRValue lhs = predInstruction.getLHS();
//            if (lhs.isIRVariable() && !bMap.containsKey(lhs)) {
//                //3. 否则，如果左操作数lhs是变量且在内存中，使用寄存器选择算法为它选
//                //择一个寄存器，将内存中的变量值加载到寄存器中，右操作数rhs同理。
//                Reg lhsreg = registerSelection();
//
//                bMap.replace((IRVariable) lhs, lhsreg);
//            }

            //4. 生成汇编指令。
            InstructionKind kind = predInstruction.getKind();
            switch (kind) {
                case MOV -> {
                    //1. 使用寄存器选择算法为result选择寄存器。
                    IRVariable result = predInstruction.getResult();
                    Reg resultReg = registerAllocation(result);

                    // 当 MOV 是变量+值  from是值 -》 li
                    // from 是变量 mv
                    if(predInstruction.getFrom().isIRVariable()){
                        Reg fromReg = registerAllocation(predInstruction.getFrom());
                        RVInstructions.add(RVInstruction.createMv(resultReg,fromReg));
                    }else {
                        RVInstructions.add(RVInstruction.createLi(resultReg,(IRImmediate) predInstruction.getFrom()));
                    }

                }
                case ADD -> {
                    IRVariable result = predInstruction.getResult();
                    Reg resultReg = registerAllocation(result);
                    Reg LHSReg = registerAllocation(predInstruction.getLHS());
                    if (predInstruction.getRHS().isIRVariable()){
                        Reg RHSReg = registerAllocation(predInstruction.getRHS());
                        RVInstructions.add(RVInstruction.createAdd(resultReg,LHSReg,RHSReg));
                    }else {
                        RVInstructions.add(RVInstruction.createAddi(resultReg,LHSReg,(IRImmediate) predInstruction.getRHS()));
                    }
                }
                case SUB -> {
                    IRVariable result = predInstruction.getResult();
                    Reg resultReg = registerAllocation(result);
                    Reg LHSReg = registerAllocation(predInstruction.getLHS());
                    if (predInstruction.getRHS().isIRVariable()){
                        Reg RHSReg = registerAllocation(predInstruction.getRHS());
                        RVInstructions.add(RVInstruction.createSub(resultReg,LHSReg,RHSReg));
                    }else {
                        RVInstructions.add(RVInstruction.createSubi(resultReg,LHSReg,(IRImmediate) predInstruction.getRHS()));
                    }
                }
                case MUL -> {
                    IRVariable result = predInstruction.getResult();
                    Reg resultReg = registerAllocation(result);
                    Reg LHSReg = registerAllocation(predInstruction.getLHS());
                    if (predInstruction.getRHS().isIRVariable()){
                        // 必须只有这种情况 因为没有MULI
                        Reg RHSReg = registerAllocation(predInstruction.getRHS());
                        RVInstructions.add(RVInstruction.createMul(resultReg,LHSReg,RHSReg));
                    }
                }
                case RET -> {
                    if(predInstruction.getReturnValue().isIRVariable()){
                        RVInstructions.add(RVInstruction.createRet(registerAllocation(predInstruction.getReturnValue())));
                    }else{
                        RVInstructions.add(RVInstruction.createLi(Reg.a0,(IRImmediate) predInstruction.getReturnValue()));
                    }

                }
            }
        }
    }


    /**
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // TODO: 输出汇编代码到文件
//todo        throw new NotImplementedException();
        System.out.println(RVInstructions);
    }
}

