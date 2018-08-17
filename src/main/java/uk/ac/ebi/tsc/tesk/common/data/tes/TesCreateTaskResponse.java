package uk.ac.ebi.tsc.tesk.common.data.tes;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * CreateTaskResponse describes a response from the CreateTask endpoint.
 */
@ApiModel(description = "CreateTaskResponse describes a response from the CreateTask endpoint.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-07T14:45:12.993Z")

public class TesCreateTaskResponse   {
  @JsonProperty("id")
  private String id = null;

  public TesCreateTaskResponse id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Task identifier assigned by the server.
   * @return id
  **/
  @ApiModelProperty(value = "Task identifier assigned by the server.")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TesCreateTaskResponse tesCreateTaskResponse = (TesCreateTaskResponse) o;
    return Objects.equals(this.id, tesCreateTaskResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TesCreateTaskResponse {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

