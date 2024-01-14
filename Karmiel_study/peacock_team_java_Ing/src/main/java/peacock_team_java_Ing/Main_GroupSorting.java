package peacock_team_java_Ing;


import java.io.IOException;
import java.nio.file.Files;
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
	
	private final static String FILEPATH = "src/main/resources/lng-4.txt";
	private final static String REGEX="^(\"\\d*\")(;\"\\d*\")*$";
	

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		

		Set<Long[]> dataRead = readNumbersFromFile(FILEPATH);
		
		
		
		
		if(dataRead.isEmpty()) {
			System.out.println("Данные не найдены");
		}
		
		Set<Map<Long, Set<Long[]>>> unGroupMatches= findMatches(dataRead);
	
		List<Set<Long[]>> groups= groupMatches(unGroupMatches);
		
		print(groups);
		System.out.println(System.currentTimeMillis()- start);


	}
	
	private static void print(List<Set<Long[]>> groups) {
		//Rewrite to file save
		 System.out.println("Групп размера больше 1: " + groups.stream().filter(s -> s.size() > 1).count());
         groups.sort(Comparator.comparingInt(s -> -s.size()));
         int i = 0;
         for (Set<Long[]> group : groups) {
             i++;
             System.out.println("\nГруппа " + i);
             for (Long[] val : group) {
                 System.out.println(val);
             }
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
	                                    .skip(maxSize)
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
		//найдем максимально длинную строку
		return dataRead.stream().mapToInt(d ->d.length).max().orElse(0);
	}

	private static Set<Long[]> readNumbersFromFile(String filePath) {
		 Set<Long[]> numberSet = new HashSet<>();
		try(Stream<String> lines = Files.lines(Paths.get(filePath))) {
			
			
			numberSet= lines.filter(l ->{
				 Pattern pattern = Pattern.compile(REGEX);
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
					
		} catch (IOException e) {
			
			e.printStackTrace();
			//дописать догтрование ошибки чтения файла
		}
		return numberSet;
	}


	
}
