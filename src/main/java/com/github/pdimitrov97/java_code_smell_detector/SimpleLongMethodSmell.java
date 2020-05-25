package com.github.pdimitrov97.java_code_smell_detector;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SimpleLongMethodSmell extends VoidVisitorAdapter<Void>
{
	private static final int MAX_METHOD_STATEMENTS = 20;

	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		for (MethodDeclaration m : c.getMethods())
		{
			SimpleMethodStatementsCounter msc = new SimpleMethodStatementsCounter();
			msc.visit(m, c);
		}
	}

	private static class SimpleMethodStatementsCounter extends VoidVisitorAdapter<ClassOrInterfaceDeclaration>
	{
		@Override
		public void visit(MethodDeclaration m, ClassOrInterfaceDeclaration arg)
		{
			int start = m.getBegin().get().line;
			int end = m.getEnd().get().line;
			int lines = end - start + 1;

			if (lines > MAX_METHOD_STATEMENTS)
				System.out.println("Method \"" + m.getSignature() + "\" in class \"" + arg.getNameAsString() + "\" has " + lines + " lines of code!\n");
		}
	}
}