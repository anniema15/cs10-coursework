//Annie Ma
//Creates the Kevin Bacon game
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map; 
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import net.datastructures.Edge;
import net.datastructures.NamedAdjacencyMapGraph;
import net.datastructures.Vertex;

public class Bacon{

	//store the pathnames as strings
	public static String actorFile= "/Users/Annie/Documents/workspace/Lab 3/src/actors.txt"; 
	public static String movieFile= "/Users/Annie/Documents/workspace/Lab 3/src/movies.txt"; 
	public static String movieActorFile= "/Users/Annie/Documents/workspace/Lab 3/src/movie-actors.txt"; 

	//read in the files
	public static List<String> readFiles(String fileName) throws IOException{
		//Scanner input=new Scanner (new File(fileName));
		BufferedReader input = new BufferedReader(new FileReader(fileName));
		List<String> fileList= new ArrayList<String>();
		String L = input.readLine();
		while (L != null) {
			fileList.add(L);
			L = input.readLine();
		}

		input.close();
		return fileList; 
	}

	//build the Bacon Graph
	public static NamedAdjacencyMapGraph<String, String> baconGraph(String actorFile, String movieFile, String movieActorFile, NamedAdjacencyMapGraph<String, String> baconGraph) throws IOException{

		//read in the data as lists
		List<String> actorList= readFiles(actorFile); 
		List<String> movieList= readFiles(movieFile); 
		List<String> movieActorList=readFiles(movieActorFile); 

		//empty maps for the data
		Map<String, String> actorMap= new HashMap<String, String>(); 
		Map<String, String> movieMap= new HashMap<String, String>(); 
		Map<String, ArrayList<String>> movieActorMap= new HashMap<String, ArrayList<String>>(); 


		//fill the actor map and insert the vertices
		for (String actor: actorList){

			//break down the string to parts
			int pipeIndex=actor.indexOf("|"); 

			//get id and name
			String id= actor.substring(0, pipeIndex); 
			String name= actor.substring(pipeIndex+1, actor.length()); 

			//add to map
			actorMap.put(id, name); 

			//add vertex
			baconGraph.insertVertex(name); 
		}

		//fill the movieMap
		for (String movie: movieList){

			//break string into parts and get ID and name
			int pipeIndex=movie.indexOf("|"); 
			String id=movie.substring(0, pipeIndex); 
			String name= movie.substring(pipeIndex+1, movie.length()); 

			//create empty ArrayLIst at each ID in movieActor
			movieActorMap.put(id, new ArrayList<String>()); 

			//fill the MovieMap
			movieMap.put(id, name); 
		}

		//fill the movieActor map
		for (String movieActor: movieActorList){

			//break down string and get id/name
			int pipeIndex= movieActor.indexOf("|"); 
			String movieID=movieActor.substring(0, pipeIndex); 
			String actorID= movieActor.substring(pipeIndex+1, movieActor.length());

			//get the arrayList at current movie
			ArrayList<String> currentMovie= movieActorMap.get(movieID); 

			//add the actor
			currentMovie.add(actorID); 

		}

		//create the graph
		for (String movieID: movieActorMap.keySet()){
			ArrayList<String> actors= movieActorMap.get(movieID);
			String movieName= movieMap.get(movieID); 

			//add edges for every connected actor with the movie as the edge
			for (int i=0; i<actors.size()-1; i++){
				for (int j= i+1; j<actors.size(); j++){
					try{
						baconGraph.insertEdge(actorMap.get(actors.get(i)), actorMap.get(actors.get(j)), movieName); 
					}
					catch(Exception e){

					}
				}
			}
		}

		//return the completed graph
		return baconGraph; 
	}

	//create the bfs
	public static NamedAdjacencyMapGraph<String, String> bfs(NamedAdjacencyMapGraph<String, String> baconGraph) throws IOException{
		//empty bfs tree
		NamedAdjacencyMapGraph<String, String> bfs=new NamedAdjacencyMapGraph<String, String>(true); 

		//insert the root
		bfs.insertVertex("Kevin Bacon"); 

		// This queue will have vertices of the baconGraph.
		Queue<Vertex<String>> queue = new LinkedList<Vertex<String>>();

		//add the root to the queue
		queue.add(baconGraph.getVertex("Kevin Bacon")); 

		//run the bfs while queue is not empty 
		while (!queue.isEmpty()){

			//get the last item
			Vertex<String> current = queue.poll(); 
			//for all outgoing edges on the current vertex
			for (Edge<String> edge : baconGraph.outgoingEdges(current)){

				//get the opposite vertex
				Vertex<String> nextVertex= baconGraph.opposite(current, edge);
				//if the vertex isn't in the bfs tree
				if (!bfs.vertexInGraph(nextVertex.getElement())){
					//add it to the queue
					queue.add(nextVertex); 

					//insert the next vertex and an edge to the current from the next
					bfs.insertVertex(nextVertex.getElement()); 
					bfs.insertEdge(nextVertex.getElement(), current.getElement(), edge.getElement()); 
				}
			}		
		}

		//return the search tree
		return bfs; 
	}

	//find the path to the root
	public static ArrayList<String> traverse(String actor, NamedAdjacencyMapGraph<String, String> bfs, NamedAdjacencyMapGraph<String, String> baconGraph ){
		//create an array list for the path
		ArrayList<String> path = new ArrayList<String>();

		//actor is not in database
		if (!baconGraph.vertexInGraph(actor)){
			System.out.println(actor+ " is not in database."); 
		}
		//actor is not connected to bacon
		else if(!bfs.vertexInGraph(actor)){
			System.out.println(actor+ " has infinite Bacon number."); 
		}
		//otherwise, find bacon number
		else {
			//get the current vertex on given actor
			Vertex<String> current= bfs.getVertex(actor); 

			//iterator on the outgoing edge
			Iterator<Edge<String>> outEdge= bfs.outgoingEdges(current).iterator();

			//while we're not at the root
			while (outEdge.hasNext()){

				//get the edge from the iterator
				Edge<String> nextEdge= outEdge.next(); 
				//get the opposite vertex
				Vertex<String> nextVertex=bfs.opposite(current, nextEdge);

				//add to the path
				path.add(current.getElement() + " appeared in "+ nextEdge.getElement()+ " with "+ nextVertex.getElement() ); 

				//advance the current vertex and the iterator
				current=nextVertex; 
				outEdge=bfs.outgoingEdges(current).iterator(); 


			}
			//print out the bacon number and path 
			System.out.println(actor+ " has Bacon number " +path.size()); 
			for (String casting: path){
				System.out.println(casting); 
			}
		}
		return path;



	}

	//main method
	public static void main (String [] args) throws IOException{
		//create the graphs
		try{
			NamedAdjacencyMapGraph<String, String> bacon= new NamedAdjacencyMapGraph<String, String>(false); 
			NamedAdjacencyMapGraph<String, String> bfs= new NamedAdjacencyMapGraph<String, String>(true); 

			bacon= baconGraph(actorFile, movieFile, movieActorFile, bacon); 
			bfs= bfs(bacon); 

			//take in user input repeatedly with a prompt and return the bacon number
			Scanner input=new Scanner (System.in); 

			String actor; 
			while (true){
				System.out.println("Enter the name of an actor: "); 
				actor=input.nextLine(); 
				traverse(actor, bfs, bacon); 

			}

		}
		//catch the IO exception
		catch(IOException e){
			System.out.println("Invalid Path");
		}
	}

}