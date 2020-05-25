package com.github.pdimitrov97.java_code_smell_detector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;

public class MiddleManSmellDetector
{
	public static void detectMiddleMan(List<ClassOrInterfaceDeclaration> classes)
	{
		ArrayList<String> classList = new ArrayList<>();
		HashMap<String, SimpleName> classAndMethodsMap = new HashMap<>();
		HashMap<SimpleName, String> variableAndClassMap = new HashMap<>();

		for (ClassOrInterfaceDeclaration coi : classes)
		{
			classList.add(coi.getName().toString());

			for (BodyDeclaration m : coi.getMembers())
			{
				if (m.isMethodDeclaration())
					classAndMethodsMap.put(coi.getName().toString(), m.asMethodDeclaration().getName());
			}
		}

		ArrayList<SimpleName> foreignVariables = new ArrayList<>();

		// Fill out all class methods with associated class, used in function1
		for (ClassOrInterfaceDeclaration coi : classes)
		{
			if (coi.isInterface())
				continue;

			// Class name
			for (BodyDeclaration m : coi.getMembers())
			{

				if (m.isFieldDeclaration())
				{
					NodeList<VariableDeclarator> myList = m.asFieldDeclaration().getVariables();

					for (VariableDeclarator vd : myList)
					{
						if (classList.contains(vd.getType().toString()))
						{
							foreignVariables.add(vd.getName());
							variableAndClassMap.put(vd.getName(), vd.getType().toString());
						}
					}
				}

				if (m.isMethodDeclaration())
				{
					if (m.asMethodDeclaration().getBody().isPresent())
					{
						// Chekc if it is just 1 statement
						if (m.asMethodDeclaration().getBody().get().getStatements().size() == 1)
						{
							// Check if its of type return
							if (m.asMethodDeclaration().getBody().get().getStatement(0).isReturnStmt())
							{
								if (m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().isPresent())
								{
									// Check if its a method call
									if (m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().isMethodCallExpr())
									{
										// Check is the variable is of a user defined class
										if (m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().asMethodCallExpr().getScope().isPresent())
										{
											if (m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().asMethodCallExpr().getScope().get().isNameExpr())
											{
												if (foreignVariables.contains(m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().asMethodCallExpr().getScope().get().asNameExpr().getName()))
												{
													// Check if the variable's call method is defined in its class
													if (classAndMethodsMap.get(variableAndClassMap.get(m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().asMethodCallExpr().getScope().get().asNameExpr().getName())).equals(m.asMethodDeclaration().getBody().get().getStatement(0).asReturnStmt().getExpression().get().asMethodCallExpr().getName()))
														System.out.println("In class " + coi.getName() + " the Method " + m.asMethodDeclaration().getName() + " is a MiddleMan!\n");
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
