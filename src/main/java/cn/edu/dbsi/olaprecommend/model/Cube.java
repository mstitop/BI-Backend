package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cube
{
  private String name;
  private List<Dimension> dimensions = new ArrayList();
  private List<Measure> measures = new ArrayList();
  Random rand = new Random();

  public Cube(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public void addDimension(Dimension dimension)
  {
    this.dimensions.add(dimension);
  }

  public int getDimensionCount()
  {
    return this.dimensions.size();
  }

  public List<Dimension> getDimensions()
  {
    return this.dimensions;
  }

  public Dimension getDimension(int number)
  {
    return (Dimension)this.dimensions.get(number);
  }

  public Dimension getTemporalDimension()
  {
    for (Dimension dim : this.dimensions)
    {
      if (dim.isTemporal())
      {
        return dim;
      }
    }

    return null;
  }

  public Dimension getRandomDimension()
  {
    return (Dimension)this.dimensions.get(this.rand.nextInt(getDimensionCount()));
  }

  public int getMeasureCount()
  {
    return this.measures.size();
  }

  public void addMeasure(Measure measure)
  {
    this.measures.add(measure);
  }

  public List<Measure> getMeasures()
  {
    return this.measures;
  }

  public Measure getMeasure(int number)
  {
    return (Measure)this.measures.get(number);
  }

  public List<Measure> getRandomMeasures(int number)
  {
    List<Measure> tempList = new ArrayList<Measure>();

    while (tempList.size() < number)
    {
      Measure tempMeas = getRandomMeasure();
      boolean present = false;

      for (Measure meas : tempList)
      {
        if (meas.getName().equals(tempMeas.getName())) {
          present = true;
        }
      }
      if (!present) {
        tempList.add(tempMeas);
      }
    }
    return tempList;
  }

  private Measure getRandomMeasure()
  {
    return (Measure)this.measures.get(this.rand.nextInt(getMeasureCount()));
  }

  public Hierarchy findHierarchy(String hierarchy)
  {
    for (Hierarchy hie : getHierarchies())
    {
      if (hie.getName().equals(hierarchy))
      {
        return hie;
      }
    }

    return null;
  }

  public int getTotalHierarchyCount()
  {
    return getHierarchies().size();
  }

  public List<Hierarchy> getHierarchies()
  {
    List hierarchies = new ArrayList();

    for (Dimension dim : this.dimensions)
    {
      hierarchies.addAll(dim.getHierarchies());
    }

    return hierarchies;
  }

  public Hierarchy getRandomHierarchy()
  {
    return (Hierarchy)getHierarchies().get(this.rand.nextInt(getTotalHierarchyCount()));
  }

  public boolean isTemporalHierarchy(Hierarchy hierarchy)
  {
    for (Dimension dim : this.dimensions)
    {
      if ((dim.isTemporal()) && (dim.getHierarchies().contains(hierarchy)))
      {
        return true;
      }
    }

    return false;
  }
}