package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dimension
{
  private String name;
  private List<Hierarchy> hierarchies = new ArrayList();
  private boolean temporal;
  Random rand = new Random();

  public Dimension(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public List<Hierarchy> getHierarchies()
  {
    return this.hierarchies;
  }

  public Hierarchy getHierarchy(int number)
  {
    return (Hierarchy)this.hierarchies.get(number);
  }

  public Hierarchy getRandomHierarchy()
  {
    return (Hierarchy)this.hierarchies.get(this.rand.nextInt(getHierarchyCount()));
  }

  public int getHierarchyCount()
  {
    return this.hierarchies.size();
  }

  public void addHierarchy(Hierarchy hierarchy)
  {
    this.hierarchies.add(hierarchy);
  }

  public boolean isTemporal()
  {
    return this.temporal;
  }

  public void setTemporal(boolean value)
  {
    this.temporal = value;
  }
}