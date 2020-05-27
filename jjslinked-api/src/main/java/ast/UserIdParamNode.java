package ast;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserIdParamNode {
    private String name;
    private String type;
}
