package com.github.pdimitrov97.cs409_java_code_smell_detector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TemporaryFieldSmell extends VoidVisitorAdapter<Void>
{
	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		List<String> globalVariables = new ArrayList<String>();

		for (FieldDeclaration field : c.getFields())
		{
			if (!field.isFinal())
			{
				for (VariableDeclarator variable : field.getVariables())
				{
					globalVariables.add(variable.getNameAsString());
				}
			}
		}

		Map<String, List<String>> methodVariables = new HashMap<String, List<String>>();
		List<String> currentMethodVariables;

		for (MethodDeclaration method : c.getMethods())
		{
			if (method.getName().toString().equals("equals") || method.getName().toString().equals("toString") || method.getName().toString().equals("compareTo") || method.getName().toString().equals("hashCode"))
				continue;

			currentMethodVariables = new ArrayList<String>();
			NameExprVisitor nameExprVisitor = new NameExprVisitor();
			nameExprVisitor.visit(method, currentMethodVariables);
			methodVariables.put(method.getNameAsString(), currentMethodVariables);
		}

		int usageCount = 0;

		for (String variable : globalVariables)
		{
			usageCount = 0;

			for (Map.Entry<String, List<String>> method : methodVariables.entrySet())
			{
				currentMethodVariables = methodVariables.get(method.getKey());

				if (Collections.frequency(currentMethodVariables, variable) > 0)
					usageCount++;
			}

			if (methodVariables.size() == 0)
				System.out.println("Variable \"" + variable + "\" is a Temporary Field in class \"" + c.getNameAsString() + "\" or the class might be a Data Class\n");
			else if (methodVariables.size() == 1)
			{
				if (usageCount == 0)
					System.out.println("Variable \"" + variable + "\" is a Temporary Field in class \"" + c.getNameAsString() + "\"\n");
			}
			else if (usageCount <= 1)
				System.out.println("Variable \"" + variable + "\" is a Temporary Field in class \"" + c.getNameAsString() + "\"\n");
		}
	}

	private static class NameExprVisitor extends VoidVisitorAdapter<List<String>>
	{
		@Override
		public void visit(NameExpr ne, List<String> arg)
		{
			arg.add(ne.getNameAsString());
		}
	}
}
