package co.gargoyle.supercab.android.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value=Include.NON_NULL) 
public abstract class SuperCabBaseModel {
  
  @JsonProperty("_id")
  public String superCabId;

}
