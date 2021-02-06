package one.xis.sql.api;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
class CrossTableMetaData {
    private final String tableName;

    static Map<String, CrossTableMetaData> metaDataMap;

  
}
