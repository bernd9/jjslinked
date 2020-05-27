package ast;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParamNode {
    private String name;
    private String type;
}
