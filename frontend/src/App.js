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
  const matchDataRefreshRate = 5000;
  const [matchStatus, setMatchStatus] = React.useState('Unmatched');

  /** Initiate GET request to search-match servlet */
  function getMatch() {
    axios.get('/api/v1/search-match')
        .then((response) => {
          console.log(response);
          if (response.status === 200 && response.data.matchStatus === 'true') {
            setMatchStatus('Matched');
            clearInterval(interval);
          }
        })
        .catch((error) => {
          // TO-DO(#76): Add "Oops, something went wrong" page view
          console.log(error);
          alert('Oops, something went wrong. Please try again later');
        });
    const interval = setInterval(getMatch, matchDataRefreshRate);
  }

  return (
    <div>
      <MenuBar />
      <div className={classes.centerHorizontal}>
        <Card className={classes.content}>
          <CardContent>
            <h3>Meet fellow Googlers <em>now</em>!</h3>
            <p>Miss bumping into new faces at the office? Want an easy, fun,
               spontaneous way of meeting Googlers virtually? Now you can!</p>
            <p>Ad-lib matches you with a fellow Googler in the queue, notifies
               you through email when you’ve been matched, and adds an event to
               your Calendar with a Meet link for you to join immediately! It
               also provides a starter question to get the conversation
               flowing!</p>
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
