import React, {useEffect} from 'react';
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
  let match;
  const classes = useStyles();
  const matchDataRefreshRateMilliseconds = 30000;
  const defaultPageView = 'form';
  const pageViewKey = 'pageViewState';

  /**
   * Set current page view to state retrieved from local storage or
   * default view if no state is currently saved
   * */
  const [currentPage, setCurrentPage] = React.useState(
      localStorage.getItem(pageViewKey) || defaultPageView,
  );

  // Load page view state from local storage using useEffect hook
  useEffect(() => {
    localStorage.setItem(pageViewKey, currentPage);
  }, [pageViewKey, currentPage]);

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
