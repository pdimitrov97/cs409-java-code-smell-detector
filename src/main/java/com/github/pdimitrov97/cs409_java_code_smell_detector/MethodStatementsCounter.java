package com.github.pdimitrov97.cs409_java_code_smell_detector;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodStatementsCounter extends VoidVisitorAdapter<ClassOrInterfaceDeclaration>
{
	private static final int MAX_METHOD_STATEMENTS = 20;

	@Override
	public void visit(MethodDeclaration m, ClassOrInterfaceDeclaration arg)
	{
		if (!m.getBody().isPresent())
			return;

		int statementsCount = 0;
		List<Statement> statements = m.getBody().get().getStatements();

		for (Statement temp : statements)
			statementsCount += expandStmt(temp);

		if (statementsCount > MAX_METHOD_STATEMENTS)
			System.out.println("Method \"" + m.getSignature() + "\" in class \"" + arg.getNameAsString() + "\" has " + statementsCount + " statements!\n");
	}

	public int expandStmt(Statement s)
	{
		if (s.isBlockStmt())
		{
			int count = 0;

			for (Statement temp : s.asBlockStmt().getStatements())
				count += expandStmt(temp);

			return count;
		}

		if (s.isIfStmt())
		{
			if (s.asIfStmt().hasElseBranch())
				return 1 + expandStmt(s.asIfStmt().getThenStmt()) + expandStmt(s.asIfStmt().getElseStmt().get());
			else
				return 1 + expandStmt(s.asIfStmt().getThenStmt());
		}

		if (s.isForStmt())
			return 1 + expandStmt(s.asForStmt().getBody());

		if (s.isForEachStmt())
			return 1 + expandStmt(s.asForEachStmt().getBody());

		if (s.isWhileStmt())
			return 1 + expandStmt(s.asWhileStmt().getBody());

		if (s.isDoStmt())
			return 1 + expandStmt(s.asDoStmt().getBody());

		if (s.isTryStmt())
		{
			int count = 1;

			for (Statement temp : s.asTryStmt().getTryBlock().getStatements())
				count += expandStmt(temp);

			for (CatchClause cc : s.asTryStmt().getCatchClauses())
			{
				for (Statement temp : cc.getBody().getStatements())
					count += expandStmt(temp);
			}

			if (s.asTryStmt().getFinallyBlock().isPresent())
			{
				for (Statement temp : s.asTryStmt().getFinallyBlock().get().getStatements())
					count += expandStmt(temp);
			}

			return count;
		}

		if (s.isSynchronizedStmt())
		{
			int count = 1;

			for (Statement temp : s.asSynchronizedStmt().getBody().getStatements())
				count += expandStmt(temp);

			return count;
		}

		if (s.isSwitchStmt())
		{
			int count = 1;

			for (SwitchEntry se : s.asSwitchStmt().getEntries())
			{
				for (Statement temp : se.getStatements())
					count += expandStmt(temp);
			}

			return count;
		}

		if (s.isLabeledStmt())
			return 1 + expandStmt(s.asLabeledStmt().getStatement());

		return 1;
	}
}