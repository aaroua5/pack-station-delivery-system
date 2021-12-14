package com.ase.boxservice.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "boxes")
public class Box {

    @Id
    @Field("id")
    private String id;

    @Field("name")
    private String name;

    @Field("status")
    private BoxStatus status;

    @Field("address")
    private BoxAddress address;

}

