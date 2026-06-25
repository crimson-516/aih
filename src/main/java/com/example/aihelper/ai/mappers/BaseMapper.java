package com.example.aihelper.ai.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T,P> {
    //查询所有
    List<T> selectList(@Param("query")P p);
    //根据集合查询数量
    Integer selectCount(@Param("query")P p);
    Integer insert(@Param("bean")P p);
    Integer insertOrUpdate(@Param("bean")P p);
    Integer insertBatch(@Param("list") List<T> list);
    Integer insertOrUpdateBatch(@Param("list") List<T> list);
    Integer updateByParam(@Param("bean") T t,@Param("query") P p);
    Integer deleteByParam(@Param("query") P p);
}
