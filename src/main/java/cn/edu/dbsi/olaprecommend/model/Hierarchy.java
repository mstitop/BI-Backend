package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hierarchy
{
  private static final int SECOND_LAST_PADDING = 2;
  private String name;
  private List<Level> levels = new ArrayList();
  Random rand = new Random();

  public Hierarchy(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public List<Level> getLevels()
  {
    return this.levels;
  }

  public Level getLevel(int number)
  {
    return (Level)this.levels.get(number);
  }

  public Level getLevel(String levelName)
  {
    for (Level lev : this.levels)
    {
      if (lev.getName().equals(levelName))
      {
        return lev;
      }
    }

    return null;
  }

  public int getLevelCount()
  {
    return this.levels.size();
  }

  public Level getRandomLevel(boolean allIncluded)
  {
    int range = allIncluded ? getLevelCount() : getLevelCount() - 1;

    return (Level)this.levels.get(this.rand.nextInt(range));
  }

  public void addLevel(Level level)
  {
    this.levels.add(0, level);
  }

  public int findPosition(String levelName)
  {
    int position = -1;

    for (int i = 0; i < this.levels.size(); i++)
    {
      String currentLevel = ((Level)this.levels.get(i)).getName();

      if (currentLevel.equals(levelName))
      {
        position = i;
        break;
      }
    }

    return position;
  }

  public boolean isAncestor(String level1, String level2)
  {
    int pos1 = findPosition(level1);
    int pos2 = findPosition(level2);

    if ((pos1 != -1) && (pos2 != -1) && (pos1 <= pos2))
    {
      return true;
    }

    return false;
  }

  public boolean isMaxLevel(String level)
  {
    return findPosition(level) == getLevelCount() - 1;
  }

  public void setLevelValues(List<String> valueList)
  {
    for (int i = 0; i < this.levels.size() - 1; i++)
    {
      ((Level)this.levels.get(i)).addDistinctValues((String)valueList.get(i));
    }
  }

  public int getValidPredicatePosition(int levelPosition)
  {
    Random rand = new Random();

    return 1 + levelPosition + rand.nextInt(getLevelCount() - levelPosition - 2);
  }
}