import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.sun.deploy.util.StringUtils;

public class methods {
	

public static HashMap<String, String> getMethods(BodyDeclaration member)
{
	MethodDeclaration method = (MethodDeclaration) member;
	String methodName = method.getName();
	String methodType = method.getType().toString();
	BlockStmt body = method.getBody();
	HashMap<String, String> methodmap = new HashMap<String, String>();
	if (body != null) {
		List<Statement> methodStatements = method.getBody().getStmts();
		ArrayList uses = new ArrayList();

		for (Statement st: methodStatements) {
			String tmpSt = st.toString();
			if (tmpSt.contains("new ")) {
				uses.add(tmpSt.split(" ")[0]);
			}
		}
		
		methodmap.put("uses", StringUtils.join(uses, ","));
	} else {
		methodmap.put("uses", "");
	}

	ArrayList parameters = new ArrayList();
	ArrayList parameterTypes = new ArrayList();
	
	for (Parameter p : method.getParameters()) {
		parameters.add(p.toString());
		parameterTypes.add(p.getType().toString());
	}

	AccessSpecifier accessSpecifier = ModifierSet.getAccessSpecifier(method.getModifiers());

	methodmap.put("type", methodType);
	methodmap.put("name", methodName);
	if (accessSpecifier.getCodeRepresenation().equals(""))
	{
		methodmap.put("access", "package");
	}
	else {
		
		methodmap.put("access", accessSpecifier.getCodeRepresenation());
	}
		
	methodmap.put("parameters", StringUtils.join(parameters, ","));
	methodmap.put("parameterTypes", StringUtils.join(parameterTypes, ","));
	
	return methodmap;

}

}
