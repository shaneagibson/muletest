# muletest

A simple example of a MuleSoft ESB flow, and subsequent JUnit component test. 

The flow performs the following operations:

1. Receive inbound HTTP request on http://localhost:8081/*
2. Filter out any 'favicon.ico' requests
3. Extract the 'language' query parameter and respond as follows:
  - for 'Spanish', language respond with "Hola!"
  - for 'French' language respond with "Bonjour!"
  - for 'English' language respond with "Hello!"
  - for an unknown language:
    - log an error
    - post an error to an error HTTP service (running on http://localhost:9000/error)
    - default the language to 'English' (therefore respond with "Hello!") 
4. Log the response
