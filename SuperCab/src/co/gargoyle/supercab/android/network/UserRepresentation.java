package co.gargoyle.supercab.android.network;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import co.gargoyle.supercab.android.model.UserModel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;

//public class UserRepresentation extends JacksonRepresentation<ApiResponse<UserModel>> {
public class UserRepresentation extends JacksonRepresentation<UserModel> {

  public UserRepresentation(Representation jacksonRepresentation) throws Exception {
    super(jacksonRepresentation, UserModel.class);
    ObjectMapper mapper = getObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public Optional<UserModel> getUser() {
    UserModel user = getObject();
    if (user == null) {
       return Optional.absent();
    } else {
      return Optional.of(user);
    }
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
