package one.xis.sql.processor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
class RepositoryImplModel {
    private final EntityModel entityModel;

    private List<CrossTableFieldHandlerModel> getCrossTableFieldHandlers() {
        return entityModel.getCrossTableFields().stream()
                .map(CrossTableFieldHandlerModel::new)
                .collect(Collectors.toList());
    }

}
