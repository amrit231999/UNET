package test;


//Initial Template for Java
import java.util.*;

class dop
{
	public static void main(String[] args) {
        int[][] graph ={{0,10,15,20,40,60},
        				{10,0,35,25,45,65},
        				{15,35,0,30,15,23},
        				{20,25,30,0,15,15},
        				{40,45,15,15,0,67},
        				{60,65,23,15,67,0}};
        
        boolean visited[] = new boolean[graph.length];
        TreeMap<Integer,LinkedList<Integer>> hm = new TreeMap<>();
        LinkedList<Integer> ll = new LinkedList<>(); 
        findMin(0,visited,graph,0,hm,0,ll);
        int min = hm.firstKey();
    	System.out.print(min+" : ");
    	int[] shortestPath = new int[hm.size()];
    	int i = 0;
    	while(!hm.get(min).isEmpty()) {
    		int temp = hm.get(min).remove();
    		System.out.print(temp+" ");
    		shortestPath[i++] = temp;
    	}
    	System.out.println();
    }

	private static void findMin(int c, boolean[] visited, int[][] graph, int f,TreeMap<Integer,LinkedList<Integer>> hm, int sum,LinkedList<Integer> ll) {
		
		visited[c] = true;
		ll.add(c);
		boolean flag = true;
		
		for(int i = 0; i<visited.length; i++) {
			if(!visited[i]) {
				flag = false;
				break;
			}
		}
		
		if(flag) {
			ll.add(f);
			LinkedList<Integer> temp = new LinkedList<>();
			temp.addAll(ll);
			sum += graph[f][c];
			hm.put(sum, temp);
			ll.removeLast();
			ll.removeLast();
			visited[c] = false;
			return;
		}
		
		for(int i = 0;i<graph.length; i++) {
			if(!visited[i]) {
				findMin(i,visited,graph,f,hm,sum+graph[c][i],ll);
			}
		}
		
		ll.removeLast();
		visited[c] = false;
	}
    
}
