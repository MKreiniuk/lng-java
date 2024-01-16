package peacock_team_java_Ing;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;



public class Main_GroupSorting {
	
	
	
	
	private static String FILEPATH= "";
	

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		FILEPATH = args[0];
		
		matchingAndGrouping(FILEPATH);
		
		System.out.printf("Общее время выполнения команды: %.3f секунд ",(System.currentTimeMillis()- start)*0.001);


	}
	
	private static void matchingAndGrouping(String path) {
		   BufferedReader reader;
		
		   
		
		  try {
			reader = new BufferedReader(new FileReader(path));
			 List<Set<String>> groups = new ArrayList<>();
			 List<Map<String, Integer>> connections = new ArrayList<>();

	          String line = reader.readLine();
	          while(line != null) {
	        	  String [] row = getRows(line);
	        	  Integer groupNumber = null; 
	        	  for (int i = 0; i < Math.min(connections.size(), row.length); i++) {
	                    Integer groupNumber2 = connections.get(i).get(row[i]);
	                    if (groupNumber2 != null) {
	                        if (groupNumber == null) {
	                            groupNumber = groupNumber2;
	                        } else if (!Objects.equals(groupNumber, groupNumber2)) {
	                            for (String line2 : groups.get(groupNumber2)) {
	                                groups.get(groupNumber).add(line2);
	                                apply(getRows(line2), groupNumber, connections);
	                            }
	                            groups.set(groupNumber2, new HashSet<>());
	                        }
	                    }
	                }
	        	  if (groupNumber == null) {
	                    if (Arrays.stream(row).anyMatch(s -> !s.isEmpty())) {
	                        groups.add(new HashSet<>(List.of(line)));
	                        apply(row, groups.size() - 1, connections);
	                    }
	                } else {
	                    groups.get(groupNumber).add(line);
	                    apply(row, groupNumber, connections);
	                }
	                line = reader.readLine();
	        		 	
				}
	          reader.close();
	          
	          print(groups);
	        	  
	          
		} catch (Exception e) {
			System.out.println("Ошибка чтения файла");
			e.printStackTrace();
		}
         
		
	}

	private static void apply(String[] rows, Integer groupNumber, List<Map<String, Integer>> connections) {
		 for (int i = 0; i < rows.length; i++) {
	            if (rows[i].isEmpty()) {
	                continue;
	            }
	            if (i < connections.size()) {
	                connections.get(i).put(rows[i], groupNumber);
	            } else {
	                HashMap<String, Integer> map = new HashMap<>();
	                map.put(rows[i], groupNumber);
	                connections.add(map);
	            }
	        }
		
	}

	private static String[] getRows(String line) {
		 for (int i = 1; i < line.length() - 1; i++) {
	            if (line.charAt(i) == '"' && line.charAt(i - 1) != ';' && line.charAt(i + 1) != ';') {
	                return new String[0];
	            }
	        }
	        return line.replaceAll("\"", "").split(";");
	}

	private static void print(List<Set<String>> groups) {
		
		String answerPath = FILEPATH.replace(".txt", "-result.txt");
		Path outPath = Path.of(answerPath);
		
		try {
			Files.createFile(outPath);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Ошибка создания файла ответа");
		}
		
		try (FileOutputStream fos = new FileOutputStream(outPath.toFile());
	             PrintStream out = new PrintStream(fos)) {
			long groupNum= groups.stream().filter(s -> s.size() > 1).count();
			
			 out.println("Ощеее количество групп: " + groupNum);
	         groups.sort(Comparator.comparingLong(s -> -s.size()));
	         int i = 0;
	         for (Set<String> group : groups) {
	             i++;
	             
	             if (i<= groupNum) {
					out.println("\nГруппа " + i);
					for (String line : group) {
						out.println(line);
					} 
				}
	         }

	        } catch (FileNotFoundException e) {
	            System.out.println(e.getMessage());
	            System.err.println("Файл не найден");
	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	            System.err.println("Ошибка записи в файл");
	        }
		
		
	
    
		
	}


	
}
