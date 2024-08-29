package com.ganeshgc.ecommerce.exception;
import java.util.Map;

public record ErrorResponse (

        Map<String, String> e
){
}