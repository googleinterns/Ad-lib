import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';

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
 * Add LoadingPage components to UI
 * @return {LoadingPage} LoadingPage component
 */
export default function LoadingPage() {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>Finding you a match...</h3>
          <p><em>This part may take some time as we wait for more Googlers
              to enter the queue.</em></p>
          <p>While you’re waiting on this page, you can continue working,
              learn about <a href="https://guidetoallyship.com/">Allyship</a>,
              or even make a smoothie! We will send you an <b>email </b>
              and <b>Calendar invite</b> as soon as we find you a match!</p>
          <Button variant="contained" color="primary">Exit Queue</Button>   
        </CardContent>
      </Card>
    </div>
  );
}