package peacock_team_java_Ing;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main_GroupSorting {
	
	
	
	private final static String REGEX="^(\"\\d*\")(;\"\\d*\")*$";
	private static String FILEPATH= "";
	

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		FILEPATH = args[0];
		
		Set<Long[]> dataRead = readNumbersFromFile(FILEPATH);
		
		
		if(dataRead.isEmpty()) {
			System.out.println("Данные не найдены");
		}
		
		Set<Map<Long, Set<Long[]>>> unGroupMatches= findMatches(dataRead);
	
		List<Set<Long[]>> groups= groupMatches(unGroupMatches);
		
		print(groups);
		System.out.printf("Общее время выполнения команды: %.3f секунд ",(System.currentTimeMillis()- start)*0.001);


	}
	
	private static void print(List<Set<Long[]>> groups) {
		
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
			
			 out.println("Ощеее количество групп: " + groups.stream().count());
	         groups.sort(Comparator.comparingLong(s -> -s.size()));
	         int i = 0;
	         for (Set<Long[]> group : groups) {
	             i++;
	             out.println("\nГруппа " + i);
	             for (Long[] line : group) {
	             for(Long l : line) {
	            	 out.print(l+" ");
	            	
	             }
	            out.println();
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

	private static List<Set<Long[]>> groupMatches(Set<Map<Long, Set<Long[]>>> unGroupMatches) {
		List<Set<Long[]>> groups = new ArrayList<>();
		
		for(Map<Long, Set<Long[]>> map: unGroupMatches) {
			for(Long key: map.keySet()) {
				Set<Long[]> temp = map.get(key);
				boolean isAdded = false;
					for(Set<Long[]> group: groups) {
						for(Long[] line: temp) {
							if(group.contains(line)) {
								group.addAll(temp);
								isAdded= true;
								break;
							}
						}
					}
				if(!isAdded) {
					groups.add(temp);
				}
					
				
			}
		}
		
		
		return groups;
	}

	private static Set<Map<Long, Set<Long[]>>> findMatches(Set<Long[]> dataRead) {
		Set<Map<Long, Set<Long[]>>> matches = new HashSet<>();
		int maxSize = longestLine(dataRead);
		int size = 0;
		
		
		while(size <= maxSize) {
			int point= size;
			long[] temp = dataRead.stream()
					.flatMapToLong(l -> LongStream.of(Arrays.stream(l)
							.skip(point).findFirst().orElse(0L)))
					.toArray();
			
			   Set<Long> clear = new HashSet<>();
	            Set<Long> copies = new HashSet<>();
	            for (long l : temp) {
	                if (l != 0 && !clear.add(l)) {
	                    copies.add(l);
	                }
	            }

	            Map<Long, Set<Long[]>> mapWithMatches = dataRead.stream()
	                    .filter(l -> Arrays.stream(l)
	                            .skip(point)
	                            .limit(1)
	                            .anyMatch(copies::contains))
	                    .collect(Collectors.groupingBy(
	                            s -> Arrays.stream(s)
	                                    .skip(point)
	                                    .limit(1)
	                                    .findFirst().orElse(0L),
	                            Collectors.toSet()
	                    ));
	            matches.add(mapWithMatches);
	            size++;
		}
		return matches;
	}

	private static int longestLine(Set<Long[]> dataRead) {
		
		return dataRead.stream().mapToInt(d ->d.length).max().orElse(0);
	}

	private static Set<Long[]> readNumbersFromFile(String filePath) {
		 Set<Long[]> numberSet = new HashSet<>();
		 Pattern pattern = Pattern.compile(REGEX);
		try(Stream<String> lines = Files.lines(Paths.get(filePath))) {
			
			
			numberSet= lines.filter(l ->{
				Matcher matcher = pattern.matcher(l);
                return matcher.matches();
			})	.filter(l -> l.length()>2)
					.distinct()
					.map(l -> l.replace("\"\"", "0"))
                    .map(l -> l.replace("\"", ""))
                    .map(l->l.trim())
                    .map(l -> Arrays.stream(l.split(";"))
                    		.map(Long::valueOf)
                    		.toList()
                    		.toArray(Long[]::new))
                    .collect(Collectors.toSet());                                 
					
		} catch (IOException  e ) {
			
			e.printStackTrace();
	System.err.println("Ошибка чтения файла");
		}catch(NumberFormatException e ) {
			e.printStackTrace();
			System.err.println("Ошибка преобразования файла");
		}
		
		return numberSet;
	}


	
}
