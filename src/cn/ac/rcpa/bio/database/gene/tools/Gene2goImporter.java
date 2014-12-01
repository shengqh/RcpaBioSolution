/*
 * Created on 2006-1-18
 *
 *                    Rcpa development code
 *
 * Author Sheng QuanHu(qhsheng@sibs.ac.cn / shengqh@gmail.com)
 * This code is developed by RCPA Bioinformatic Platform
 * http://www.proteomics.ac.cn/
 *
 */
package cn.ac.rcpa.bio.database.gene.tools;

import java.io.BufferedReader;
import java.io.FileReader;

import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.database.gene.Gene2go;
import cn.ac.rcpa.bio.database.gene.Gene2goDao;
import cn.ac.rcpa.database.utils.ApplicationContextFactory;

public class Gene2goImporter {
  private static Gene2goDao dao;
  private static Gene2goDao getDao(){
   if (null == dao){
     dao = (Gene2goDao)ApplicationContextFactory.getContext(RcpaDatabaseType.ANNOTATION).getBean("Gene2goDao"); 
   }
   return dao;
  }
  
  public static void main(String[] args) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(
        "D:\\database\\gene\\Gene2go"));

    getDao().deleteAll();
    
    //Skip first line
    br.readLine();

    String line;
    int count = 0;
    while (null != (line = br.readLine())) {
      if (0 == line.trim().length()) {
        break;
      }

      String[] parts = line.split("\\t");
      if (parts.length < 7){
        break;
      }

      count ++;
      if (count % 1000 == 0){
        System.out.println(count);
      }
      
      Gene2go gg = new Gene2go();
      
      gg.setTaxId(Integer.parseInt(parts[0]));
      gg.setGeneId(Integer.parseInt(parts[1]));
      gg.setGoId(parts[2]);
      gg.setEvidence(parts[3]);
      gg.setGoQualifier(parts[4]);
      gg.setGoDescription(parts[5]);
      gg.setPipeSeparatedListOfPubmedId(parts[6]);
      
      getDao().create(gg);
    }
    
    System.out.println(count);
    
    br.close();
  }
}
