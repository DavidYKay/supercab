package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;

@JsonInclude(value=Include.NON_NULL) 
public abstract class SuperCabBaseModel {
  
  @JsonIgnore
  @DatabaseField(generatedId=true)
  public int id;
  
  @JsonProperty("_id")
  @DatabaseField
  public String superCabId;

}
