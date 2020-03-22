package com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.post;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.coderstower.socialmediapubisher.springpublisher.main.aws.repository.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDateTime;

@Data
@DynamoDBTable(tableName = "Posts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDynamo {
    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String id;
    @DynamoDBAttribute
    private String name;
    @DynamoDBAttribute
    private String description;
    @DynamoDBAttribute
    private String tags;
    @DynamoDBAttribute
    private URL url;
    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    private LocalDateTime publishedDate;
}
