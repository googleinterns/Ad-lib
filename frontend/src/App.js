import React from 'react';
import axios from 'axios';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import './App.css';
import MenuBar from './components/MenuBar';
import Form from './components/Form';
import LoadingPage from './components/LoadingPage';
import MatchPage from './components/MatchPage';
import NoMatchPage from './components/NoMatchPage';
import ErrorPage from './components/ErrorPage';
import FormContent from './components/FormContent';

/**
 * Establishes style to use on rendering components
 */
const useStyles = makeStyles((theme) => ({
  centerHorizontal: {
    position: 'absolute', left: '50%',
    transform: 'translate(-50%)',
  },
  content: {
    margin: theme.spacing(2),
    width: 800,
  },
}));

/**
 * Add components and content to UI
 * @return {App} App component
 */
export default function App() {
  const classes = useStyles();
  const matchDataRefreshRateMilliseconds = 30000;
  const [currentPage, setCurrentPage] = React.useState('form');
  let match;
  
  /** Initiate GET request to search-match servlet */
  function getMatch() {
    setCurrentPage('loading');
    axios.get('/api/v1/search-match')
        .then((response) => {
          console.log(response);
          if (response.status === 200 && response.data.matchStatus === 'true') {
            match = response;
            setCurrentPage('match');
            clearInterval(interval);
          }
        })
        .catch((error) => {
          // TO-DO(#76): Add 'Oops, something went wrong' page view
          console.log(error);
          setCurrentPage('error');
          clearInterval(interval);
        });
    const interval = setInterval(getMatch, matchDataRefreshRateMilliseconds);
  }

  switch (currentPage) {
    case 'form':
      return (
        <div>
          <MenuBar />
          <div className={classes.centerHorizontal}>
            <FormContent />
            <Card className={classes.content}>
              <Form
                onSubmit={getMatch}
              />
            </Card>
          </div>
        </div>
      );
    case 'loading':
      return (
        <div>
          <MenuBar />
          <div className={classes.centerHorizontal}>
            <LoadingPage />
          </div>
        </div>
      );
    case 'match':
      return (
        <div>
          <MenuBar />
          <div className={classes.centerHorizontal}>
            <MatchPage matchInformation={match}/>
          </div>
        </div>
      );
    case 'no-match':
      return (
        <div>
          <MenuBar />
          <div className={classes.centerHorizontal}>
            <NoMatchPage matchInformation={match}/>
          </div>
        </div>
      );
    case 'error':
      return (
        <div>
          <MenuBar />
          <div className={classes.centerHorizontal}>
            <ErrorPage />
          </div>
        </div>
      );
    default:
      break;
  }
}
