package cn.edu.hitsz.compiler.ir;

import cn.edu.hitsz.compiler.parser.table.Term;

/**
 * @author leng
 */
public class IRElement {
    private Term term;
    private IRValue irValue;

    public IRElement(Term term, IRValue irValue) {
        this.term = term;
        this.irValue = irValue;
    }

    public IRElement(Term term) {
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
        return "IRElement{" +
                "term=" + term +
                ", irValue=" + irValue +
                '}';
    }
}
