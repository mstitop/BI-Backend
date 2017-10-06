package cn.edu.dbsi.olaprecommend.model;

public class SelectionPredicate
{
  private String hierarchy;
  private String level;
  private String element;
  private boolean yearPrompt;
  private boolean segregationPredicate;

  public SelectionPredicate(String hierarchy, String level, String element, boolean yearPrompt, boolean segregationPredicate)
  {
    this.hierarchy = hierarchy;
    this.level = level;
    this.element = element;
    this.yearPrompt = yearPrompt;
    this.segregationPredicate = segregationPredicate;
  }

  public String getSelectionPredicate()
  {
    String selPred = this.hierarchy.toUpperCase() + 
      "." + 
      this.level.toUpperCase() + 
      " = " + 
      this.element.toUpperCase();

    return selPred;
  }

  public String getHierarchy()
  {
    return this.hierarchy;
  }

  public String getLevel()
  {
    return this.level;
  }

  public String getElement()
  {
    return this.element;
  }

  public boolean getPrompt()
  {
    return this.yearPrompt;
  }

  public boolean getSegregation()
  {
    return this.segregationPredicate;
  }

  public boolean matchesWith(SelectionPredicate sel)
  {
    if ((getHierarchy().equals(sel.getHierarchy())) && 
      (getLevel().equals(sel.getLevel())) && 
      (getElement().equals(sel.getElement())) && 
      (getPrompt() == sel.getPrompt()) && 
      (getSegregation() == sel.getSegregation()))
    {
      return true;
    }

    return false;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.element == null ? 0 : this.element.hashCode());
    result = 31 * result + (
      this.hierarchy == null ? 0 : this.hierarchy.hashCode());
    result = 31 * result + (this.level == null ? 0 : this.level.hashCode());
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
    if (!(obj instanceof SelectionPredicate)) {
      return false;
    }

    SelectionPredicate other = (SelectionPredicate)obj;

    if (this.element == null) {
      if (other.element != null)
        return false;
    }
    else if (!this.element.equals(other.element)) {
      return false;
    }

    if (this.hierarchy == null) {
      if (other.hierarchy != null)
        return false;
    }
    else if (!this.hierarchy.equals(other.hierarchy)) {
      return false;
    }

    if (this.level == null) {
      if (other.level != null)
        return false;
    }
    else if (!this.level.equals(other.level)) {
      return false;
    }

    return true;
  }
}