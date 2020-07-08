# Ad-lib

Ad-lib features a React frontend with a Java backend. In order to connect the frontend and backend 
and correctly route which microservice handles request, a dispatch.yaml file has been defined. 
This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## Run the front-end and back-end locally:

### Front-end
```bash
cd frontend
yarn local
```

### Back-end
```bash
cd backend
mvn package appengine:run
```
or <br />
```bash
build
```

## Deploy web-application using dispatch.yaml
```bash
gcloud init
gcloud config set project [Project_ID]
gcloud app deploy dispatch.yaml
```

## Deploy web-application to producation
```bash
gcloud init
gcloud config set project [Project_ID]
```

### React front-end
```bash
cd frontend
yarn build
gcloud app deploy
```

### Java back-end
```bash
cd backend
mvn package appengine:deploy
```