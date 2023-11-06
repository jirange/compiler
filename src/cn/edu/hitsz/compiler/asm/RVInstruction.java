package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.ir.IRImmediate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 中间表示的指令.
 * <br>
 */
public class RVInstruction {
    public enum RVInstructionKind {ADD, SUB, MUL, ADDI, SUBI, LI, MV}

    //============================== 不同种类 RV IR 的构造函数 ==============================
    public static RVInstruction createAdd(Reg result, Reg lhs, Reg rhs) {
        return new RVInstruction(RVInstructionKind.ADD, result, List.of(lhs, rhs));
    }

    public static RVInstruction createSub(Reg result, Reg lhs, Reg rhs) {
        System.out.println("我收到是："+result+" "+lhs+" "+rhs);
        return new RVInstruction(RVInstructionKind.SUB, result, List.of(lhs, rhs));

    }

    public static RVInstruction createMul(Reg result, Reg lhs, Reg rhs) {
        return new RVInstruction(RVInstructionKind.MUL, result, List.of(lhs, rhs));
    }

    public static RVInstruction createMv(Reg resultReg, Reg fromReg) {
        return new RVInstruction(RVInstructionKind.MV, resultReg, List.of(fromReg));
    }

    public static RVInstruction createRet(Reg returnReg) {
        return new RVInstruction(RVInstructionKind.MV, Reg.a0, List.of(returnReg));
    }

    public static RVInstruction createAddi(Reg result, Reg lhs, IRImmediate rhs) {
        return new RVInstruction(RVInstructionKind.ADDI, result, lhs, Integer.parseInt(rhs.toString()));
    }

    public static RVInstruction createSubi(Reg result, Reg lhs, IRImmediate rhs) {
        return new RVInstruction(RVInstructionKind.SUBI, result, lhs, Integer.parseInt(rhs.toString()));
    }

    public static RVInstruction createLi(Reg result, IRImmediate imm) {
        return new RVInstruction(RVInstructionKind.LI, result, Integer.parseInt(imm.toString()));
    }


    //============================== 不同种类 IR 的参数 getter ==============================


    //============================== 基础设施 ==============================
    @Override
    public String toString() {
        final var kindString = kind.toString();
        final var resultString = resultReg == null ? "" : resultReg.toString();
        final var operandsString = operandsReg.stream().map(Objects::toString).collect(Collectors.joining(", "));
        String immString = imm == null ? "" : ", " + imm.toString();
        return "\t%s %s, %s%s\t".formatted(kindString, resultString, operandsString, immString);
    }

    public List<Reg> getOperands() {
        return Collections.unmodifiableList(operandsReg);
    }

    private RVInstruction(RVInstructionKind kind, Reg result, List<Reg> operands) {
        this.kind = kind;
        this.resultReg = result;
        this.operandsReg = operands;
    }

    private RVInstruction(RVInstructionKind kind, Reg result, Reg lhs, Integer imm) {
        this.kind = kind;
        this.resultReg = result;
        this.operandsReg = List.of(lhs);
        this.imm = imm;
    }
    private RVInstruction(RVInstructionKind kind, Reg result, Integer imm) {
        this.kind = kind;
        this.resultReg = result;
        this.imm = imm;
        this.operandsReg =null;
    }
    private final RVInstructionKind kind;
    private final Reg resultReg;
    private final List<Reg> operandsReg;
    private Integer imm;

//    private void ensureKindMatch(Set<InstructionKind> targetKinds) {
//        final var kind = getKind();
//        if (!targetKinds.contains(kind)) {
//            final var acceptKindsString = targetKinds.stream()
//                .map(InstructionKind::toString)
//                .collect(Collectors.joining(","));
//
//            throw new RuntimeException(
//                "Illegal operand access, except %s, but given %s".formatted(acceptKindsString, kind));
//        }
//    }
}
