package com.github.pdimitrov97.java_code_smell_detector;
import java.util.List;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MiddleManSmell extends VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>>
{
	@Override
	public void visit(ClassOrInterfaceDeclaration c, List<ClassOrInterfaceDeclaration> arg)
	{
		arg.add(c);
	}
}
