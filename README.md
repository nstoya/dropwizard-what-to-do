# dropwizard-what-to-do
This is a small example app with dropwizard and hibernate.

## Further work

* Move authentication data to DB
    * User table
    * Endpoint POST /user for creating new user 
    * endpoint /token for users to get the Bearer token
* Generate entity classes from dropwizard migration files or hibernate mappings

## TODO

* Tests to check, that the code makes sure there the primary key and name are provided and get checked.
+ Test make sure parent exist