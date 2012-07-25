package co.gargoyle.supercab.android.network;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import co.gargoyle.supercab.android.model.ApiResponse;
import co.gargoyle.supercab.android.model.UserModel;

//public class UserRepresentation extends JacksonRepresentation<ApiResponse<UserModel>> {
public class UserRepresentation extends JacksonRepresentation<ApiResponse> {

  public UserRepresentation(Representation jacksonRepresentation) throws Exception {
    super(jacksonRepresentation, ApiResponse.class);
//    super(jacksonRepresentation, ApiResponse.class);
  }

  public UserModel getUser() {
    ApiResponse<UserModel> response = getObject();
    UserModel user = response.objects[0];
    return user;
  }

  //    @Override
  //    public GraphHandler createBuilder(Graph graph) {
  //        this.foafHandler = new FoafHandler();
  //        return foafHandler;
  //    }
  //
  //    public List<FoafContact> getFriends() {
  //        if (this.foafHandler != null) {
  //            return this.foafHandler.getFriends();
  //        }
  //        return null;
  //    }

}
