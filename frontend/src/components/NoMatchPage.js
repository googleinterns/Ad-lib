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
 * Add NoMatchPage components to UI
 * @return {NoMatchPage} NoMatchPage component
 */
export default function NoMatchPage(props) {
  const classes = useStyles();
  return (
    <div>
      <Card className={classes.content}>
        <CardContent>
          <h3>We could not find you a match!</h3>
          <p>It looks like you are only free until 11:45am, and we could not
             find you a match to meet or 30 minutes before then. Please try
             again later, and happy working!</p>
        </CardContent>
      </Card>
    </div>
  );
}