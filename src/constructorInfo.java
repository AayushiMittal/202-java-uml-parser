import java.util.ArrayList;
import java.util.HashMap;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.sun.deploy.util.StringUtils;

public class constructorInfo {
	
	 
	
	public static HashMap<String, String> getConstructorInfo(BodyDeclaration member)
	{
		HashMap<String, String> constructorMap = new HashMap<String, String>();
		ConstructorDeclaration constructorDeclared = (ConstructorDeclaration) member;
		constructorMap.put("name", constructorDeclared.getName());
		constructorMap.put("access", ModifierSet.getAccessSpecifier(constructorDeclared.getModifiers()).getCodeRepresenation());
		constructorMap.put("parameters", StringUtils.join(getConsParams(constructorDeclared), ","));
		constructorMap.put("parameterTypes", StringUtils.join(getConsParamtypes(constructorDeclared), ","));
		return constructorMap;

	}
	
	public static ArrayList<String> getConsParams(ConstructorDeclaration cd){
		
		ArrayList<String> cp = new ArrayList<>();
		for (Parameter p : cd.getParameters()) {			
			cp.add(p.toString());
		}
		return cp;
		
	}
	
	public static ArrayList<String> getConsParamtypes(ConstructorDeclaration cd){
		
		ArrayList<String> cpt = new ArrayList<>();
		for (Parameter p : cd.getParameters()) {			
			cpt.add(p.getType().toString());
		}
		return cpt;
	}
	
}
