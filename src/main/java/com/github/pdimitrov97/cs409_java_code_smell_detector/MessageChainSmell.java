package com.github.pdimitrov97.cs409_java_code_smell_detector;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.Pair;

public class MessageChainSmell extends VoidVisitorAdapter<Void>
{
	private static final int MAX_MESSAGE_CHAIN_NUMBER = 2;

	@Override
	public void visit(ClassOrInterfaceDeclaration c, Void arg)
	{
		if (c.isInterface())
			return;

		for (ConstructorDeclaration constructor : c.getConstructors())
		{
			MessageChainDetector mcd = new MessageChainDetector();
			Pair<String, String> information = new Pair<String, String>(c.getNameAsString(), constructor.getSignature().toString());
			mcd.visit(constructor, information);
		}

		for (MethodDeclaration m : c.getMethods())
		{
			MessageChainMethodVisitor mcmv = new MessageChainMethodVisitor();
			mcmv.visit(m, c);
		}

		super.visit(c, arg);
	}

	private static class MessageChainMethodVisitor extends VoidVisitorAdapter<ClassOrInterfaceDeclaration>
	{
		@Override
		public void visit(MethodDeclaration m, ClassOrInterfaceDeclaration arg)
		{
			Pair<String, String> information = new Pair<String, String>(arg.getNameAsString(), m.getSignature().toString());
			MessageChainDetector mcd = new MessageChainDetector();
			mcd.visit(m, information);
		}
	}

	private static class MessageChainDetector extends VoidVisitorAdapter<Pair<String, String>>
	{
		@Override
		public void visit(MethodCallExpr m, Pair<String, String> arg)
		{
			for (Expression e : m.getArguments())
			{
				if (e.isMethodCallExpr())
				{
					if (expandMethodCallExpr(e.asMethodCallExpr(), arg) > MAX_MESSAGE_CHAIN_NUMBER)
						System.out.println("\"" + e.asMethodCallExpr().toString() + "\" in method \"" + arg.b + "\" in class \"" + arg.a + "\" is a Message chain!\n");
				}
			}

			if (expandMethodCallExpr(m, arg) > MAX_MESSAGE_CHAIN_NUMBER)
				System.out.println("\"" + m.toString() + "\" in method \"" + arg.b + "\" in class \"" + arg.a + "\" is a Message chain!\n");
		}

		private int expandMethodCallExpr(MethodCallExpr m, Pair<String, String> arg)
		{
			Optional<Expression> tempExpr = m.getScope();

			if (tempExpr.isPresent())
			{
				Expression expr = tempExpr.get();

				if (expr.isMethodCallExpr())
				{
					for (Expression e : expr.asMethodCallExpr().getArguments())
					{
						if (e.isMethodCallExpr())
							super.visit(e.asMethodCallExpr(), arg);
					}

					return 1 + expandMethodCallExpr(expr.asMethodCallExpr(), arg);
				}
			}

			return 1;
		}
	}
}