import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

public class fields {
	
	
	public static ArrayList<String> getFields(BodyDeclaration member, ArrayList classNames, ArrayList interfaceNames)
	{
		
		FieldDeclaration field = (FieldDeclaration) member;
		AccessSpecifier accessSpecifier = ModifierSet.getAccessSpecifier(field.getModifiers());

		String oneToOne = "";
		String oneToMany = "";
		Type fType = field.getType();

		if (classNames.contains(fType.toString()) || interfaceNames.contains(fType.toString())) {
			// relationship
			oneToOne = fType.toString();
		}
		else {
			if (fType instanceof ReferenceType) {
				if (((ReferenceType) fType).getType() instanceof ClassOrInterfaceType) {
					List typeArgs = ((ClassOrInterfaceType) ((ReferenceType) fType).getType()).getTypeArgs();
					if (typeArgs.size() > 0) {
						String fType1 = typeArgs.get(0).toString();
						if (classNames.contains(fType1) || interfaceNames.contains(fType1)) {
							// 1-many relationship
							oneToMany = fType1;
						}
					}
				}
			}
		}

		ArrayList fieldList = new ArrayList();
		for(VariableDeclarator v: field.getVariables()) {
			String fieldName = v.getId().getName();
			String fieldType = field.getType().toString();
			HashMap<String, String> fieldMap = new HashMap<String, String>();
			fieldMap.put("type", fieldType);
			fieldMap.put("name", fieldName);
			fieldMap.put("oneOne", oneToOne);
			fieldMap.put("oneMany", oneToMany);
			fieldMap.put("access", accessSpecifier.getCodeRepresenation());
			
			fieldList.add(fieldMap);
		}

		return fieldList;
	}


}
