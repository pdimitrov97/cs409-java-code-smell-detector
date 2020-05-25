# Java Code Smell Detector
This is a Java program that detects "code smells" using the JavaParser library.

## Description
The program can identify the following smells:
- Long Parameter Lists
- Long Methods (counting lines)
- Large Classs (counting lines)
- Data classes
- Message Chains
- Temporary Fields
- Large Classes (counting statements)
- Long Methodes (counting statements)
- Middle Man classes
- Refused Bequest

## How to use it
The program supports the following commands:
- <b>find_smells <java file | directory></b> - searches for .java files the specified directory and in all subdirectories and checks each file for "smells".
- <b>exit</b> - to exit the program.

## Requirements:
- JDK 8
- Maven

## Build:
````
mvn clean install
````
