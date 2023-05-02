# Arbiter Client
Version: **v2.0**  
Updated: 2023-05-01

## Table of Contents
- [Packages & Classes](#packages-and-classes)
  - [Auth](#auth)
    - [Session](#session)

## Packages and Classes
Below are listed all classes introduced in Arbiter Client, grouped by package and in alphabetical order.  
There are a total number of 1 packages and 1 classes

### Auth
This package contains the following 1 classes: [Session](#session)

#### Session
Represents a local user-session

##### Details
- Fully **versioned**
- Uses 2 database **indices**: `created`, `deprecated_after`

##### Properties
Session contains the following 3 properties:
- **User Id** - `userId: Int` - Id of the logged-in user
  - Refers to *user* from another module
- **Created** - `created: Instant` - Time when this session was opened
- **Deprecated After** - `deprecatedAfter: Option[Instant]` - Time when this session was closed
