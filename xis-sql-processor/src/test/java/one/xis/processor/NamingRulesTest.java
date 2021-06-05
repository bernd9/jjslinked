package one.xis.processor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NamingRulesTest {

    @Test
    void toSqlName() {
        assertThat(NamingRules.toSqlName("SomethingInCamelCase_123")).isEqualTo("something_in_camel_case_123");
    }

    @Test
    void underscoresToCamelCase() {
        assertThat(NamingRules.underscoresToCamelCase("something_in_camel_case_123")).isEqualTo("SomethingInCamelCase123");
    }

}