import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class ClassAndInterfaceIdentify {
	 
	 
	
	public ArrayList<String> getClasses(CompilationUnit cu){
		
		ArrayList<String> class_List = new ArrayList<>();
		
		for (TypeDeclaration typeDec : cu.getTypes()) {
			
			if (typeDec instanceof ClassOrInterfaceDeclaration){				
			
			ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) typeDec ;
			
			if(!coi.isInterface())
			{
				if (typeDec !=null)
				{
				
					class_List.add(((ClassOrInterfaceDeclaration)typeDec).getName());
				}
			}
		}
		}
		return class_List;
	
	}
	
	public static ArrayList<String> getExtendsList(TypeDeclaration typeDec)
	{
		ArrayList<String> class_extends_List = new ArrayList<>();
		
		if (typeDec instanceof ClassOrInterfaceDeclaration) {
			
			ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) typeDec;
			
			List<ClassOrInterfaceType> extendList = cls.getExtends();
			
			for (ClassOrInterfaceType c : extendList) {
				
				class_extends_List.add(c.getName());
			}
		}
		return class_extends_List;
	}
	
	
	public static ArrayList<String> getInterfaces(CompilationUnit cu)
	{
		ArrayList<String> interface_List = new ArrayList<>();
		
		for (TypeDeclaration typeDec : cu.getTypes()){
			
			ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) typeDec;
			
			if(coi instanceof ClassOrInterfaceDeclaration && coi.isInterface())
			{
				interface_List.add(typeDec.getName());
			}
		}
		return interface_List;
	}
	
	public static ArrayList<String> getImplementsList(TypeDeclaration typeDec)
	{
		ArrayList<String> interface_implements_List = new ArrayList<>();
		if (typeDec instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration cls = (ClassOrInterfaceDeclaration) typeDec;
			List<ClassOrInterfaceType> implementList = cls.getImplements();
			for (ClassOrInterfaceType c : implementList) {
				interface_implements_List.add(c.getName());
			}
		}
		return interface_implements_List;
	}
	

}
