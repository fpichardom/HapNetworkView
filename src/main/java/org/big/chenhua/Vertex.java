package org.big.chenhua;

public class Vertex {

    public String id;
    public String name;
    public String []names;
    public boolean isIntermediate;
    public double weight;
    public double weight2;

    public Vertex(String str){
        int b=str.indexOf("frequency=");
        int c=str.indexOf('"', b);
        String []ss=str.substring(b+10, c).split(" ");
        this.weight=Double.parseDouble(ss[0].trim());
        this.weight2=this.weight;
        this.name=ss[1].trim();
        this.names=new String[ss.length-1];
        for(int i=1;i<ss.length;i++) this.names[i-1]=ss[i];
        this.isIntermediate=name.startsWith("IN") || name.startsWith("In");
        //--
        int d=str.indexOf(" id ");
        int e=str.indexOf(" ", d+4);
        if(e==-1) e=str.indexOf(']', d+4);
        this.id=str.substring(d+4, e).trim();
    }

    public Vertex(String id, String name, double weight){
        this.id=id;
        this.name=name;
        this.weight=weight;
        this.weight2=weight;
        this.isIntermediate=name.startsWith("IN") || name.startsWith("In");
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("Id="+this.id+"\n");
        sb.append("Name="+this.name+"\n");
        sb.append("Weight="+this.weight+"\n");
        if(this.isIntermediate) sb.append("IsIntermediate=true\n");
        else sb.append("IsIntermediate=false\n");
        return sb.toString();
    }

//    public String toString(){
//        return "node:"+name+"\t"+isIntermediate+"\t"+weight;
//    }

}
