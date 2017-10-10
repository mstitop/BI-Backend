package cn.edu.dbsi.olaprecommend.model;

public class Measure
{
  private String name;

  public Measure(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return this.name;
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
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
    if (!(obj instanceof Measure)) {
      return false;
    }

    Measure other = (Measure)obj;

    if (this.name == null) {
      if (other.name != null)
        return false;
    }
    else if (!this.name.equals(other.name)) {
      return false;
    }

    return true;
  }
}