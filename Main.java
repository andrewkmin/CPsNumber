import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static String overallBase = "https://www.basketball-reference.com/";
	private static String baseSite = "https://www.basketball-reference.com/players/";
	private static HashMap<String, String> playersToLinks = new HashMap<>();
	private static HashMap<String, HashSet<String>> graph = new HashMap<>(); // map from player to all teammates
	private static HashMap<String, HashSet<String>> roster = new HashMap<>();
	static int total = 0;
	
	public static String linesToString(ArrayList<String> lines) {
		StringBuilder build = new StringBuilder("");
		for (String line : lines) {
			build.append(line + "\n");
			
		}
		return build.toString();
	}
	
	public static void extractPlayers() {
		
		char letter = 'a';
		while (letter <= 'z') {
			if (letter == 'x') {
				letter +=1;
				continue;
			}
			URLGetter playerList = new URLGetter(baseSite+ letter +"/");
			ArrayList<String> playerLines = playerList.getContents();
			String lines = linesToString(playerLines);
			String pattern = "<th.*?data-stat=\"player\" ><strong><a href=\"/players/(.*?)\">(.*?)</a></strong>.*?data-stat=\"year_min\" >(.*?)</td>";
			Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
			Matcher m = p.matcher(lines);
			while (m.find()){
				int year = Integer.parseInt(m.group(3));
				if (year > 2009) {
					continue;
				}
				playersToLinks.put(m.group(2), m.group(1));
			}
			letter += 1;
		}
		playersToLinks.remove("Josh Childress");
	}
	
	
	public static void buildGraph() {
		for (String player : playersToLinks.keySet()) {
			String link = playersToLinks.get(player);
			String goTo = baseSite + link;
			URLGetter page = new URLGetter(goTo);
			ArrayList<String> lines = page.getContents();
			String text = linesToString(lines);
			HashSet<String> teams = getTeams(text);
			HashSet<String> teammates = getTeammates(teams);
			graph.put(player, teammates);
		}
	}
	
	public static HashSet<String> getTeammates(HashSet<String> teams) {
		HashSet<String> teammates = new HashSet<>();
		for (String team : teams) {
			if (roster.get(team) != null) {
				teammates.addAll(roster.get(team));
			}
			else {
				//need to get teammates from online
				String link = overallBase + team;
				URLGetter teamPage = new URLGetter(link);
				ArrayList<String> lines = teamPage.getContents();
				String text = linesToString(lines);
				
				String pattern = "<tr.*?data-stat=\"player\".*?<a href=\"/players.*?>(.*?)</a>";
				Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
				Matcher m = p.matcher(text);
				//find every teammate
				while (m.find()) {
					String found = m.group(1);
					if (playersToLinks.containsKey(found)) {
						teammates.add(found);
					}
				}
				roster.put(team, teammates);
			}
		}
		return teammates;
		
	}
	
	public static HashSet<String> getTeams(String text) {
		HashSet<String> toReturn = new HashSet<>();
		
		String pattern2016 = "<tr id=\"per_game.2016\".*?<a href=\"/(teams/.*?.html)";
		Pattern p = Pattern.compile(pattern2016, Pattern.DOTALL);
		Matcher m = p.matcher(text);
		if (m.find()) {
			toReturn.add(m.group(1));
			total += 1;
		}
		
		String pattern2017 = "<tr id=\"per_game.2017\".*?<a href=\"/(teams/.*?.html)";
		Pattern p2 = Pattern.compile(pattern2017, Pattern.DOTALL);
		Matcher m2 = p2.matcher(text);
		if (m2.find()) {
			toReturn.add(m2.group(1));
			total += 1;
		}
		
		String pattern2018 = "<tr id=\"per_game.2018\".*?<a href=\"/(teams/.*?.html)";
		Pattern p3 = Pattern.compile(pattern2018, Pattern.DOTALL);
		Matcher m3 = p3.matcher(text);
		if (m3.find()) {
			toReturn.add(m3.group(1));
			total += 1;
		}
		
		return toReturn;
	}
	
	public static void pruneGraph() {
		for (Map.Entry<String, HashSet<String>> entry : graph.entrySet()) {
			String player = entry.getKey();
			// remove player from his own teammate set
			entry.getValue().remove(player);
		}
	}
	
	private static void doBFS(String node, String target, int hopDistance) {
		// queue for bfs implementation
		Queue<String> queue = new ArrayDeque<String>();
		
		// set of visited nodes
		HashSet<String> bfsVisited = new HashSet<String>();
		
		// map of player names to distances representing how many hops away the 
		// player is from our source (node arg.)
		HashMap<String, Integer> distances = new HashMap<String, Integer>();
		
		// the path taken to each node in the graph
		HashMap<String, LinkedList<String>> parents = new HashMap<String, LinkedList<String>>();
		
		bfsVisited.add(node);
		queue.add(node);
		LinkedList<String> nodeParent = new LinkedList<String>();
		nodeParent.add(node);
		parents.put(node, nodeParent);
		distances.put(node, 0);
		
		while (queue.size() > 0) {
			String curr = queue.poll();
			HashSet<String> teammates = graph.get(curr);
			LinkedList<String> pathToCurr = parents.get(curr);
			for (String entry : teammates) {
				if (!bfsVisited.contains(entry)) {
					int currDistance = distances.get(curr) != null ? distances.get(curr) : 0;
					distances.put(entry, currDistance + 1);
					bfsVisited.add(entry);
					queue.add(entry);
					LinkedList<String> parentsOfEntry = new LinkedList<String>();
					for (int i = 0; i < pathToCurr.size(); i++) {
						parentsOfEntry.add(pathToCurr.get(i));
					}
					parentsOfEntry.add(entry);
					parents.put(entry, parentsOfEntry);
				}
			}
		}
		
		System.out.println("Distance from " + node + " to " + target + ": " + distances.get(target));
		LinkedList<String> targetParents = parents.get(target);
		System.out.println("The path taken from " + node + " to " + target + " was: " + targetParents);
		
		int count = 0;
		int max = 0;
		for (Map.Entry<String, Integer> entry : distances.entrySet()) {
		    int value = entry.getValue();
		    if (value > max) {
		    		max = value;
		    }
		    if (value <= hopDistance) { // manipulate this to see how many players are certain # hops away from our source
		    		count++;
		    }
		}
		
		// # players who are <= hopDistance hops away from source node
		System.out.println("Number of players who are " + hopDistance + " away from source: " + count);
		
		// max number of hops between our source and any player
		System.out.println("Maximum distance from " + node + " to every player: " + max);
	}
	
	
	public static void main(String[] args) {
		extractPlayers();
		buildGraph();
		pruneGraph();
		
		// player 1 is source, player 2 is destination
		String player1 = "Udonis Haslem";
		String player2 = "Ersan Ilyasova";
		
		// the number of players within numHops from the source
		int numHops = 2;
		
		doBFS(player1, player2, numHops);
	}

}
