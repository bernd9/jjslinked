package one.xis.processor;

import com.squareup.javapoet.CodeBlock;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class CodeBlockBuilder {
    private final String source;
    private final List<Object> vars = new ArrayList<>();

    CodeBlockBuilder withVar(Object var) {
        vars.add(var);
        return this;
    }

    CodeBlock build() {
        return CodeBlock.builder().add(source, vars.toArray()).build();
    }

}
