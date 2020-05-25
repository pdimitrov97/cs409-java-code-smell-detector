package com.github.pdimitrov97.cs409_java_code_smell_detector;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DataClassSmell extends VoidVisitorAdapter<Void>
{
	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		if (isDataClass(c))
			System.out.println(c.getName() + " is a DataClass!\n");

		LocalDataClass ldc = new LocalDataClass();

		for (MethodDeclaration m : c.getMethods())
			ldc.visit(m, arg);

		for (BodyDeclaration member : c.getMembers())
		{
			if (member.isClassOrInterfaceDeclaration())
			{
				if (isDataClass(member.asClassOrInterfaceDeclaration()))
					System.out.println(member.asClassOrInterfaceDeclaration().getName() + " is a DataClass!\n");

				super.visit(member.asClassOrInterfaceDeclaration(), arg);
			}
		}
	}

	public boolean isDataClass(ClassOrInterfaceDeclaration c)
	{
		if (c.isInterface())
			return false;

		MethodStatementsCounter msc = new MethodStatementsCounter();
		int declarationCount = 0;
		int constructorAssignmentCount = 0;
		int allStmtCount = 0;
		int returnStmtCount = 0;
		int assignmentCount = 0;

		for (FieldDeclaration f : c.getFields())
		{
			for (VariableDeclarator v : f.getVariables())
				declarationCount++;
		}

		for (ConstructorDeclaration constructor : c.getConstructors())
		{
			for (Statement temp : constructor.getBody().getStatements())
			{
				if (temp.isExpressionStmt())
					constructorAssignmentCount++;
			}
		}

		for (MethodDeclaration m : c.getMethods())
		{
			if (m.getName().toString().equals("equals") || m.getName().toString().equals("toString") || m.getName().toString().equals("compareTo")
					|| m.getName().toString().equals("hashCode"))
				continue;

			if (!m.isAbstract())
			{
				for (Statement temp : m.getBody().get().getStatements())
				{
					allStmtCount += msc.expandStmt(temp);

					if (temp.isReturnStmt())
						returnStmtCount++;

					if (temp.isExpressionStmt())
					{
						if (temp.asExpressionStmt().getExpression().isAssignExpr())
							assignmentCount++;
					}

				}
			}
		}

		if (((constructorAssignmentCount == 0) || (declarationCount == constructorAssignmentCount)) && (allStmtCount == (returnStmtCount + assignmentCount)))
			return true;

		return false;
	}

	private static class LocalDataClass extends VoidVisitorAdapter<Void>
	{
		@Override
		public void visit(LocalClassDeclarationStmt c, Void arg)
		{
			DataClassSmell dcs = new DataClassSmell();
			dcs.visit(c.getClassDeclaration(), arg);
			super.visit(c, arg);
		}
	}
}
