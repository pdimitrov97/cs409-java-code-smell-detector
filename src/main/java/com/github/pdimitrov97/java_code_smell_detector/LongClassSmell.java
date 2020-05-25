package com.github.pdimitrov97.java_code_smell_detector;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LongClassSmell extends VoidVisitorAdapter<Void>
{
	private static final int MAX_CLASS_STATEMENTS = 100;

	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		int classStatementsCount = 0;

		MethodStatementsCounter msc = new MethodStatementsCounter();

		for (ConstructorDeclaration constructor : c.getConstructors())
		{
			for (Statement temp : constructor.getBody().getStatements())
				classStatementsCount += msc.expandStmt(temp);
		}

		for (MethodDeclaration m : c.getMethods())
		{
			if (!m.isAbstract())
			{
				for (Statement temp : m.getBody().get().getStatements())
					classStatementsCount += msc.expandStmt(temp);
			}
		}

		if (classStatementsCount > MAX_CLASS_STATEMENTS)
			System.out.println("Class \"" + c.getName() + "\" is too long and it has " + classStatementsCount + " statements!\n");
	}
}
