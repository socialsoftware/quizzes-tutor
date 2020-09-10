package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto;

import java.io.Serializable;
import java.util.List;

public class UnansweredDiscussionsDto implements Serializable {
    private Integer quantity;

    public UnansweredDiscussionsDto() {
    }

    public UnansweredDiscussionsDto(List<DiscussionDto> list){
        this.quantity = list.size();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
