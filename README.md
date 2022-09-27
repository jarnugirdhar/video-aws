# Basic Video Streaming Platform

### Basic Requirements
Java 11+ for building the project source.
To run the project, you will need to setup two system environment variables:
```
AWS_ACCESS_KEY_ID=;
AWS_SECRET_ACCESS_KEY=;
```
You can obtain access keys for bucket from me.

**NOTE:** *Instead of a disk-based database, currently application temporarily persists in JVM for the
sake of quick prototyping. However, there are alternative implementations which persist in MySQL are available too.*
                           
### Demo URL

**NOTE:** To perform health check, click on the above link.

### API Description

All the APIs are described below and the corresponding collection can be found in root directory for project.

#### Authentication

**Sign-up**
```
Endpoint: /auth/signup
Type: POST
Request Body: name, email, password
Content-Type: application/json
```

**Verify Signup**
```
Endpoint: /auth/verify
Type: GET
Params: token
```

**Login**
```
Endpoint: /auth/login
Type: POST
Content-Type: application/json
Request Body: email, password
Response: token
Verification Required: true
```

**Upload**
```
Endpoint: /video/upload
type: POST
Param: name, thumbnail
Content-Type: multipart/form-data
Body: file
Authentication Required: true
```

**List files**
```
Endpoint: /video/list
type: GET
Param: pageSize (Optional, default 10)
       page (Optional, default 1)
Response Body:
       message
       status
       data: [ (name, thumbnail) ]
Authentication Required: true
```

**Stream video**
```
Endpoint: /video/stream/{name}
Type: GET
Path Param: name
Response: byte[]
Response Content-Type: video/mp4
```

**Search with name**
```
Endpoint: /video/search
Type: GET
Param: name
Response Body: -- Same as List files API --
```


## Checklist of tasks.
- [x] Authentication - Signup and Signin (without OTP Email Verification)
- [x] Video upload with title input
- [x] Video list with pagination
- [x] Video search (based on title)
- [x] Api documentation
- [x] Deployment
- [x] Email verification OTP flow for Signup 

**NOTE**: Regarding email verification, currently application uses a naive token generation (current-timestamp). The idea behind design is to indicate that this can be easily swapped with a secure one. EDIT: *The initial choice of using currentTimestamp is definitely not an ideal one even for prototyping (Two users may, with a very high probability get colliding tokens), changed it to Java util UUID (which is thread-safe) yet not the best choice as the expiration time is eternity.*

