package cn.edu.dbsi.olaprecommend.model;

public class GroupByElement
{
  private String hierarchy;
  private String level;
  private boolean visible;

  public GroupByElement(String hierarchy, String level)
  {
    this.hierarchy = hierarchy;
    this.level = level;
    this.visible = true;
  }

  public String getHierarchy()
  {
    return this.hierarchy;
  }

  public String getLevel()
  {
    return this.level;
  }

  public void setLevel(String level)
  {
    this.level = level;
  }

  public boolean getVisible()
  {
    return this.visible;
  }

  public void setVisible(boolean value)
  {
    this.visible = value;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (
      this.hierarchy == null ? 0 : this.hierarchy.hashCode());
    result = 31 * result + (this.level == null ? 0 : this.level.hashCode());
    result = 31 * result + (this.visible ? 1231 : 1237);
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
    if (!(obj instanceof GroupByElement)) {
      return false;
    }

    GroupByElement other = (GroupByElement)obj;

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

    if (this.visible != other.visible) {
      return false;
    }

    return true;
  }
}