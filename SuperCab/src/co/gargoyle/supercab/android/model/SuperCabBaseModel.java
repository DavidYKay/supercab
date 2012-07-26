package co.gargoyle.supercab.android.model;

import co.gargoyle.supercab.android.model.json.CustomIdSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class SuperCabBaseModel {
  
  @JsonIgnore
  @JsonProperty("_id")
  @JsonSerialize(using = CustomIdSerializer.class)  
  public String superCabId;

}
