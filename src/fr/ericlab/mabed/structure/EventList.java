////////////////////////////////////////////////////////////////////////////////
//  This file is part of MABED.                                               //
//                                                                            //
//  MABED is free software: you can redistribute it and/or modify             //
//  it under the terms of the GNU General Public License as published by      //
//  the Free Software Foundation, either version 3 of the License, or         //
//  (at your option) any later version.                                       //
//                                                                            //
//  MABED is distributed in the hope that it will be useful,                  //
//  but WITHOUT ANY WARRANTY; without even the implied warranty of            //
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             //
//  GNU General Public License for more details.                              //
//                                                                            //
//  You should have received a copy of the GNU General Public License         //
//  along with MABED.  If not, see <http://www.gnu.org/licenses/>.            //
////////////////////////////////////////////////////////////////////////////////

package fr.ericlab.mabed.structure;

import fr.ericlab.mabed.algo.MABED;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 *   @author Adrien GUILLE, ERIC Lab, University of Lyon 2
 *   @email adrien.guille@univ-lyon2.fr
 */
public class EventList {
    public LinkedList<Event> list;
    
    public EventList(){
        list = new LinkedList<>();
    }
    
    public void writeEventsToFile(Corpus dataset, String filename){
        try {
            File textFile = new File("output/"+filename+".txt");
            FileUtils.writeStringToFile(textFile,"",false);
            for(Event event : list){
                FileUtils.writeStringToFile(textFile,"   - ["+new SimpleDateFormat("yyyy-MM-dd hh:mm").format(dataset.toDate(event.I.timeSliceA))+"//"+new SimpleDateFormat("yyyy-MM-dd hh:mm").format(dataset.toDate(event.I.timeSliceB))+
                        "] "+event.toString(false)+"\n---------------------------------\n",true);
            }
        } catch (IOException ex) {
            Logger.getLogger(MABED.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
        
    public void scoreEvolution(){
        for(int i = 1; i <= list.size(); i++){
            System.out.print(i+",");
        }
        for(Event event : list){
            System.out.print(event.score+",");
        }
    }
    
    public void printLatex(Corpus corpus){
        System.out.println(toLatex(corpus));
    }
    
    public String toLatex(Corpus corpus){
        int rank = 1;
        String string = "";
        for(Event topic : list){
            string += rank+" & "+new SimpleDateFormat("dd/MM HH:mm").format(corpus.toDate(topic.I.timeSliceA))+" -- "+new SimpleDateFormat("dd/MM HH:mm").format(corpus.toDate(topic.I.timeSliceB))+" & "+topic.mainTerm+": "+topic.relatedTerms.toString().replace("related terms:","")+"\\\\ \\hline\n";
            rank++;
        }
        return string;
    }
    
    public void add(Event t){
        list.add(t);
    }
    
    public void sort(){
        Collections.sort(list);
    }
    
    public int size(){
        return list.size();
    }
    
    public Event get(int i){
        return list.get(i);
    }
    
    public void addAll(EventList tl){
        list.addAll(tl.list);
    }
    
    public void exportDetailledResults(Corpus corpus){
        File outputDir = new File("output/csv/");
        if(!outputDir.isDirectory()){
            outputDir.mkdir();
        }else{
            for(String filename : outputDir.list()){
                FileUtils.deleteQuietly(new File("output/csv/"+filename));
            }
        }
        NumberFormat formatter = new DecimalFormat("000");
        for(int i = 0; i < list.size(); i++){
            Event event = list.get(i);
            String mainTerm = event.mainTerm.replace(", ", "_");
            File descFile = new File("output/csv/"+formatter.format(i)+"-"+mainTerm+".desc");
            File wordsFile = new File("output/csv/"+formatter.format(i)+"-"+mainTerm+".words");
            File seriesFile = new File("output/csv/"+formatter.format(i)+"-"+mainTerm+".anomaly");            
            try {
                FileUtils.writeStringToFile(descFile,event.score+"\t"+event.I.timeSliceA+"\t"+event.I.timeSliceB+"\t"+new SimpleDateFormat("YYYY-MM-dd HH:mm").format(corpus.toDate(event.I.timeSliceA))+"\t"+new SimpleDateFormat("YYYY-MM-dd HH:mm").format(corpus.toDate(event.I.timeSliceB))+"\n",true);
            } catch (IOException ex) {
                Logger.getLogger(EventList.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(WeightedTerm wt : event.relatedTerms.list){
                try {
                    FileUtils.writeStringToFile(wordsFile,wt.term+"\t"+wt.weight+"\n",true);
                } catch (IOException ex) {
                    Logger.getLogger(EventList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            for(int j = 0; j < event.anomaly.size(); j++){
                try {
                    FileUtils.writeStringToFile(seriesFile,j+"\t"+event.anomaly.get(j)+"\n",true);
                } catch (IOException ex) {
                    Logger.getLogger(EventList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
