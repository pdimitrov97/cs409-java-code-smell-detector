package com.github.pdimitrov97.cs409_java_code_smell_detector;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LongMethodSmell extends VoidVisitorAdapter<Void>
{
	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		for (MethodDeclaration m : c.getMethods())
		{
			MethodStatementsCounter msc = new MethodStatementsCounter();
			msc.visit(m, c);
		}
	}
}
