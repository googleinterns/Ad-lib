This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

Ad-lib features a React frontend with a Java backend. In order to connect the frontend and backend and correctly route which microservice handles request, a dispatch.yaml file has been defined. 

## Run the front-end and back-end locally:

### Front-end
cd frontend <br />
yarn local

### Back-end
cd backend <br />
mvn appengine:run <br />
or <br />
build

## Deploy web-application using dispatch.yaml
gcloud init <br />
gcloud config set project [Project_ID]
gcloud app deploy dispatch.yaml

## Deploy web-application to producation
gcloud init <br />
gcloud config set project [Project_ID]

### React front-end
cd frontend <br />
yarn build <br />
gcloud app deploy

### Java back-end
cd backend <br />
mvn appengine:deploy