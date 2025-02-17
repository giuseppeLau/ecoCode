package fr.greencodeinitiative.php.checks;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.Tree.Kind;
import org.sonar.plugins.php.api.tree.statement.BlockTree;
import org.sonar.plugins.php.api.tree.statement.DoWhileStatementTree;
import org.sonar.plugins.php.api.tree.statement.ExpressionStatementTree;
import org.sonar.plugins.php.api.tree.statement.ForEachStatementTree;
import org.sonar.plugins.php.api.tree.statement.ForStatementTree;
import org.sonar.plugins.php.api.tree.statement.IfStatementTree;
import org.sonar.plugins.php.api.tree.statement.StatementTree;
import org.sonar.plugins.php.api.visitors.PHPSubscriptionCheck;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@Rule(
        key = AvoidSQLRequestInLoopCheck.RULE_KEY,
        name = AvoidSQLRequestInLoopCheck.ERROR_MESSAGE,
        description = AvoidSQLRequestInLoopCheck.ERROR_MESSAGE,
        priority = Priority.MINOR,
        tags = {"sql", "performance", "eco-design", "ecocode"})
@DeprecatedRuleKey(repositoryKey = "gci-php", ruleKey = "S72")
public class AvoidSQLRequestInLoopCheck extends PHPSubscriptionCheck {

    public static final String RULE_KEY = "EC72";
    public static final String ERROR_MESSAGE = "Avoid SQL request in loop";
    private static final Pattern PATTERN = Pattern.compile("(mysql(i::|_)query\\s*\\(.*)|(oci_execute\\(.*)");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Arrays.asList(Kind.FOR_STATEMENT, Kind.FOREACH_STATEMENT, Kind.DO_WHILE_STATEMENT);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Kind.FOR_STATEMENT)) {
            StatementTree stTree = ((ForStatementTree) tree).statements().get(0);
            if (stTree.is(Kind.BLOCK)) {
                visitBlockNode((BlockTree) stTree);
            }
        }

        if (tree.is(Kind.FOREACH_STATEMENT)) {
            StatementTree stTree = ((ForEachStatementTree) tree).statements().get(0);
            if (stTree.is(Kind.BLOCK)) {
                visitBlockNode((BlockTree) stTree);
            }
        }

        if (tree.is(Kind.DO_WHILE_STATEMENT)) {
            StatementTree stTree = ((DoWhileStatementTree) tree).statement();
            if (stTree.is(Kind.BLOCK)) {
                visitBlockNode((BlockTree) stTree);
            }
        }
    }

    private void visitBlockNode(BlockTree block) {
        block.statements().forEach(this::visiteChildNode);
    }

    private void visiteChildNode(Tree tree) {
        if (tree.is(Kind.EXPRESSION_STATEMENT)) {
            ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) tree;
            String expression = expressionStatementTree.expression().toString();
            verifyIfThereIsAError(expression, expressionStatementTree);
        }

        if (tree.is(Kind.IF_STATEMENT)) {
            StatementTree statementTree = ((IfStatementTree) tree).statements().get(0);
            if (statementTree.is(Kind.BLOCK)) {
                visitBlockNode((BlockTree) statementTree);
            } else {
                visiteChildNode(statementTree);
            }
        }
    }

    private void verifyIfThereIsAError(String expression, ExpressionStatementTree expressionStatementTree) {
        if (PATTERN.matcher(expression).find())
            context().newIssue(this, expressionStatementTree, ERROR_MESSAGE);
    }

}
