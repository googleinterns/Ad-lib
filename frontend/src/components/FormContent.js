import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

/**
 * Establishes style to use on rendering components
 */
const useStyles = makeStyles((theme) => ({
  content: {
    margin: theme.spacing(2),
    width: 800,
  },
}));
/**
 * Define FormContent component
 * @return {FormContent} FormContent component
 */
export default function FormContent() {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>Meet fellow Googlers <em>now</em>!</h3>
          <p>Miss bumping into new faces at the office? Want an easy, fun,
            spontaneous way of meeting Googlers virtually?
            Now you can!</p>
          <p>Ad-lib matches you with a fellow Googler in the queue,
            notifies you through email when you’ve been matched, and adds
            an event to your Calendar with a Meet link for you to join
            immediately! It also provides a starter question to get the
            conversation flowing!</p>
        </CardContent>
      </Card>
    </div>
  );
}