package org.adelbs.iso8583.bsparser;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * This class will parse the text through the BeanShell library.<br/>
 * The content of the String should be at the following format:<br/><br/>
 * <i>
 * text text text<br/>
 * text text text<br/>
 * <%<br/>
 * command1();<br/>
 * command2();<br/>
 * %><br/>
 * text text text<br/><br/>
 * </i>
 * All the content outside of the <% and %> tags, will be converted to "print(value);" 
 * and the content inside the <% and %> tags will keep the same. So, the example above will be converted to:<br/><br/>
 * <i>
 * print("text text text");<br/>
 * print("text text text");<br/>
 * command1();<br/>
 * command2();<br/>
 * print("text text text");<br/><br/>
 * </i>
 * And then, the script will be passed to the BeanShell interpreter.<br/>
 * By default, all scripts have the default import: com.ca.smartresponse.command.*;<br/>
 * 
 * @author CA - AD
 *
 */
public class Parser {
	
	public static final String OPENSCRIPT = "<%";
	public static final String CLOSESCRIPT = "%>";

	private boolean executeScript = false;

	/**
	 * This method will parse the String through the BeanShell library.<br/>
	 * The content of the String should be at the following format:<br/><br/>
	 * <i>
	 * text text text<br/>
	 * text text text<br/>
	 * <%<br/>
	 * command1();<br/>
	 * command2();<br/>
	 * %><br/>
	 * text text text<br/><br/>
	 * </i>
	 * All the content outside of the <% and %> tags, will be converted to "print(value);" 
	 * and the content inside the <% and %> tags will keep the same. So, the example above will be converted to:<br/><br/>
	 * <i>
	 * print("text text text");<br/>
	 * print("text text text");<br/>
	 * command1();<br/>
	 * command2();<br/>
	 * print("text text text");<br/><br/>
	 * </i>
	 * And then, the script will be passed to the BeanShell interpreter.<br/>
	 * By default, all scripts have the default import: com.ca.smartresponse.command.*;<br/>
	 * 
	 * @param fullText String to be parsed
	 * @return parsed String
	 */
	public String parseText(String fullText) {
		
		Console console = new Console();
		Interpreter interpreter = new Interpreter(console);
		StringBuilder script = new StringBuilder();
		String[] splitLine = fullText.split("\n");
		
		String result = fullText;
		
		script.append("import org.adelbs.iso8583.bsparser.util.*;")
			.append("public void print(String str) {")
			.append("    console.print(str);")
			.append("}")
			.append("public void println(String str) {")
			.append("    console.println(str);")
			.append("}")
			.append("public void setSupressNewLine(boolean value) {")
			.append("    console.setSupressNewLine(value);")
			.append("}")
			.append("public boolean isSupressNewLine() {")
			.append("    return console.isSupressNewLine();")
			.append("}")
			.append("public void autoprint(String value) {")
			.append("    if (isSupressNewLine()) print(value);")
			.append("    else println(value);")
			.append("}");
		
			for (int i = 0; i <= splitLine.length - 1; i++)
				parseLine(console, script, splitLine[i].replaceAll("\r", ""));
			
			try {
				interpreter.set("console", console);
				interpreter.eval(script.toString());
			} 
			catch (EvalError e) {
				console.print(e);
				e.printStackTrace();
			}
			
			result = console.getResult().toString();
		
		return result;
	}
	
	private void parseLine(Console console, StringBuilder result, String line) {
		
		if (line.indexOf(OPENSCRIPT) > -1) {
			
			result.append(getBeanPrintCmd(console, line.substring(0, line.indexOf(OPENSCRIPT)))).append("\n");

			if (line.indexOf(CLOSESCRIPT) > -1) {
				result.append(line.substring(line.indexOf(OPENSCRIPT) + OPENSCRIPT.length(), line.indexOf(CLOSESCRIPT))).append("\n");
				result.append(getBeanPrintCmd(console, line.substring(line.indexOf(CLOSESCRIPT) + CLOSESCRIPT.length()))).append("\n");
			}
			else {
				executeScript = true;
				result.append(line.substring(line.indexOf(OPENSCRIPT) + OPENSCRIPT.length())).append("\n");
			}
		}
		else if (line.indexOf(CLOSESCRIPT) > -1) {
			executeScript = false;
		
			result.append(line.substring(0, line.indexOf(CLOSESCRIPT))).append("\n");
			result.append(getBeanPrintCmd(console, line.substring(line.indexOf(CLOSESCRIPT) + CLOSESCRIPT.length()))).append("\n");
		}
		else {
			if (executeScript) result.append(line).append("\n");
			else result.append(getBeanPrintCmd(console, line)).append("\n");
		}
	}

	private String getBeanPrintCmd(Console console, String value) {
		String content = getBeanString(value);
		if (content.equals("\"\"")) content = "";
		else content = "autoprint(".concat(content).concat(");");
		return content;
	}
	
	private String getBeanString(String value) {
		String result = value;
		result = result.replaceAll("\\\\", "\\\\\\\\");
		result = result.replaceAll("\"", "\\\\\"");
		
		return "\"".concat(result).concat("\"");
	}
}
