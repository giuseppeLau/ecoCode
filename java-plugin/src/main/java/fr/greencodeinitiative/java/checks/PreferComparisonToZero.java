package fr.greencodeinitiative.java.checks;

import java.util.List;
import java.util.Arrays;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.BinaryExpressionTree;

@Rule(
		key = "ECPCTZ",
		name = "Developement",
		description = PreferComparisonToZero.MESSAGERULE,
		priority = Priority.MINOR,
		tags = {"performance", "eco-design", "ecocode"})
@DeprecatedRuleKey(repositoryKey = "greencodeinitiative-java", ruleKey = "PCTZ")
public class PreferComparisonToZero extends IssuableSubscriptionVisitor {
	
	protected static final String MESSAGERULE = "In Loops Prefer Comparison To Zero";
	
	private static boolean isZero(ExpressionTree tree) {
		return tree.is(Kind.INT_LITERAL) && "0".equals(((LiteralTree) tree).value());
	}
	
	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Arrays.asList(Tree.Kind.FOR_STATEMENT, Kind.WHILE_STATEMENT);
	}
	  
	@Override
	public void visitNode(Tree tree) {
		
		if(tree.is(Kind.FOR_STATEMENT)) {
			ForStatementTree forStatementTree = (ForStatementTree) tree;
			ExpressionTree condition = forStatementTree.condition();
			if (condition != null) {
				BinaryExpressionTree binaryExp = (BinaryExpressionTree) condition;
				if(binaryExp.rightOperand().symbolType().isNumerical()) {
			    		
					if(!isZero(binaryExp.rightOperand())) {
						context.reportIssue(this, forStatementTree.forKeyword(), MESSAGERULE);
					}	
				}
			} 
		} else if (tree.is(Kind.WHILE_STATEMENT)) {
			WhileStatementTree whileStatementTree = (WhileStatementTree) tree;
			ExpressionTree condition = whileStatementTree.condition();
			BinaryExpressionTree binaryExp = (BinaryExpressionTree) condition;
			if(binaryExp.rightOperand().symbolType().isNumerical()) {			    		
				if(!isZero(binaryExp.rightOperand())) {
					context.reportIssue(this, whileStatementTree.whileKeyword(), MESSAGERULE);
				}	
			} 
		} else {
            throw new UnsupportedOperationException("Kind of statement NOT supported - real kind : " + tree.kind().getAssociatedInterface());
        }
	}	 
}