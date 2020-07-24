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
 * Define MatchPage component
 * @return {MatchPage} MatchPage component
 */
export default function MatchPage(props) {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>We found you a match!</h3>
          <p>This Googler is so excited to meet you.</p>
          <p>Please check your Calendar to find the event and Meet link and
          join to meet your new friend</p>
        </CardContent>
      </Card>
    </div>
  );
}