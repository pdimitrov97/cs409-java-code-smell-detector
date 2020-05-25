package com.github.pdimitrov97.java_code_smell_detector;
import java.util.Map;
import java.util.Optional;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class RefusedBequestSmellDetector
{
	public static void detectRefusedBequest(Map<String, ClassOrInterfaceDeclaration> classes)
	{
		for (Map.Entry<String, ClassOrInterfaceDeclaration> c : classes.entrySet())
		{
			ClassOrInterfaceDeclaration target = classes.get(c.getKey());
			ClassOrInterfaceDeclaration checkClass;

			if (target.getExtendedTypes().size() == 0)
				continue;

			for (ClassOrInterfaceType temp : target.getExtendedTypes())
			{
				if (classes.containsKey(temp.toString()))
				{
					checkClass = classes.get(temp.toString());
					checkForRefusedBequest(target, checkClass, classes);
				}
			}
		}
	}

	private static void checkForRefusedBequest(ClassOrInterfaceDeclaration target, ClassOrInterfaceDeclaration checkClass, Map<String, ClassOrInterfaceDeclaration> classes)
	{
		for (MethodDeclaration currentMethod : target.getMethods())
		{
			if (currentMethod.getName().toString().equals("equals") || currentMethod.getName().toString().equals("toString") || currentMethod.getName().toString().equals("compareTo") || currentMethod.getName().toString().equals("hashCode"))
				continue;

			for (MethodDeclaration parentMethod : checkClass.getMethods())
			{
				if (currentMethod.getNameAsString().equals(parentMethod.getNameAsString()))
				{
					if (currentMethod.getSignature().equals(parentMethod.getSignature()))
					{
						if (currentMethod.getBody().isPresent())
						{
							if (currentMethod.getBody().get().getStatements().size() == 0)
								System.out.println("Refused Bequest found - Method \"" + currentMethod.getSignature().toString() + "\" in class \"" + target.getNameAsString() + "\" and parent class \"" + checkClass.getNameAsString() + "\"\n");
							else
							{
								boolean foundSuper = false;

								for (Statement s : currentMethod.getBody().get().getStatements())
								{
									if (findSuperInStatements(s))
									{
										foundSuper = true;
										break;
									}
								}

								if (!foundSuper)
									System.out.println("Refused Bequest found - Method \"" + currentMethod.getSignature().toString() + "\" in class \"" + target.getNameAsString() + "\" and parent class \"" + checkClass.getNameAsString() + "\"\n");
							}
						}
					}
				}
			}
		}

		if (checkClass.getExtendedTypes().size() > 0)
		{
			ClassOrInterfaceDeclaration newCheckClass;

			for (ClassOrInterfaceType temp : checkClass.getExtendedTypes())
			{
				if (classes.containsKey(temp.toString()))
				{
					newCheckClass = classes.get(temp.toString());
					checkForRefusedBequest(target, newCheckClass, classes);
				}
			}
		}
	}

	private static boolean findSuperInStatements(Statement s)
	{
		if (s.isBlockStmt())
		{
			for (Statement temp : s.asBlockStmt().getStatements())
			{
				if (findSuperInStatements(temp))
					return true;
			}

		}

		if (s.isIfStmt())
		{
			if (findSuperInStatements(s.asIfStmt().getThenStmt()))
				return true;

			if (s.asIfStmt().hasElseBranch())
			{
				if (findSuperInStatements(s.asIfStmt().getElseStmt().get()))
					return true;
			}
		}

		if (s.isForStmt())
		{
			if (findSuperInStatements(s.asForStmt().getBody()))
				return true;
		}

		if (s.isForEachStmt())
		{
			if (findSuperInStatements(s.asForEachStmt().getBody()))
				return true;
		}

		if (s.isWhileStmt())
		{
			if (findSuperInStatements(s.asWhileStmt().getBody()))
				return true;
		}

		if (s.isDoStmt())
			findSuperInStatements(s.asDoStmt().getBody());

		if (s.isTryStmt())
		{
			for (Statement temp : s.asTryStmt().getTryBlock().getStatements())
			{
				if (findSuperInStatements(temp))
					return true;
			}

			for (CatchClause cc : s.asTryStmt().getCatchClauses())
			{
				for (Statement temp : cc.getBody().getStatements())
				{
					if (findSuperInStatements(temp))
						return true;
				}
			}

			if (s.asTryStmt().getFinallyBlock().isPresent())
			{
				for (Statement temp : s.asTryStmt().getFinallyBlock().get().getStatements())
				{
					if (findSuperInStatements(temp))
						return true;
				}
			}
		}

		if (s.isSynchronizedStmt())
		{
			for (Statement temp : s.asSynchronizedStmt().getBody().getStatements())
			{
				if (findSuperInStatements(temp))
					return true;
			}
		}

		if (s.isSwitchStmt())
		{
			for (SwitchEntry se : s.asSwitchStmt().getEntries())
			{
				for (Statement temp : se.getStatements())
				{
					if (findSuperInStatements(temp))
						return true;
				}
			}
		}

		if (s.isLabeledStmt())
		{
			if (findSuperInStatements(s.asLabeledStmt().getStatement()))
				return true;
		}

		if (s.isExpressionStmt())
		{
			Expression temp = s.asExpressionStmt().getExpression();

			if (temp.isMethodCallExpr())
			{
				Optional<Expression> exprScope = temp.asMethodCallExpr().getScope();

				if (exprScope.isPresent())
				{
					Expression expr = exprScope.get();

					if (expr.toString().equals("super"))
						return true;
				}
			}
		}

		return false;
	}
}
