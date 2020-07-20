package pt.ulisboa.tecnico.socialsoftware.tutor.user.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UsersIdsDto implements Serializable {
    private List<Integer> usersIds;

    public UsersIdsDto(){
    }

    public UsersIdsDto (List<Integer> usersIds){
        this.usersIds = usersIds;
    }

    public List<Integer> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }
}
