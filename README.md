# Spring Boot and Spring Security OAuth2 Tiny Demo

[![Build Status](https://travis-ci.org/making/demo-oauth2.svg)](https://travis-ci.org/making/demo-oauth2)

## Run App

    $ mvn spring-boot:run

## Issue Access Token

    $ curl -X POST -u clientapp:123456  http://localhost:8080/oauth/token -d "grant_type=password&username=making&password=pass" | jq .
    
    {
      "scope": "read write",
      "expires_in": 43192,
      "refresh_token": "18208b3b-e6d8-4db5-b464-ffbff69f52ac",
      "token_type": "bearer",
      "access_token": "d768a840-965b-49e1-bf7e-4006727479cf"
    }

## Access API

    $ curl -X POST http://localhost:8080/api/v1/tweets\
      -H "Content-Type: application/json"\
      -H "Authorization: Bearer d768a840-965b-49e1-bf7e-4006727479cf"\
      -d '{"content":"Hello World"}' | jq .
    
    {
      "content": "Hello World",
      "uuid": "8b7774de-a118-49da-bfdc-fbcdba0a8083",
      "tweetedBy": "making"
    }
    
    $ curl -X POST http://localhost:8080/api/v1/tweets\
      -H "Content-Type: application/json"\
      -H "Authorization: Bearer d768a840-965b-49e1-bf7e-4006727479cf"\
      -d '{"content":"Hello OAuth2"}' | jq .
    
    {
      "content": "Hello OAuth2",
      "uuid": "3bd2f8b4-bc09-4fc8-82da-7d6dafac7d1a",
      "tweetedBy": "making"
    }
    
    $ curl -X GET http://localhost:8080/api/v1/tweets\
      -H "Authorization: Bearer d768a840-965b-49e1-bf7e-4006727479cf" | jq .
      
    [
      {
        "content": "Hello World",
        "uuid": "8b7774de-a118-49da-bfdc-fbcdba0a8083",
        "tweetedBy": "making"
      },
      {
        "content": "Hello OAuth2",
        "uuid": "3bd2f8b4-bc09-4fc8-82da-7d6dafac7d1a",
        "tweetedBy": "making"
      }
    ]

## End to End Test

    $ mvn test
