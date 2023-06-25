package fr.greencodeinitiative.java.checks;

import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

class PreferComparisonToZeroCheckTest {
	
	@Test
    void test() {
        CheckVerifier.newVerifier()
                .onFile("src/test/files/PreferComparisonToZeroCheck.java")
                .withCheck(new PreferComparisonToZero())
                .verifyNoIssues();
    }
}
