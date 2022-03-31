package io.metersphere.track.issue.domain.coding;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodingIssue {
    private String expand;
    private String id;
    private String self;
    private String key;
    private JSONObject fields;
}
