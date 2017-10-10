package cn.edu.dbsi.olaprecommend.model;

import java.util.ArrayList;
import java.util.List;

public class Session
{
  private String profileName;
  private int progressive;
  private List<Query> queryList = new ArrayList();
  private String templateName;

  public Session(String profileName, int progressive, String templateName)
  {
    this.profileName = profileName;
    this.progressive = progressive;
    this.templateName = templateName;
  }

  public String getProfileName()
  {
    return this.profileName;
  }

  public int getProgressive()
  {
    return this.progressive;
  }

  public List<Query> getQueryList()
  {
    return this.queryList;
  }

  public String getTemplateName()
  {
    return this.templateName;
  }

  public void setQueryList(List<Query> queryList)
  {
    this.queryList = queryList;
  }
}