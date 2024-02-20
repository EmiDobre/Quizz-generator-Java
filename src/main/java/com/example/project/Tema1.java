package com.example.project;

public class Tema1 {

	public static void main(final String[] args)
	{
		if ( args == null ) {
			System.out.println("Hello world!");
			return;
		}

		//nr arg main:
		int count = args.length;

		Parser parser = new Parser(count, args);
		parser.parseArgs();
		parser.checkAct();

	}

}
