# dropwizard-what-to-do
This is a simple example app using dropwizard and hibernate to manage todo lists via api calls. 


## Prerequisites:

Make sure you have already 

* docker installed
* curl installed
* a running unix shell

## Setup

To build and create example data run the following in a shell:
  
    git clone https://github.com/nstoya/dropwizard-what-to-do.git
    cd dropwizard-what-to-do
    ./setup
    
## Start

Start the application via

    docker-compose up
  
The application should be running on http://localhost:8080 and you should be able to send requests. There is already some sample data.

    curl -X GET 'http://localhost:8080/todos' -H 'Authorization: Bearer test-token'
    
If everything is fine you should get:

    [
        {
            "id": 1,
            "name": "Vor dem Urlaub",
            "description": "Vor Reiseantritt",
            "tasks": [
                {
                    "id": 2,
                    "name": "Gültigkeit Reisepässe"
                },
                {
                    "id": 3,
                    "name": "Gepäckbestimmungen",
                    "description": "für Lufthansa"
                }
            ]
        },
        {
            "id": 4,
            "name": "Vor Abreise",
            "description": "Am Tag selbst",
            "tasks": [
                {
                    "id": 5,
                    "name": "Müll entsorgen"
                },
                {
                    "id": 6,
                    "name": "Stecker ziehen"
                }
            ]
        }
    ]
    
## Authentication

To use the application you need to authenticate. The application uses OAuth 2.0 for this purpose. For every request a valid
Berarer token must be provided in the `Authorization` header of the request. Since this is an example application only 
one user exists. The needed token is configured in the config-prod.yml file in the property `authToken`.

If the token is not valid a http 401 (Unauthorized) error code is returned.

## Endpoints

### `GET /todos` 

Returns a list of all todos and their tasks. For this endpoint paging is provided. Please check chapter **Paging And
 Total Count**.

**Return codes**

| Code  | Description  |
|---|---|
| 200  | OK |  

**Example**

        curl -X GET 'http://localhost:8080/todos' -H 'Authorization: Bearer test-token'

### `POST /todos`  

Creates a todo and returns it.

**Required data**

The data required is a json object with the following properties:

| Name  | Type  | Required  | 
|---|---|---|
| name  | string   | yes  | 
| description  | string  | no |
| tasks  | JSON Array  | no |

The maximum tasks size is 25. The tasks array consist of objects with the following properties:

| Name  | Type  | Required  |
|---|---|---|
| name  | string   | yes  | 
| description  | string  | no |


**Return codes**

| Code  | Description  |
|---|---|
| 201  | Created |
| 422  | Mandatory data was not provided or trying to create too many tasks  |  

**Example**

    curl -X POST 'http://localhost:8080/todos' \
    -H 'Authorization: Bearer test-token' \
    -H 'Content-Type: application/json' \
    --data-raw '{
        "name": "Monat vor Ulaub",
        "tasks": [
            {
                "name": "Packliste erstellen"
            },
            {
                "name": "Zahlungsmittel",
                "description": "Kreditkarte und Bar"
            }
        ]
    }'


### `PUT /todos/{id}`

Updates the todo with the given `id`. If the element `tasks` is not provided, then it is not changed. If `tasks` is
 provided as an empty json array, all existing tasks are deleted. All tasks are replaced by the new tasks if the
  tasks array is provided in the request body.   

**Parameters**

| Name  | Type  | Value type  | Description |
|---|---|---|---|
| id  | path | int | The id of an existing todo. | 

**Required data**

See required data for endpoint `POST /todos`. 

**Return codes**

| Code  | Description  |
|---|---|
| 200  | Returns the updated todo with the given id |
| 404  | The todo with the given id was not found |
| 422  | Mandatory data was not provided or trying to create too many tasks |  

**Example**

    curl -X PUT 'http://localhost:8080/todos/1' \
    -H 'Authorization: Bearer test-token' \
    -H 'Content-Type: application/json' \
    --data-raw '{
            "id": 1,
            "name": "Vor dem Urlaub",
            "description": "6 Wochen vorher",
            "tasks": [
                {
                    "name": "Gültigkeit Reisepässe"
                },
                {
                    "name": "Gepäckbestimmungen",
                    "description": "für Lufthansa"
                },
                {
                    "name": "Impfen lassen"
                }
            ]
        }'

### `GET  /todos/{id}`

Returns the todo with the given `id`.

**Parameters**

| Name  | Type  | Value type  | Description |
|---|---|---|---|
| id  | path | int | The id of an existing todo. | 


**Return codes**

| Code  | Description  |
|---|---|
| 200  | Returns the todo with the given id |
| 404  | The todo with the given id was not found |

**Example**

    curl -X GET 'http://localhost:8080/todos/1' -H 'Authorization: Bearer test-token'

### `DELETE  /todos/{id}`

Deletes the todo with the given `id`. 

**Parameters**

| Name  | Type  | Value type  | Description |
|---|---|---|---|
| id  | path | int | The id of an existing todo. | 


**Return codes**

| Code  | Description  |
|---|---|
| 204  | The todo with the given `id` was sucessfully deleted or it doesn't exist. |
     

**Example**

    curl -X DELETE 'http://localhost:8080/todos/6' -H 'Authorization: Bearer test-token' 

## Paging And Total Count

 Retrieving data may result in a high number of entries. That is why the results are capped to a default of 100 in
  several pages. The number of the first page is  which is also the page default. The pageSize default is 100. It can
   be set to the maximum value of 1000. 
   
Information about paging and total count is provided in the `Link` and `X-Total-Count` headers.
   
The paging parameters are as follows:
   
| Name  | Type  | Value type |
|---|---|---|
| page  | query   | int |  
| page_size  | query  | int |

Example: 

    curl --location --request GET 'http://localhost:8080/todos?page=2&page_size=7' --header 'Authorization: Bearer test-token'

## Further work

* Provide search
* CRUD endpoints for tasks
* Paging for tasks
* Move authentication data to DB
    * User table
    * todos per user
    * Endpoint POST /user for creating new user 
    * endpoint POST /token to get the Bearer token
* Generate entity classes from dropwizard migration files or hibernate mappings
* use https
* handle CORS
