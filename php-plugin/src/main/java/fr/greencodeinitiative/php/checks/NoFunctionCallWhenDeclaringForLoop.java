package fr.greencodeinitiative.php.checks;

import java.util.Collections;
import java.util.List;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.SeparatedList;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.expression.BinaryExpressionTree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.statement.ForStatementTree;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(
        key = NoFunctionCallWhenDeclaringForLoop.RULE_KEY,
        name = NoFunctionCallWhenDeclaringForLoop.ERROR_MESSAGE,
        description = NoFunctionCallWhenDeclaringForLoop.ERROR_MESSAGE,
        priority = Priority.MINOR,
        tags = {"performance", "eco-design", "ecocode"})
@DeprecatedRuleKey(repositoryKey = "gci-php", ruleKey = "S69")
public class NoFunctionCallWhenDeclaringForLoop extends PHPSubscriptionCheck {

    public static final String RULE_KEY = "EC69";
    public static final String ERROR_MESSAGE = "Do not call a function in for-type loop declaration";

    @Override
    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Kind.FOR_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        ForStatementTree method = (ForStatementTree) tree;
        checkExpressionsTree(method.update());
        checkExpressionsTree(method.condition());
    }

    public void checkExpressionsTree(SeparatedList<ExpressionTree> treeSeparatedList) {
        treeSeparatedList.forEach(this::checkBothSideExpression);
    }

    public void checkBothSideExpression(ExpressionTree expressionTree) {
        if (expressionTree.getKind().getAssociatedInterface() == BinaryExpressionTree.class) {
            BinaryExpressionTree binaryExpressionTree = (BinaryExpressionTree) expressionTree;
            isFunctionCall(binaryExpressionTree.leftOperand());
            isFunctionCall(binaryExpressionTree.rightOperand());
        } else
            isFunctionCall(expressionTree);
    }

    public void isFunctionCall(ExpressionTree expressionTree) {
        if (expressionTree.getKind() == Tree.Kind.FUNCTION_CALL)
            context().newIssue(this, expressionTree, ERROR_MESSAGE);
    }
}
