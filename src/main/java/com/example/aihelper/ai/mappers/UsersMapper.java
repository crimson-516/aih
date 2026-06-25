package com.example.aihelper.ai.mappers;


import org.apache.ibatis.annotations.Param;


public interface UsersMapper<T,P> extends BaseMapper<T,P>{
    T selectByName(@Param("userName")String userName);
    T login(@Param("query")P p);

}
