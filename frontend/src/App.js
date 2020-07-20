import React from 'react';
import axios from 'axios';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import './App.css';
import MenuBar from './components/MenuBar.js';
import Form from './components/Form.js';

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
  const getMatchDataRefreshRate = 5000;
  const [matchStatus, setMatchStatus] = React.useState("Unmatched");

  /** Initiate GET request to search-match servlet */
  function getMatch() {
    axios.get('/api/v1/search-match')
        .then((response) => {
          console.log(response);
          if (response.status === 200 && response.data.matchStatus === 'true') {
            setMatchStatus("Matched");
            clearInterval(interval);
          } 
        })
        .catch ((error) => {
          console.log(error);
          // Redirect to "Oops, something went wrong page"
          //clearInterval(interval);
        });
    const interval = setInterval(getMatch, getMatchDataRefreshRate);
  }
  
  return (
    <div>
      <MenuBar />
      <div className={classes.centerHorizontal}>
        <Card className={classes.content}>
          <CardContent>
            <h2>Meet fellow Googlers <em>now</em>!</h2>
            <h4>Miss bumping into new faces at the office? Want an easy, fun,
               spontaneous way of meeting Googlers virtually? Now you can!</h4>
            <h4>Ad-lib matches you with a fellow Googler in the queue, notifies
               you through email when you’ve been matched, and adds an event to
               your Calendar with a Meet link for you to join immediately! It
               also provides a starter question to get the conversation
               flowing!</h4>
          </CardContent>
        </Card>
        <Card className={classes.content}>
          <Form 
            onSubmit={getMatch}
          />
        </Card>
      </div>
      <p id="match-status">{matchStatus}</p>
    </div>
  );
}
