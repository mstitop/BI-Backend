package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level
{
  private String name;
  private List<String> values = new ArrayList();
  Random rand = new Random();

  public Level(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public int getValuesCount()
  {
    return this.values.size();
  }

  public List<String> getValues()
  {
    List tempList = new ArrayList();

    for (String s : this.values)
    {
      tempList.add(s);
    }

    return tempList;
  }

  public void addDistinctValues(String value)
  {
    if (!this.values.contains(value))
    {
      this.values.add(value);
    }
  }

  public String getRandomValue()
  {
    return (String)this.values.get(this.rand.nextInt(getValuesCount()));
  }

  public boolean isLeaf(Hierarchy hierarchy)
  {
    return hierarchy.findPosition(this.name) == hierarchy.getLevelCount() - 1;
  }

  public boolean isRoot(Hierarchy hierarchy)
  {
    return hierarchy.findPosition(this.name) == 0;
  }
}