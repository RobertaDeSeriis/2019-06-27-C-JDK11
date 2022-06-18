package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao;
	List<String> categorie; 
	Graph<String, DefaultWeightedEdge> grafo;
	private List<String> migliore;
	
	public Model()
	{
		dao = new EventsDao();
		this.categorie = dao.getCategorie();
	}

	public List<String> getCategorie() {
		return categorie;
	}
	
	public List<LocalDate> getDate()
	{
		return dao.getGiorni();
	}
	
	public String creaGrafo(String cat, LocalDate data)
	{
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(grafo, dao.getVertici(cat, data));
		
		for(Adiacenza a:dao.getArchi(cat, data))
		{
			Graphs.addEdgeWithVertices(grafo, a.getT1(), a.getT2(), a.getPeso());
		}
		
		
		return "#Vertici: " + this.grafo.vertexSet().size() + " #Archi: " + this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getArchiMaggMed()
	{
		double med;
		int max = 0;
		int min = 9999999;
		List<Adiacenza> magg = new LinkedList<Adiacenza>();
		
		for(DefaultWeightedEdge e:grafo.edgeSet())
		{
			if(this.grafo.getEdgeWeight(e) > max)
			{
				max = (int) this.grafo.getEdgeWeight(e);
			}
			
			if(this.grafo.getEdgeWeight(e) < min)
			{
				min = (int) this.grafo.getEdgeWeight(e);
			}
		}
		
		med = (max+min)/2;
		
		for(DefaultWeightedEdge e:grafo.edgeSet())
		{
			if(this.grafo.getEdgeWeight(e) > med)
			{
				magg.add(new Adiacenza(grafo.getEdgeSource(e), grafo.getEdgeTarget(e), (int)grafo.getEdgeWeight(e)));
			}
		}
		
		return magg;
	}
	
	public List<String> calcolaPercorso(String sorg, String dest)
	{
		migliore = new LinkedList<String>();
		List<String> parziale = new LinkedList<>();
		parziale.add(sorg);
		cercaRicorsiva(parziale, dest);
		return migliore;
	}

	private void cercaRicorsiva(List<String> parziale, String dest) {
		 
				//condizione di terminazione
				if(parziale.get(parziale.size()-1).equals(dest))
				{
					int pesoParziale = pesoTot(parziale);
					if(pesoParziale > pesoTot(migliore))//la strada piú lunga é la migliore
					{
						migliore = new LinkedList<>(parziale);
					}
					return;
				}
				
				for(String v:Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) //scorro sui vicini dell'ultimo nodo sulla lista
				{
					if(!parziale.contains(v))
					{
						parziale.add(v);
						cercaRicorsiva(parziale, dest);
						parziale.remove(parziale.size()-1);
					}
					
				}
		
	}

	private int pesoTot(List<String> parziale) {
		
		int peso = 0;
		int i = 0;
		for(DefaultWeightedEdge e: grafo.edgeSet())
		{
			while(i<parziale.size()-2)
			{
				if(grafo.getEdgeSource(e).compareTo(parziale.get(i))==0 && grafo.getEdgeSource(e).compareTo(parziale.get(i+1))==0)
				{
					peso += grafo.getEdgeWeight(e);
				}
				i++;
			}
		}
		return peso;
	}
	
	
	
}
