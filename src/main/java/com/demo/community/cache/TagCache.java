package com.demo.community.cache;

import com.demo.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagCache{

    public static List<TagDTO> get(){
        List<TagDTO> tagDTOS = new ArrayList<>();

        TagDTO language = new TagDTO();
        language.setCategoryName("开发语言");
        language.setTags(Arrays.asList("js","php","html","java","python","c++","c#"));
        tagDTOS.add(language);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring", "express", "django","flask", "struts"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux", "ubuntu", "nginx","docker", "apache"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql", "redis", "sql","oracle", "memcached"));
        tagDTOS.add(db);

        return tagDTOS;
    }

    public static String filterInvalid(String tags){

        String[] split = StringUtils.split(tags, ',');
        List<String> tagList = get().stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
