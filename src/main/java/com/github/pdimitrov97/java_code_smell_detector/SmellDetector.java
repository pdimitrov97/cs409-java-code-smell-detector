package com.github.pdimitrov97.java_code_smell_detector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;

public class SmellDetector
{
	private static List<File> files;
	private static List<CompilationUnit> cus;

	public static void gatherJavaFiles(File file)
	{
		if (file.isDirectory())
		{
			// System.out.println("Looking for Java files in directory\"" + file.getPath() + "\"");

			for (String childFilename : file.list())
			{
				// System.out.println("Found directory \"" + file.getName() + "\"");
				gatherJavaFiles(new File(file.getPath() + "/" + childFilename));
			}
		}
		else if (file.isFile())
		{
			// System.out.println("Found file \"" + file.getPath() + "\"");

			if (file.exists() && file.canRead())
			{
				int extensionIndex = file.getName().lastIndexOf('.');

				if ((file.getName().substring(extensionIndex + 1)).equals("java"))
				{
					files.add(file);
					System.out.println("Discovered Java class \"" + file.getName() + "\"");
				}
			}
		}
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println("Welcome to Pavel Dimitrov's Java Code Smell Detector using JavaParser !");
		System.out.println("\nThe program can find:\n" + "- Long Parameter List\n" + "- Long Method (counting lines)\n" + "- Large Class (counting lines)\n" + "- Data class\n" + "- Message Chains\n" + "- Temporary Field\n" + "- Large Class (counting statements)\n" + "- Long Method (counting statements)\n" + "- Middle Man\n" + "- Refused Bequest\n");
		System.out.println("Supported commands:\n" + "find_smells <java file | directory>\n" + "exit - to exit the program\n");

		String input;
		Scanner scanner = new Scanner(System.in);
		String[] tokens;

		while (true)
		{
			System.out.println("Enter command:");
			input = scanner.nextLine();
			tokens = input.split(" (?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"); // Split on whitespace but ignore text between double quotation marks.

			switch (tokens[0])
			{
				case "find_smells":
				{
					if (tokens.length > 1)
					{
						if (tokens[1].isEmpty() || tokens[1].equals("") || tokens[1] == null)
						{
							System.out.println("Invalid file or path!");
							break;
						}
					}
					else
					{
						System.out.println("File or path missing!");
						break;
					}

					files = new ArrayList<File>();
					cus = new ArrayList<CompilationUnit>();

					tokens[1] = tokens[1].replace("\"", "");
					File in = new File(tokens[1]);
					gatherJavaFiles(in);

					for (File file : files)
					{
						CompilationUnit cu;

						try
						{
							cu = StaticJavaParser.parse(file);
							cus.add(cu);
							System.out.println("Added file \"" + file.getName() + "\"");
						}
						catch (FileNotFoundException e)
						{
							System.out.println("Error: Couldn't add \"" + file.getName() + "\" - " + e.getMessage());
						}
					}

					System.out.println();
					System.out.println();

					Map<String, ClassOrInterfaceDeclaration> classesRefusedBequest = new HashMap<String, ClassOrInterfaceDeclaration>();
					List<ClassOrInterfaceDeclaration> classesMiddleMan = new ArrayList<ClassOrInterfaceDeclaration>();

					for (CompilationUnit cu : cus)
					{
						VoidVisitor<?> longMethodParameterListSmell = new LongMethodParameterListSmell();
						longMethodParameterListSmell.visit(cu, null);

						VoidVisitor<?> simpleLongMethodSmell = new SimpleLongMethodSmell();
						simpleLongMethodSmell.visit(cu, null);

						VoidVisitor<?> simpleLongClassSmell = new SimpleLongClassSmell();
						simpleLongClassSmell.visit(cu, null);

						VoidVisitor<?> dataclassSmell = new DataClassSmell();
						dataclassSmell.visit(cu, null);

						VoidVisitor<?> messageChainSmell = new MessageChainSmell();
						messageChainSmell.visit(cu, null);

						VoidVisitor<?> temporaryFieldSmell = new TemporaryFieldSmell();
						temporaryFieldSmell.visit(cu, null);

						VoidVisitor<?> longClassSmell = new LongClassSmell();
						longClassSmell.visit(cu, null);

						VoidVisitor<?> longMethodSmell = new LongMethodSmell();
						longMethodSmell.visit(cu, null);

						VoidVisitor<List<ClassOrInterfaceDeclaration>> middleManSmell = new MiddleManSmell();
						middleManSmell.visit(cu, classesMiddleMan);

						VoidVisitor<Map<String, ClassOrInterfaceDeclaration>> refusedBequestSmell = new RefusedBequestSmell();
						refusedBequestSmell.visit(cu, classesRefusedBequest);
					}

					MiddleManSmellDetector.detectMiddleMan(classesMiddleMan);
					RefusedBequestSmellDetector.detectRefusedBequest(classesRefusedBequest);
					break;
				}
				case "exit":
				{
					System.exit(0);
				}
				default:
				{
					System.out.println("Invalid command! Supported commands:\n" + "find_smells <java file | directory>\n" + "exit - to exit the program\n");
				}
			}
		}
	}
}