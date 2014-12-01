/*
 * Created on 2006-1-17
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
import java.util.ArrayList;
import java.util.List;

import cn.ac.rcpa.bio.database.RcpaDatabaseType;
import cn.ac.rcpa.bio.database.gene.Gene2unigene;
import cn.ac.rcpa.bio.database.gene.Gene2unigeneDao;
import cn.ac.rcpa.bio.processor.IFileProcessor;
import cn.ac.rcpa.database.utils.ApplicationContextFactory;

public class Gene2unigeneImporter implements IFileProcessor {
  private Gene2unigeneDao dao;

  private Gene2unigeneDao getDao() {
    if (null == dao) {
      dao = (Gene2unigeneDao) ApplicationContextFactory.getContext(
          RcpaDatabaseType.ANNOTATION).getBean("Gene2unigeneDao");
    }
    return dao;
  }

  public List<String> process(String originFile) throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(originFile));

    getDao().deleteAll();

    String line;
    int count = 0;
    while (null != (line = br.readLine())) {
      if (0 == line.trim().length()) {
        break;
      }

      count++;
      if (count % 1000 == 0) {
        System.out.println(count);
      }

      String[] parts = line.split("\\s+");

      Gene2unigene ug = new Gene2unigene(Integer.parseInt(parts[0]), parts[1]);
      getDao().create(ug);
    }
    System.out.println(count);
    
    br.close();

    return new ArrayList<String>();
  }

  public static void main(String[] args) throws Exception {
    String file = "D:\\database\\gene\\Gene2unigene";
    new Gene2unigeneImporter().process(file);
  }
}
