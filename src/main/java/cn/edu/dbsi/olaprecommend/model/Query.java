package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Query
{
  private List<GroupByElement> groupBySet = new ArrayList();
  private List<Measure> measures = new ArrayList();
  private List<SelectionPredicate> predicates = new ArrayList();
  Random rand = new Random();

  public List<GroupByElement> getGroupBySet()
  {
    return this.groupBySet;
  }

  public List<Measure> getMeasures()
  {
    return this.measures;
  }

  public void setMeasures(List<Measure> measureList)
  {
    this.measures = measureList;
  }

  public List<SelectionPredicate> getPredicates()
  {
    return this.predicates;
  }

  public void addGroupByElement(String hierarchy, String level)
  {
    this.groupBySet.add(new GroupByElement(hierarchy, level));
  }

  public void addMeasure(Measure measure)
  {
    this.measures.add(measure);
  }

  public void addSelectionPredicate(SelectionPredicate selPred)
  {
    this.predicates.add(selPred);
  }

  public boolean containsPredicateOn(String hierarchy)
  {
    boolean present = false;

    for (SelectionPredicate sel : this.predicates)
    {
      if (sel.getHierarchy().equals(hierarchy))
      {
        present = true;
        break;
      }
    }

    return present;
  }

  public SelectionPredicate getSelectionPredicate(String hierarchy)
  {
    for (SelectionPredicate sel : this.predicates)
    {
      if (sel.getHierarchy().equals(hierarchy))
      {
        return sel;
      }
    }

    return null;
  }

  public String printQuery()
  {
    return buildGroupBy() + "\n" + buildSelectionPredicates() + "\n" + buildMeasures() + "\n";
  }

  private String buildGroupBy()
  {
    String finalString = "";

    for (int i = 0; i < this.groupBySet.size(); i++)
    {
      GroupByElement gb = (GroupByElement)this.groupBySet.get(i);

      if (gb.getVisible())
      {
        finalString = finalString + gb.getHierarchy() + "." + gb.getLevel();

        if (i != this.groupBySet.size() - 1)
        {
          finalString = finalString + ", ";
        }
      }
    }

    return finalString;
  }

  private String buildSelectionPredicates()
  {
    String finalString = "";

    for (int i = 0; i < this.predicates.size(); i++)
    {
      finalString = finalString + ((SelectionPredicate)this.predicates.get(i)).getSelectionPredicate();

      if (i != this.predicates.size() - 1)
      {
        finalString = finalString + ", ";
      }
    }

    return finalString;
  }

  private String buildMeasures()
  {
    String finalString = "";

    for (int i = 0; i < this.measures.size(); i++)
    {
      finalString = finalString + ((Measure)this.measures.get(i)).getName();

      if (i != this.measures.size() - 1)
      {
        finalString = finalString + ", ";
      }
    }

    return finalString;
  }

  public String findLevel(String hierarchy)
  {
    for (GroupByElement gb : this.groupBySet)
    {
      if (gb.getHierarchy().equals(hierarchy))
      {
        return gb.getLevel();
      }
    }

    return null;
  }

  public void setLevel(String hierarchy, String level)
  {
    for (GroupByElement gb : this.groupBySet)
    {
      if (gb.getHierarchy().equals(hierarchy))
      {
        gb.setLevel(level);
      }
    }
  }

  public boolean getVisibility(String hierarchy)
  {
    for (GroupByElement gb : this.groupBySet)
    {
      if (gb.getHierarchy().equals(hierarchy))
      {
        return gb.getVisible();
      }
    }

    return false;
  }

  private void setVisibility(String hierarchy, boolean value)
  {
    for (GroupByElement gb : this.groupBySet)
    {
      if (gb.getHierarchy().equals(hierarchy))
      {
        gb.setVisible(value);
      }
    }
  }

  public boolean isDescendible(Hierarchy hierarchy)
  {
    String level = findLevel(hierarchy.getName());
    int position = hierarchy.findPosition(level);

    return position != 0;
  }

  public void descendHierarchy(Hierarchy hierarchy)
  {
    String level = findLevel(hierarchy.getName());
    int position = hierarchy.findPosition(level);

    String newLevel = hierarchy.getLevel(--position).getName();
    setLevel(hierarchy.getName(), newLevel);
  }

  public boolean isAscendable(Hierarchy hierarchy)
  {
    boolean ascendable = false;
    String hierarchyName = hierarchy.getName();

    String queryLevel = findLevel(hierarchy.getName());
    int queryPosition = hierarchy.findPosition(queryLevel);

    if (hierarchy.isMaxLevel(queryLevel))
    {
      ascendable = false;
    }
    else if (containsPredicateOn(hierarchyName))
    {
      SelectionPredicate selPred = getSelectionPredicate(hierarchyName);

      String selectionLimit = selPred.getLevel();
      int predicatePosition = hierarchy.findPosition(selectionLimit);

      if (queryPosition < predicatePosition - 1)
      {
        ascendable = true;
      }
      else
      {
        ascendable = false;
      }
    }
    else
    {
      ascendable = true;
    }

    return ascendable;
  }

  public void ascendHierarchy(Hierarchy hierarchy)
  {
    String level = findLevel(hierarchy.getName());
    int position = hierarchy.findPosition(level);

    String newLevel = hierarchy.getLevel(++position).getName();
    setLevel(hierarchy.getName(), newLevel);
  }

  public void checkValidity(Cube cube)
  {
    List hieList = null;

    for (Dimension dim : cube.getDimensions())
    {
      if (dim.getHierarchyCount() == 1)
      {
        setVisibility(dim.getHierarchy(0).getName(), true);
      }
      else
      {
        hieList = dim.getHierarchies();

        for (int i = 0; i < hieList.size(); i++)
        {
          boolean isVisible = true;
          Hierarchy tempHierarchy = (Hierarchy)hieList.get(i);

          for (int j = 0; (j < hieList.size()) && (j != i); j++)
          {
            if (tempHierarchy.isAncestor(findLevel(((Hierarchy)hieList.get(i)).getName()), 
              findLevel(((Hierarchy)hieList.get(j)).getName())))
            {
              setVisibility(tempHierarchy.getName(), false);
              isVisible = false;
              break;
            }
          }

          if (isVisible)
          {
            setVisibility(tempHierarchy.getName(), true);
          }
        }
      }
    }
  }

  public int getReportSize(Cube cube)
  {
    int reportSize = 1;

    for (Hierarchy hie : cube.getHierarchies())
    {
      if (getVisibility(hie.getName()))
      {
        if (containsPredicateOn(hie.getName()))
        {
          SelectionPredicate selPred = getSelectionPredicate(hie.getName());
          String level = selPred.getLevel();
          int selectivePosition = hie.findPosition(level);
          int selectiveDistinctValues = hie.getLevel(selectivePosition).getValuesCount();

          String currentLevel = findLevel(hie.getName());
          int position = hie.findPosition(currentLevel);
          int distinctValues = hie.getLevel(position).getValuesCount();
          reportSize *= distinctValues / selectiveDistinctValues;
        }
        else
        {
          String currentLevel = findLevel(hie.getName());
          int position = hie.findPosition(currentLevel);
          reportSize *= hie.getLevel(position).getValuesCount();
        }
      }
    }

    return reportSize;
  }

  public int distanceFrom(Query query, Cube cube)
  {
    int distance = 0;

    for (GroupByElement gb : this.groupBySet)
    {
      String hierarchyName = gb.getHierarchy();

      if ((getVisibility(hierarchyName)) && 
        (query.getVisibility(hierarchyName)))
      {
        Hierarchy tempHie = cube.findHierarchy(hierarchyName);
        int position_1 = tempHie.findPosition(findLevel(hierarchyName));
        int position_2 = tempHie.findPosition(query.findLevel(hierarchyName));

        distance += Math.abs(position_1 - position_2);
      }

    }

    if (measuresMismatch(query.measures))
    {
      distance++;
    }

    if (predicatesMismatch(query))
    {
      distance++;
    }

    return distance;
  }

  private boolean measuresMismatch(List<Measure> measList)
  {
    for (Measure meas : this.measures)
    {
      if (!measList.contains(meas))
      {
        return true;
      }
    }

    return false;
  }

  private boolean predicatesMismatch(Query queryToMatch)
  {
    boolean different = false;
    List<SelectionPredicate> initialPredicates = new ArrayList<SelectionPredicate>();
    List finalPredicates = new ArrayList();
    String segregatedHierarchy = null;

    for (SelectionPredicate selPred : this.predicates)
    {
      initialPredicates.add(selPred);
    }

    for (SelectionPredicate selPred : queryToMatch.predicates)
    {
      finalPredicates.add(selPred);
    }

    if ((isSegregated()) && (!queryToMatch.isSegregated()))
    {
      for (SelectionPredicate sel : initialPredicates)
      {
        if (sel.getSegregation())
        {
          segregatedHierarchy = sel.getHierarchy();
          initialPredicates.remove(sel);
          break;
        }

      }

      if (queryToMatch.containsPredicateOn(segregatedHierarchy))
      {
        finalPredicates.remove(queryToMatch.getSelectionPredicate(segregatedHierarchy));
      }
    }

    if (initialPredicates.size() != finalPredicates.size())
    {
      different = true;
    }
    else
    {
      for (SelectionPredicate sel : initialPredicates)
      {
        String tempHierarchy = sel.getHierarchy();

        if (!queryToMatch.containsPredicateOn(tempHierarchy))
        {
          different = true;
          break;
        }

        SelectionPredicate tempSel = queryToMatch.getSelectionPredicate(tempHierarchy);

        if (!sel.matchesWith(tempSel))
        {
          different = true;
          break;
        }
      }

    }

    return different;
  }

  public boolean convergeTo(Query queryToConverge, Cube cube)
  {
    int choice = this.rand.nextInt(2);
    boolean modified = false; boolean equalMeasures = false; boolean equalGroupBy = false;

    if (!measuresMismatch(queryToConverge.measures))
    {
      choice = 0;
      equalMeasures = true;
    }

    List modifiableHierarchies = new ArrayList();

    for (Hierarchy hie : cube.getHierarchies())
    {
      if (!hierarchyMatch(queryToConverge, hie))
      {
        modifiableHierarchies.add(hie);
      }
    }

    if (modifiableHierarchies.size() == 0)
    {
      equalGroupBy = true;
      choice = 1;
    }

    if ((equalMeasures) && (equalGroupBy))
    {
      modified = false;
    }
    else if (choice == 0)
    {
      Hierarchy hie = (Hierarchy)modifiableHierarchies.get(this.rand.nextInt(modifiableHierarchies.size()));
      String hieName = hie.getName();

      String levelToSet = queryToConverge.findLevel(hieName);
      setLevel(hieName, levelToSet);

      if (containsPredicateOn(hieName))
      {
        SelectionPredicate currentPredicate = getSelectionPredicate(hieName);

        if (!currentPredicate.getSegregation())
        {
          this.predicates.remove(currentPredicate);

          if (queryToConverge.containsPredicateOn(hieName))
          {
            SelectionPredicate matchPredicate = queryToConverge.getSelectionPredicate(hieName);

            this.predicates.add(matchPredicate);
          }

        }

      }
      else if (queryToConverge.containsPredicateOn(hieName))
      {
        SelectionPredicate matchPredicate = queryToConverge.getSelectionPredicate(hieName);

        this.predicates.add(matchPredicate);
      }

      modified = true;
    }
    else
    {
      this.measures = queryToConverge.measures;
      modified = true;
    }

    return modified;
  }

  public boolean isPrompted()
  {
    for (SelectionPredicate sel : this.predicates)
    {
      if (sel.getPrompt())
      {
        return true;
      }
    }

    return false;
  }

  public boolean isSegregated()
  {
    for (SelectionPredicate sel : this.predicates)
    {
      if (sel.getSegregation())
      {
        return true;
      }
    }

    return false;
  }

  public boolean isSegregatedOn(Hierarchy hierarchy)
  {
    for (SelectionPredicate sel : this.predicates)
    {
      if ((sel.getHierarchy().equals(hierarchy.getName())) && (sel.getSegregation()))
      {
        return true;
      }
    }

    return false;
  }

  private void changeGroupBy(Cube cube)
  {
    List modifiableHierarchies = new ArrayList();

    for (Hierarchy hie : cube.getHierarchies())
    {
      if ((isAscendable(hie)) || (isDescendible(hie)))
      {
        modifiableHierarchies.add(hie);
      }
    }

    if (modifiableHierarchies.size() == 0)
    {
      return;
    }

    Hierarchy hie = (Hierarchy)modifiableHierarchies.get(this.rand.nextInt(modifiableHierarchies.size()));

    if (!isDescendible(hie))
    {
      ascendHierarchy(hie);
    }
    else if (!isAscendable(hie))
    {
      descendHierarchy(hie);
    }
    else if (this.rand.nextBoolean())
    {
      ascendHierarchy(hie);
    }
    else
    {
      descendHierarchy(hie);
    }

    checkValidity(cube);
  }

  private void changePredicates(Cube cube)
  {
    boolean addable = true; boolean removable = true;

    if ((this.predicates.isEmpty()) || (
      (this.predicates.size() == 1) && (((SelectionPredicate)this.predicates.get(0)).getSegregation())))
    {
      removable = false;
    }

    if (this.predicates.size() == cube.getTotalHierarchyCount())
    {
      addable = false;
    }

    List selectableHierarchies = new ArrayList();
    for (Hierarchy hie : cube.getHierarchies())
    {
      if (!containsPredicateOn(hie.getName()))
      {
        String level = findLevel(hie.getName());
        int position = hie.findPosition(level);

        if (position <= hie.getLevelCount() - 3)
        {
          selectableHierarchies.add(hie);
        }

      }

    }

    if (selectableHierarchies.size() == 0)
    {
      addable = false;
    }
    boolean choice;
    if ((removable) && (addable))
    {
      choice = this.rand.nextBoolean();
    }
    else
    {
      if ((removable) && (!addable))
      {
        choice = false;
      }
      else
      {
        if ((!removable) && (addable))
        {
          choice = true;
        }
        else
          return;
      }
    }
    if (choice)
    {
      Hierarchy hierarchy = (Hierarchy)selectableHierarchies.get(this.rand.nextInt(selectableHierarchies.size()));
      String queryLevel = findLevel(hierarchy.getName());
      int position = hierarchy.findPosition(queryLevel);
      Level level = hierarchy.getLevel(hierarchy.getValidPredicatePosition(position));

      this.predicates.add(new SelectionPredicate(hierarchy.getName(), 
        level.getName(), 
        level.getRandomValue(), 
        false, 
        false));
    }
    else
    {
      SelectionPredicate tempSel;
      do
      {
        tempSel = (SelectionPredicate)this.predicates.get(this.rand.nextInt(this.predicates.size()));
      }while (tempSel.getSegregation());

      this.predicates.remove(tempSel);
    }
  }

  private void changeMeasures(Cube cube)
  {
    Measure oldMeasure = (Measure)this.measures.get(this.rand.nextInt(this.measures.size()));
    this.measures.remove(oldMeasure);

    List measureList = new ArrayList();

    for (Measure meas : this.measures)
    {
      measureList.add(meas.getName());
    }

    Measure newMeasure;
    do
    {
      newMeasure = cube.getMeasure(this.rand.nextInt(cube.getMeasureCount()));
    }while ((newMeasure.getName().equals(oldMeasure.getName())) || 
      (measureList.contains(newMeasure.getName())));

    this.measures.add(newMeasure);
  }

  private boolean hierarchyMatch(Query queryToMatch, Hierarchy hierarchy)
  {
    boolean match = false;
    String hierarchyName = hierarchy.getName();
    int position_1 = hierarchy.findPosition(findLevel(hierarchyName));
    int position_2 = hierarchy.findPosition(queryToMatch.findLevel(hierarchyName));

    if ((containsPredicateOn(hierarchyName)) || 
      (queryToMatch.containsPredicateOn(hierarchyName)))
    {
      if ((containsPredicateOn(hierarchyName)) && 
        (queryToMatch.containsPredicateOn(hierarchyName)))
      {
        SelectionPredicate currentPredicate = getSelectionPredicate(hierarchyName);
        SelectionPredicate matchPredicate = queryToMatch.getSelectionPredicate(hierarchyName);

        if ((currentPredicate.getSegregation()) && (matchPredicate.getSegregation()))
        {
          match = position_1 == position_2;
        }
        else if (currentPredicate.getSegregation())
        {
          if (((position_1 < position_2) && (isAscendable(hierarchy))) || 
            (position_1 > position_2))
          {
            match = false;
          }
          else
          {
            match = true;
          }

        }
        else if ((position_1 == position_2) && (currentPredicate.matchesWith(matchPredicate)))
        {
          match = true;
        }
        else
        {
          match = false;
        }

      }
      else if ((containsPredicateOn(hierarchyName)) && 
        (getSelectionPredicate(hierarchyName).getSegregation()))
      {
        if (((position_1 < position_2) && (isAscendable(hierarchy))) || 
          (position_1 > position_2))
        {
          match = false;
        }
        else
        {
          match = true;
        }
      }
      else
      {
        match = false;
      }

    }
    else
    {
      match = position_1 == position_2;
    }

    return match;
  }

  public Query randomEvolution(Cube cube)
  {
    Query newQuery = clone(this);

    int choice = this.rand.nextInt(3);

    if (choice == 0)
    {
      newQuery.changeGroupBy(cube);
    }
    else if (choice == 1)
    {
      newQuery.changePredicates(cube);
    }
    else
    {
      newQuery.changeMeasures(cube);
    }

    return newQuery;
  }

  public static Query clone(Query queryToClone)
  {
    Query newQuery = new Query();

    for (GroupByElement gb : queryToClone.getGroupBySet())
    {
      newQuery.addGroupByElement(gb.getHierarchy(), gb.getLevel());
    }

    for (Measure meas : queryToClone.getMeasures())
    {
      newQuery.addMeasure(meas);
    }

    for (SelectionPredicate selPred : queryToClone.getPredicates())
    {
      newQuery.addSelectionPredicate(selPred);
    }

    return newQuery;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (
      this.groupBySet == null ? 0 : this.groupBySet.hashCode());
    result = 31 * result + (
      this.measures == null ? 0 : this.measures.hashCode());
    result = 31 * result + (
      this.predicates == null ? 0 : this.predicates.hashCode());
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Query)) {
      return false;
    }

    Query other = (Query)obj;

    if (this.groupBySet == null) {
      if (other.groupBySet != null)
        return false;
    }
    else {
      List thisGroupBySet = new ArrayList();
      List otherGroupBySet = new ArrayList();

      for (GroupByElement element : this.groupBySet) {
        if (element.getVisible()) {
          thisGroupBySet.add(element);
        }
      }
      for (GroupByElement element : other.groupBySet) {
        if (element.getVisible()) {
          otherGroupBySet.add(element);
        }
      }

      if ((!thisGroupBySet.containsAll(otherGroupBySet)) || (!otherGroupBySet.containsAll(thisGroupBySet))) {
        return false;
      }
    }

    if (this.measures == null) {
      if (other.measures != null)
        return false;
    }
    else if ((!this.measures.containsAll(other.measures)) || (!other.measures.containsAll(this.measures))) {
      return false;
    }

    if (this.predicates == null) {
      if (other.predicates != null)
        return false;
    }
    else if ((!this.predicates.containsAll(other.predicates)) || (!other.predicates.containsAll(this.predicates))) {
      return false;
    }
    return true;
  }
}