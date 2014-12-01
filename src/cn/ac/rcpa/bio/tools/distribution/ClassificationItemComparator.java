package cn.ac.rcpa.bio.tools.distribution;

import java.util.Comparator;

import cn.ac.rcpa.bio.tools.distribution.option.ClassificationItem;

public class ClassificationItemComparator implements Comparator<ClassificationItem>{
  public ClassificationItemComparator() {
  }

  /**
   * equals
   *
   * @param obj Object
   * @return boolean
   */
  @Override
  public boolean equals(Object obj) {
    return false;
  }

  /**
   * compare
   *
   * @param o1 Object
   * @param o2 Object
   * @return int
   */
  public int compare(ClassificationItem item1, ClassificationItem item2) {
    return item1.getClassifiedName().compareTo(item2.getClassifiedName());
  }
}
