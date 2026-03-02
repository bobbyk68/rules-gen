package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for NormalisationConfig.
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
class NormalisationConfigTest {

    @Test
    void shouldReturnTrueForConfiguredNormalisedAttribute() {
        NormalisationConfig config = new NormalisationConfig(Map.of("countryRegionRoleType", true));
        assertThat(config.shouldNormalise("countryRegionRoleType")).isTrue();
    }

    @Test
    void shouldReturnFalseForConfiguredNonNormalisedAttribute() {
        NormalisationConfig config = new NormalisationConfig(Map.of("partyRoleType", false));
        assertThat(config.shouldNormalise("partyRoleType")).isFalse();
    }

    @Test
    void shouldDefaultToFalseForUnknownAttribute() {
        NormalisationConfig config = new NormalisationConfig(Map.of());
        assertThat(config.shouldNormalise("unknownAttribute")).isFalse();
    }

    @Test
    void shouldReturnImmutableCopyOfMap() {
        NormalisationConfig config = new NormalisationConfig(Map.of("partyRoleType", false));
        assertThat(config.getMap()).containsEntry("partyRoleType", false);
    }
}
