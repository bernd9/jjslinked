package com.jjslinked.ast;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ClientIdParamNode {
    private String name;
    private String type;
}
