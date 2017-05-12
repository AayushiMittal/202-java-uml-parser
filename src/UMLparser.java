import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.sun.deploy.util.StringUtils;
import net.sourceforge.plantuml.SourceStringReader;
public class UMLparser {
	
	
	ClassAndInterfaceIdentify cid = new ClassAndInterfaceIdentify();
	enums enm = new enums();
	constructorInfo ci = new constructorInfo();
	fields f = new fields();
	methods m = new methods();
	
	
	public static void main(String[] args) throws Exception{

		UMLparser parser= new UMLparser();
		System.out.println(args[0]);
		System.out.println(args[1]);
		
		if(args.length==1 ) {
			String imageoutput = "imageoutput.png";
			File folder_name = new File(args[0]);
			if (folder_name.isDirectory()) {
				parser.parse(folder_name, imageoutput);
			} else {
				throw new FileNotFoundException("2 Please enter a valid path for the file");
			}
		}else if ( args.length==2 ) {
			String imageoutput = args[1];
			File folder_name =new File(args[0]);
			if (folder_name.isDirectory());
			parser.parse(folder_name, imageoutput);
		} else {
			throw new FileNotFoundException("2 Please enter a valid path for the file");

		}

	}
	private void parse(File folder_name, String imageoutput) throws IOException, ParseException {
		// TODO Auto-generated method stub
		ArrayList<String> javafiles = new ArrayList<>();
		StringBuilder input_StringBuilder = new StringBuilder();
		
			for (File file : folder_name.listFiles()) {
			
			if (file.getPath().toLowerCase().endsWith(".java") && file.getPath() != null) {
				
				javafiles.add(file.getPath());
			}
		}		
		
		for ( String file : javafiles){
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			String line = br.readLine();
			while(line != null){
			input_StringBuilder.append(line);
			input_StringBuilder.append('\n');
			line = br.readLine();
			}
		}
		
	    String ipStreamStr1;
	    ipStreamStr1 = input_StringBuilder.toString();	
		ipStreamStr1 = ipStreamStr1.replace("import", "// import");
		ipStreamStr1 = ipStreamStr1.replace("package", "// package");		

		InputStream in = new ByteArrayInputStream(ipStreamStr1.getBytes(StandardCharsets.UTF_8));

		CompilationUnit cu;
		try {
			// We are parsing the file
			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}
		
		ArrayList bal = new ArrayList();
		
		ArrayList class_Names = cid.getClasses(cu);
		
		ArrayList interface_Names = cid.getInterfaces(cu);
		
		ArrayList enum_Names = enm.getEnums(cu);	
		
		int count = 0;
		
		for (TypeDeclaration typeDec : cu.getTypes()) {
			
			count++;
			
			List<BodyDeclaration> members = typeDec.getMembers();			
			ArrayList extends_List = cid.getExtendsList(typeDec);			
			ArrayList implements_List = cid.getImplementsList(typeDec);				
			ArrayList enum_List = new ArrayList();
			ArrayList field_List = new ArrayList();
			ArrayList constructor_List = new ArrayList();
			ArrayList method_List = new ArrayList();
			
			if (members != null) {
				
				for (BodyDeclaration member : members) {
					if (member instanceof FieldDeclaration) {
						ArrayList<String> field_Info = f.getFields(member, class_Names, interface_Names);
						field_List.addAll(field_Info);

					} else if (member instanceof ConstructorDeclaration) {
						HashMap<String, String> constructorMap = ci.getConstructorInfo(member);
						constructor_List.add(constructorMap);

					} else if (member instanceof MethodDeclaration) {
						HashMap<String, String> methodMap = m.getMethods(member);						
						method_List.add(methodMap);
					}
				}				
				HashMap<String, ArrayList> addAll = new HashMap<String, ArrayList>();
				addAll.put("methods", method_List);
				addAll.put("constructors", constructor_List);
				addAll.put("extends", extends_List);
				addAll.put("fields", field_List);
				addAll.put("implements", implements_List);
				
				ArrayList cls_Name = new ArrayList();
				cls_Name.add(typeDec.getName());
				addAll.put("name", cls_Name);				
				bal.add(addAll);
			} 
			
			else if (typeDec instanceof EnumDeclaration) {
				HashMap<String, ArrayList> enumMap = enm.getEnumInfo(typeDec);
				bal.add(enumMap);
			}
		}
		
		String mainUMLGrammar = "@startuml\n";
		HashMap<String, ArrayList> dependency_map = new HashMap<String, ArrayList>();
		HashMap<String, ArrayList> manytoRelationship = new HashMap<String, ArrayList>();
		HashMap<String, ArrayList> onetoRelationship = new HashMap<String, ArrayList>();
		for (int i = 0; i < bal.size(); i++) {
			HashMap<String, ArrayList> tempMap = (HashMap<String, ArrayList>) bal.get(i);			
			ArrayList classNameList = tempMap.get("name");
			String className = (String) classNameList.get(0);
			ArrayList fields_List = tempMap.get("fields");
			ArrayList methods_List = tempMap.get("methods");
			ArrayList constructors_List = tempMap.get("constructors");
			ArrayList extends_List1 = tempMap.get("extends");
			ArrayList implements_List1 = tempMap.get("implements");
			Set<String> field_Names = new HashSet<String>();
			Set<String> method_Names = new HashSet<String>();			
			Set<String> usesClass = new HashSet<String>();
			Set<String> dependencies = new HashSet<String>();
			String grammar = "";
			if (enum_Names.contains(className)) {				
				mainUMLGrammar += "enum " + className + " {\n";
				ArrayList enumFieldsList = tempMap.get("enumFields");
				
				for (Object obj:enumFieldsList)
				{
					mainUMLGrammar += obj +"\n";
				}
				mainUMLGrammar += "}\n";
				continue;
			}
			
			for (Object obj:fields_List){
				HashMap<String, String> fl = (HashMap<String, String>) obj;
				field_Names.add(fl.get("name").toLowerCase());
			}
			
			for (Object obj:methods_List){
				HashMap<String, String> ml = (HashMap<String, String>) obj;
				method_Names.add(ml.get("name").toLowerCase());
			}
			if (interface_Names.contains(className)) {
				grammar = "interface " + className + " {\n";
			} else 
			{
				grammar = "class " + className + " {\n";
			}
			
			
			for (Object obj:constructors_List){
				HashMap<String, String> cns = (HashMap<String, String>) obj;
				String clsName = cns.get("name");
				String classAccess = cns.get("access");
				//String[] classParameters = cns.get("parameters").split(",");
				//String[] classParameterTypes = cns.get("parameterTypes").split(",");
				for (Object cpt:cns.get("parameterTypes").split(",")){
					if (interface_Names.contains(cpt)){
						usesClass.add(cpt.toString());
					}
				}
				ArrayList parameters_Reorder = new ArrayList();
				for (String cp: cns.get("parameters").split(",")) {
					if (cp.equals("")) {
						continue;
					}
					//String[] parameterTypeName = cp.split(" ");
					//String parameterType = parameterTypeName[0];
					//String parameterName = parameterTypeName[1];
					parameters_Reorder.add(cp.split(" ")[1] + ":" + cp.split(" ")[0]);
				}
				String parameterReorderStr = StringUtils.join(parameters_Reorder, ",");
				String cSign = "+";
				if (classAccess.equals("private")) {
					cSign = "-";
				}
				grammar += cSign + " " + clsName + "(" + parameterReorderStr + ")" + "\n";
			}
			if (implements_List1.size() > 0)
			{
				for ( int k=0; k < method_Names.size(); k++){					
					for (int b=0; b< methods_List.size();b++){						
					HashMap<String, String> interfaceMenthodList = (HashMap<String, String>) methods_List.get(b);
					if (method_Names.contains(interfaceMenthodList.get("name"))){
						methods_List.remove(b);		
						continue;
						}
					}
				}
			}
			for (Object hm:methods_List) {
				HashMap<String, String> m = (HashMap<String, String>) hm;
				String methodName = m.get("name");
				String methodType = m.get("type");
				String methodAccess = m.get("access");
				String[] methodParameterTypes = m.get("parameterTypes").split(",");
				String[] mParameters = m.get("parameters").split(",");
				String[] uses = m.get("uses").split(",");
				ArrayList parameters_Reorder = new ArrayList();
				for (String p: mParameters) {
					if (p.equals("")) {
						continue;
					}
					String[] parameterTypeName = p.split(" ");
					String parameterType = parameterTypeName[0];
					String parameterName = parameterTypeName[1];
					parameters_Reorder.add(parameterName + ":" + parameterType);
					if (enum_Names.contains(parameterType)) {
						mainUMLGrammar += className + " ..> " + parameterType + "\n";
					}					
				}
				String parameterReorderStr = StringUtils.join(parameters_Reorder, ",");
				for (int b = 0; b < methodParameterTypes.length; b++) {
					if (interface_Names.contains(methodParameterTypes[b])) {
						usesClass.add(methodParameterTypes[b]);
					}
				}
				for (int b = 0; b < uses.length; b++) {
					if (interface_Names.contains(uses[b])) {
						usesClass.add(uses[b]);
					}
				}
				if ((methodName.startsWith("get") || methodName.startsWith("set")) && methodAccess.equals("public") && methods_List.contains(methodName) && field_Names.contains(methodName.substring(3).toLowerCase())) {
					continue;
				}
				String mSign = "+";
				if (methodAccess.equals("private") || methodAccess.equals("") || methodAccess.equals("package")) {
					continue;
				}				
				grammar += mSign + " " + methodName + "(" + parameterReorderStr + "):" + methodType + "\n";				
			}			
			for (int a = 0; a < fields_List.size(); a++) {
				HashMap<String, String> f = (HashMap<String, String>)fields_List.get(a);
				String field_Name = f.get("name");
				String field_Type = f.get("type");
				String field_Access = f.get("access");
				String oneOne = f.get("oneOne");
				String oneMany = f.get("oneMany");
				String fieldSign = "-";			
				if (field_Access.equals("public")) {
					fieldSign = "+";
				} 
				else if (field_Access.equals("protected")) {
					fieldSign = "~";
				}
				else if (field_Access.equals("") || field_Access.equals("package")){
					fieldSign = "#";
					field_Access = "package";
				}
				String tempMethod_Get = "get" + field_Name.toLowerCase();
				String tempMethod_Set = "set" + field_Name.toLowerCase();	
				if (((method_Names.contains(tempMethod_Get) || method_Names.contains(tempMethod_Set))) && field_Access.equals("private") && !field_Access.equals("package") && !field_Access.equals("")) {
					fieldSign = "-";
				}
				if (!field_Access.equals("package") && !field_Access.equals("") && !field_Type.startsWith("Collection<") && !field_Access.equals("protected") && !class_Names.contains(field_Type)) {
					grammar += fieldSign + " " + field_Name + ":" + field_Type + "\n";
				}								
				if (interface_Names.contains(field_Type)) {
					usesClass.add(field_Type);
				}
				if (class_Names.contains(oneOne) || interface_Names.contains(oneOne)) {
					dependencies.add(oneOne);
					if (onetoRelationship.containsKey(className)) {
						ArrayList temp = onetoRelationship.get(className);
						temp.add(oneOne);
						onetoRelationship.put(className, temp);
					} else {
						ArrayList temp = new ArrayList();
						temp.add(oneOne);
						onetoRelationship.put(className, temp);
					}
				}
				if (class_Names.contains(oneMany) || interface_Names.contains(oneMany)) {
					dependencies.add(oneMany);
					if (manytoRelationship.containsKey(className)) {
						ArrayList temp = manytoRelationship.get(className);
						temp.add(oneMany);
						manytoRelationship.put(className, temp);
					} else {
						ArrayList temp = new ArrayList();
						temp.add(oneMany);
						manytoRelationship.put(className, temp);
					}
				}
			}
			grammar += "}\n";
			for (int l = 0; l < extends_List1.size(); l++) {
				String e = (String) extends_List1.get(l);
				grammar += e + " <|-- " + className + "\n";
			}
			for (int l = 0; l < implements_List1.size(); l++) {
				String e = (String) implements_List1.get(l);
				grammar += e + " <|.. " + className + "\n";
			}
			for (String str:usesClass) {
				if (interface_Names.contains(className)) {
					continue;
				}
				grammar += className + " ..> " + str + "\n";
			}

			mainUMLGrammar = mainUMLGrammar + grammar;
		}

		for (Map.Entry<String, ArrayList> entry1 : manytoRelationship.entrySet()) {
			String key = entry1.getKey();
			ArrayList<String> value = entry1.getValue();

			for (String c: value) {
				if (manytoRelationship.containsKey(c) && manytoRelationship.get(c).contains(key)) {
					
					mainUMLGrammar += key + "\"*\" -- \"*\"" + c + "\n";
				} else if (onetoRelationship.containsKey(c) && onetoRelationship.get(c).contains(key)) {
					
					ArrayList<String> tmp = onetoRelationship.get(c);
					tmp.remove(key);
					onetoRelationship.put(c, tmp);
					mainUMLGrammar += key + "\"1\" -- \"*\"" + c + "\n";
				} else {
					
					mainUMLGrammar += key + "\"1\" -- \"*\"" + c + "\n";
				}
			}
		}

		ArrayList<String> visited = new ArrayList<String>();
		for (Map.Entry<String, ArrayList> entry : onetoRelationship.entrySet()) {
			String key1 = entry.getKey();
			ArrayList<String> value1 = entry.getValue();

			for (String c: value1) {
				
				if (visited.contains(key1 + "-" + c) || visited.contains(c + "-" + key1)) {
					continue;
				}
				mainUMLGrammar += key1 + "\"1\" -- \"1\"" + c + "\n";
				visited.add(key1 + "-" + c);
				visited.add(c + "-" + key1);
			}
		}

		mainUMLGrammar += "@enduml";		
		OutputStream png = null;
		try {
		
			png = new FileOutputStream(imageoutput);
			SourceStringReader reader = new SourceStringReader(mainUMLGrammar);
			String desc = reader.generateImage(png);
		}
		catch (FileNotFoundException e1) {
			System.out.println("File Not Found");
		}
		catch (IOException e) {
			System.out.println("IO Exception occured"); 
		}

	}


	
}
