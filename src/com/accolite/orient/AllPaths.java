package com.accolite.orient;

import java.util.ArrayList;
import java.util.List;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class AllPaths {

    private boolean stop=false;
    private Vertex vertexFrom=null;
    private List<Vertex> vertexPreviousStep=new ArrayList<Vertex>();
    private List<Vertex> vertexCurrently=new ArrayList<Vertex>();
    private List<List<Vertex>> paths=new ArrayList<List<Vertex>>();
    private OrientGraph g;

    public AllPaths(OrientGraph g) {
        this.g=g;
    } 

    public List<List<Vertex>> getPaths(String ridFrom, String ridTo) {
        if(!check(ridFrom,ridTo))
            return paths;
        vertexPreviousStep.add(vertexFrom);
        List<Vertex> p=new ArrayList<Vertex>();
        p.add(vertexFrom);
        paths.add(p);
        int step=1;
        do{
            stop=false;
            for(Vertex v: vertexPreviousStep){
                List<Vertex> toAdd=new ArrayList<Vertex>();
                Iterable<Vertex> nodes = (Iterable<Vertex>) v.getVertices(Direction.OUT);
                for(Vertex v1:nodes){
                    toAdd.add(v1);
                    if(!v1.getId().toString().equals(ridTo))  
                        vertexCurrently.add(v1);
                }
                if(toAdd.size()!=0)
                    setPaths(v,toAdd,step);
            }
            change();
            step++;
        }while(stop==true);
        return cleanPaths(ridTo);
    }

    private boolean check(String ridFrom,String ridTo) {
        boolean findFrom=false,findTo=false;
        for(Vertex v:g.getVertices()){
            if(v.getId().toString().equals(ridFrom)){
                findFrom=true;
                vertexFrom=v;
            }
            else if(v.getId().toString().equals(ridTo))
                findTo=true;
        }
        if(findFrom==false || findTo==false)
            return false; 
        return true;
    }

    public void change(){
        vertexPreviousStep.clear();
        for(Vertex v:vertexCurrently)
            vertexPreviousStep.add(v);
        vertexCurrently.clear();
    }

    private void setPaths(Vertex previousVertex,List<Vertex> toAdd,int step) {
        for(int i=0;i<paths.size();i++){
            List<Vertex> list=paths.get(i);
            Vertex last=list.get(list.size()-1);
            if(last.getId().toString().equals(previousVertex.getId().toString()) && list.size()==step){  
                int j=0;
                for(Vertex v1:toAdd){
                    boolean vertexFound=false;
                    for(Vertex v2:list){
                        if(v2.getId().toString().equals(v1.getId().toString()))
                            vertexFound=true;   
                    }
                    if(vertexFound==false){
                        List<Vertex> listVertex=new ArrayList<Vertex>();
                        for(Vertex p:list)
                            listVertex.add(p);
                        listVertex.add(v1);
                        if(j==0){
                            stop=true;
                            paths.set(i,listVertex);
                            j++;
                        }
                        else
                            paths.add(listVertex);
                    }
                }
            }
        }   
    }

    public List<List<Vertex>> cleanPaths(String ridTo){
        for(int i=0;i<paths.size();i++){
            List<Vertex> list=paths.get(i);
            if(!list.get(list.size()-1).getId().toString().equals(ridTo)){
                paths.remove(i);
                i--;
            }
        }
        return paths;
    }
}
