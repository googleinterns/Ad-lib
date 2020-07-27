// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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

/** Initiate GET request to search-match servlet */
export async function fetchMatch() {
  try {
    const response = await axios.get('/api/v1/search-match');
    return response.data;
  } catch (error) {
    console.log('Error', error);
    return null;
  }
}

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

  /** Parse servlet response and update page view */
  function parseServletResponseAndUpdateUI() {
    setCurrentPage('loading');
    fetchMatch().then((response) => {
      console.log(response);
      if (response === null) {
        setCurrentPage('error');
        clearInterval(interval);
      } else if (response.matchStatus === 'true') {
        match = response;
        setCurrentPage('match');
        clearInterval(interval);
      }
    });
    const interval = setInterval(parseServletResponseAndUpdateUI,
        matchDataRefreshRateMilliseconds);
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
                onSubmit={parseServletResponseAndUpdateUI}
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
