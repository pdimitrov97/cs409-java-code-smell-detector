package com.github.pdimitrov97.java_code_smell_detector;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class RefusedBequestSmell extends VoidVisitorAdapter<Map<String, ClassOrInterfaceDeclaration>>
{
	@Override
	public void visit(ClassOrInterfaceDeclaration c, Map<String, ClassOrInterfaceDeclaration> arg)
	{
		arg.put(c.getNameAsString(), c);
	}
}