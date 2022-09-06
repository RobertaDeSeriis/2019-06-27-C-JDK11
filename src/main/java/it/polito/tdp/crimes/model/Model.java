package it.polito.tdp.crimes.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	EventsDao dao; 
	List<String> categorie; 
	List<LocalDate> date;
	List<Adiacenza> vertici;
	Map<String, Adiacenza> idMap;
	Graph<Adiacenza, DefaultWeightedEdge> grafo;
	
	public Model() {
		this.dao= new EventsDao(); 
		
	}

	public List<String> getCategorie() {
		return dao.getCategorie();
	}

	public void setCategorie(List<String> categorie) {
		this.categorie = categorie;
	}

	public List<LocalDate> getDate() {
		return dao.getGiorni();
	}

	public void setDate(List<LocalDate> date) {
		this.date = date;
	}
	
	public String creaGrafo(String c, LocalDate d) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap= new HashMap<>();
		vertici=dao.getVertici(c, d, idMap);
		int peso=0;
		
		Graphs.addAllVertices(this.grafo,vertici);
		
		for (Adiacenza a: grafo.vertexSet()) {
			for (Adiacenza a1: grafo.vertexSet()) {
				if (a.getP()==a1.getP() && a.getT()!=a1.getT()) {
					peso++;
					if(peso>0)
					Graphs.addEdgeWithVertices(this.grafo, a, a1, peso);
				
				}
			}
		}
		
		return "Grafo creato con " +this.grafo.vertexSet().size()+ " vertici e "
		+ this.grafo.edgeSet().size()+ " archi";
	}
	
	
	
	
	
}
