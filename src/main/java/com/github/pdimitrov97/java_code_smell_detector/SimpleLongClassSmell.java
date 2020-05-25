package com.github.pdimitrov97.java_code_smell_detector;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SimpleLongClassSmell extends VoidVisitorAdapter<Void>
{
	private static final int MAX_CLASS_STATEMENTS = 100;

	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		int start = c.getBegin().get().line;
		int end = c.getEnd().get().line;
		int lines = end - start + 1;

		if (lines > MAX_CLASS_STATEMENTS)
			System.out.println("Class \"" + c.getName() + "\" is too long and has " + lines + " lines of code!\n");
	}
}