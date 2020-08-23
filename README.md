# dropwizard-what-to-do
This is a simple example app using dropwizard and hibernate to manage todo lists via api calls. 

## Getting started

Prerequisites:

Make sure you have already installed

* docker installed
* an sh shell

Checkout the project
 
    git clone https://github.com/nstoya/dropwizard-what-to-do.git
    
Build and prepare:

    run script
    
Run

    cd directory
    docker-compose up
    
## Authentication

To use the application you need to authenticate. The application uses OAuth 2.0 for this purpose. For every request a valid
Berarer token must be provided in the `Authorization` header of the request. Since this is an example application only 
one user exists. The needed token is configured in the config-prod.yml file in the property `authToken`.

If the token is not valid a http 401 (Unauthorized) error code is returned.

## Error Codes

|   |   |   |   |   |
|---|---|---|---|---|
|   |   |   |   |   |
|   |   |   |   |   |
|   |   |   |   |   |

## Endpoints

* `GET /todos` 

    Returns a list of all todos and their tasks.
    
    Parameters

    | Name  | Type  | Value type  | Example  |
    |---|---|---|---|
    | page  | query   | int  | 2  | 
    | page_size  | query  | int  |2  |

* `POST /todos`  
    DELETE  /todos/{id} 
    GET     /todos/{id} 
    PUT     /todos/{id} 

* parameters
* accepts
* returns
* codes 

## Paging

## Examples

## Further work

* Provide search
* CRUD endpoints for tasks
* Paging for tasks
* Move authentication data to DB
    * User table
    * ToDos per user
    * Endpoint POST /user for creating new user 
    * endpoint /token for users to get the Bearer token
* Generate entity classes from dropwizard migration files or hibernate mappings

## Todo
* logging config
* readme