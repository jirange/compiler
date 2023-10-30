package cn.edu.hitsz.compiler.ir;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Term;

/**
 * @author leng
 */
public class IrSymbol {
    private Term term;
    private IRValue irValue;

    public IrSymbol(Term term, IRValue irValue) {
        this.term = term;
        this.irValue = irValue;
    }

    public IrSymbol(Term term) {
        this.term = term;
    }

    public Term getTerm() {
        return term;
    }

    public IRValue getIrValue() {
        return irValue;
    }

    @Override
    public String toString() {
        return "IrSymbol{" +
                "term=" + term +
                ", irValue=" + irValue +
                '}';
    }
}
