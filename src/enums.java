import java.util.ArrayList;
import java.util.HashMap;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class enums {
	
	
	public static ArrayList getEnums(CompilationUnit cu) {
		ArrayList<String> enum_name = new ArrayList<String>();
		ArrayList enumList = new ArrayList();
		for (TypeDeclaration typeDec : cu.getTypes()) {
			if(typeDec instanceof EnumDeclaration)
			{
				enumList.add(typeDec.getName());
			}
		}
		return enumList;
	}
	
	public static HashMap<String,ArrayList> getEnumInfo(BodyDeclaration member) {
		ArrayList<String> enum_name = new ArrayList<String>();
		ArrayList<String> entries = new ArrayList<String>();
		HashMap<String, ArrayList> enumInfoMap = new HashMap<String, ArrayList>();
		EnumDeclaration enumDeclared = (EnumDeclaration) member;
		String enumName = enumDeclared.getName();		
		enum_name.add(enumName);	

		for (EnumConstantDeclaration ecd: enumDeclared.getEntries()) {
			entries.add(ecd.getName());
		}

		enumInfoMap.put("name", enum_name);
		enumInfoMap.put("enumFields", entries);
		return enumInfoMap;
	}

}
