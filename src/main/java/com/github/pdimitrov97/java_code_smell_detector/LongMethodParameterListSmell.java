package com.github.pdimitrov97.java_code_smell_detector;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LongMethodParameterListSmell extends VoidVisitorAdapter<Void>
{
	private static final int MAX_METHOD_PARAMETERS_NUMBER = 5;

	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		for (MethodDeclaration m : c.getMethods())
		{
			MethodParameterListCounter msc = new MethodParameterListCounter();
			msc.visit(m, c);
		}
	}

	private static class MethodParameterListCounter extends VoidVisitorAdapter<ClassOrInterfaceDeclaration>
	{
		@Override
		public void visit(MethodDeclaration m, ClassOrInterfaceDeclaration arg)
		{
			int parametersNumber = m.getParameters().size();

			if (parametersNumber > MAX_METHOD_PARAMETERS_NUMBER)
				System.out.println("Method \"" + m.getSignature() + "\" in class \"" + arg.getNameAsString() + "\" has " + parametersNumber + " parameters!\n");
		}
	}
}
